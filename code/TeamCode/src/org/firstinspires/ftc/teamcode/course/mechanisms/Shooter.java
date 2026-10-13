package org.firstinspires.ftc.teamcode.course.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Cơ cấu bắn bóng (flywheel + servo đẩy) cho "Shooter Bot".
 *
 * Điều khiển vận tốc tự viết (chạy trong vòng lặp của OpMode):
 *     power = (kV * vMụcTiêu + kS) * (12 / điệnÁpPin) + kP * (vMụcTiêu - vĐo)
 *       - kV, kS: feedforward ("đoán trước" công suất cần thiết) — đo ở 12 V
 *       - 12/điệnÁpPin: bù điện áp pin
 *       - kP: phản hồi, sửa sai số còn lại và giúp phục hồi nhanh sau mỗi phát bắn
 *
 * Máy trạng thái bắn: IDLE -> CHỜ_ĐỦ_TỐC -> ĐẨY -> RÚT_VỀ -> (lặp lại nếu còn lượt bắn) -> IDLE
 */
public class Shooter {
    public static final double TICKS_PER_REV = 28;      // động cơ 1:1 (6000 RPM)

    // Hệ số điều khiển — xem Phần 4 để biết cách tìm (đo) các giá trị này.
    public static double kV = 0.000382;   // công suất / (tick/s), tại 12 V
    public static double kS = 0.019;      // công suất thắng ma sát, tại 12 V
    public static double kP = 0.004;      // công suất / (tick/s) sai số
    public static double TOLERANCE_RPM = 50;
    public static int STABLE_LOOPS = 3;   // số vòng lặp liên tiếp phải "đủ tốc" trước khi đẩy bóng
    public static double PUSH_MS = 150, RETRACT_MS = 150;
    public static boolean VOLTAGE_COMPENSATION = true;   // nhân feedforward với 12/V

    public enum State {IDLE, WAIT_FOR_SPEED, PUSH, RETRACT}

    private DcMotorEx motor;
    private Servo feeder;
    private VoltageSensor battery;
    private double targetRpm = 0;
    private int shotsQueued = 0, shotsFired = 0, stableCount = 0;
    private State state = State.IDLE;
    private final ElapsedTime timer = new ElapsedTime();
    private double lastPower = 0;

    public void init(HardwareMap hw) {
        motor = hw.get(DcMotorEx.class, "shooter_motor");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);    // ta tự điều khiển vận tốc
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT); // flywheel nên quay trơn khi tắt
        feeder = hw.get(Servo.class, "feeder_servo");
        feeder.setPosition(0);
        battery = hw.voltageSensor.iterator().next();
    }

    public void setTargetRpm(double rpm) {
        targetRpm = Math.max(0, rpm);
    }

    public double getTargetRpm() {
        return targetRpm;
    }

    public double getRpm() {
        return motor.getVelocity() * 60.0 / TICKS_PER_REV;
    }

    public boolean atSpeed() {
        return targetRpm > 0 && Math.abs(getRpm() - targetRpm) <= TOLERANCE_RPM;
    }

    /** Xếp hàng n phát bắn. Mỗi phát chỉ được đẩy khi flywheel đã ổn định ở tốc độ mục tiêu. */
    public void shoot(int n) {
        shotsQueued += n;
    }

    public boolean isBusy() {
        return shotsQueued > 0 || state != State.IDLE;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public State getState() {
        return state;
    }

    public double getLastPower() {
        return lastPower;
    }

    public double getBatteryVoltage() {
        return battery.getVoltage();
    }

    /** Gọi MỖI vòng lặp. */
    public void update() {
        // 1) Điều khiển vận tốc
        double targetTps = targetRpm * TICKS_PER_REV / 60.0;
        double measuredTps = motor.getVelocity();
        double power = 0;
        if (targetTps > 0) {
            double feedforward = kV * targetTps + kS;
            if (VOLTAGE_COMPENSATION) feedforward *= 12.0 / battery.getVoltage();
            double feedback = kP * (targetTps - measuredTps);
            power = Math.max(0, Math.min(1, feedforward + feedback));  // flywheel chỉ quay một chiều
        }
        motor.setPower(power);
        lastPower = power;

        // 2) Máy trạng thái đẩy bóng
        switch (state) {
            case IDLE:
                if (shotsQueued > 0) state = State.WAIT_FOR_SPEED;
                break;
            case WAIT_FOR_SPEED:
                stableCount = atSpeed() ? stableCount + 1 : 0;
                if (stableCount >= STABLE_LOOPS) {
                    feeder.setPosition(1);
                    timer.reset();
                    state = State.PUSH;
                }
                break;
            case PUSH:
                if (timer.milliseconds() >= PUSH_MS) {
                    feeder.setPosition(0);
                    timer.reset();
                    state = State.RETRACT;
                }
                break;
            case RETRACT:
                if (timer.milliseconds() >= RETRACT_MS) {
                    shotsQueued--;
                    shotsFired++;
                    stableCount = 0;
                    state = shotsQueued > 0 ? State.WAIT_FOR_SPEED : State.IDLE;
                }
                break;
        }
    }

    public void stop() {
        targetRpm = 0;
        shotsQueued = 0;
        state = State.IDLE;
        motor.setPower(0);
        feeder.setPosition(0);
    }
}
