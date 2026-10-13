package org.firstinspires.ftc.teamcode.course.p4;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * PHẦN 4 — Ví dụ 4.2: Dùng bộ điều khiển vận tốc CÓ SẴN (RUN_USING_ENCODER + setVelocity).
 * Cấu hình: Shooter Bot.
 *   dpad lên/xuống : RPM mục tiêu ±100         A : đẩy 1 quả bóng (xem tốc độ tụt rồi hồi phục)
 *   X / B / Y      : bộ hệ số PIDF "mặc định" / "theo công thức" / "đã chỉnh"
 *
 * Công thức khởi đầu hay dùng cho REV Hub:  F = 32767 / (tick/s tối đa),  P ≈ 0,1·F,  I ≈ 0,1·P,  D = 0
 * rồi tăng P (và I) cho tới khi phục hồi đủ nhanh mà không dao động. (Trong virtual_robot, bộ PIDF của "Hub"
 * chỉ là mô phỏng gần đúng — trên robot thật phải chỉnh lại.)
 */
@TeleOp(name = "P4_FlywheelVelocity", group = "P4-Flywheel")
public class P4_FlywheelVelocity extends OpMode {
    static final double TICKS_PER_REV = 28, MAX_TPS = 2800;
    static final PIDFCoefficients DEFAULT = new PIDFCoefficients(10, 3, 0, 0);
    static final double F = 32767 / MAX_TPS;                                       // ≈ 11,7
    static final PIDFCoefficients RECIPE = new PIDFCoefficients(0.1 * F, 0.01 * F, 0, F); // P = 0,1F; I = 0,1P
    static final PIDFCoefficients TUNED = new PIDFCoefficients(10, 1, 0, F);

    private DcMotorEx flywheel;
    private Servo feeder;
    private double targetRpm = 2200;
    private String pidfName = "mặc định";
    private final ElapsedTime feedTimer = new ElapsedTime(), recoveryTimer = new ElapsedTime(), runTimer = new ElapsedTime();
    private boolean feeding = false, recovering = false, dropped = false;
    private double minRpmAfterShot = 0, lastRecoveryMs = 0, riseMs = -1;

    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "shooter_motor");
        flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, DEFAULT);
        feeder = hardwareMap.get(Servo.class, "feeder_servo");
        feeder.setPosition(0);
    }

    /** X / B / Y chọn bộ hệ số. Nên chọn TRONG LÚC INIT để mỗi lần thử đều bắt đầu từ flywheel đứng yên. */
    private void handlePidfButtons() {
        if (gamepad1.xWasPressed()) { flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, DEFAULT); pidfName = "mặc định"; }
        if (gamepad1.bWasPressed()) { flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, RECIPE); pidfName = "theo công thức"; }
        if (gamepad1.yWasPressed()) { flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, TUNED); pidfName = "đã chỉnh"; }
    }

    @Override
    public void init_loop() {
        handlePidfButtons();
        telemetry.addData("Bộ hệ số (X/B/Y)", pidfName);
    }

    @Override
    public void start() {
        runTimer.reset();                                   // đo thời gian lên tốc tính từ lúc START
    }

    @Override
    public void loop() {
        if (gamepad1.dpadUpWasPressed()) targetRpm += 100;
        if (gamepad1.dpadDownWasPressed()) targetRpm = Math.max(0, targetRpm - 100);
        handlePidfButtons();

        flywheel.setVelocity(targetRpm * TICKS_PER_REV / 60.0);      // đơn vị: tick/giây
        double rpm = flywheel.getVelocity() * 60.0 / TICKS_PER_REV;

        boolean inBand = Math.abs(rpm - targetRpm) < 0.03 * targetRpm;      // trong ±3% mục tiêu
        if (riseMs < 0 && inBand) riseMs = runTimer.milliseconds();         // lần đầu vào ±3% = thời gian lên tốc

        if (gamepad1.aWasPressed() && !feeding) {
            feeder.setPosition(1); feedTimer.reset(); feeding = true;
            recovering = true; dropped = false; minRpmAfterShot = rpm;
        }
        if (feeding && feedTimer.milliseconds() > 150) { feeder.setPosition(0); feeding = false; }
        if (recovering) {          // hồi phục = từ lúc bóng bay (tốc độ rơi khỏi ±3%) tới lúc về lại ±3%
            minRpmAfterShot = Math.min(minRpmAfterShot, rpm);
            if (!dropped && !inBand) { dropped = true; recoveryTimer.reset(); }
            else if (dropped && inBand) { lastRecoveryMs = recoveryTimer.milliseconds(); recovering = false; }
        }
        PIDFCoefficients c = flywheel.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        telemetry.addData("PIDF (" + pidfName + ")", "P=%.2f I=%.2f D=%.2f F=%.2f", c.p, c.i, c.d, c.f);
        telemetry.addData("Mục tiêu", "%.0f RPM", targetRpm);
        telemetry.addData("Đo được", "%.0f RPM (sai số %.0f)", rpm, targetRpm - rpm);
        telemetry.addData("Lên tốc (từ START tới ±3%)", riseMs < 0 ? "chưa tới" : String.format("%.0f ms", riseMs));
        telemetry.addData("Sau phát bắn gần nhất", "tụt xuống %.0f RPM, hồi phục %.0f ms", minRpmAfterShot, lastRecoveryMs);
    }
}
