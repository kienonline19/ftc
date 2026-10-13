package org.firstinspires.ftc.teamcode.course.solutions;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * LỜI GIẢI Bài tập 4.3 — Flywheel điều khiển BANG-BANG có vùng trễ (hysteresis), giống chương 24.3 của sách:
 *   tốc độ < mục tiêu − DEADBAND  => công suất 1,0 (tăng tốc hết cỡ)
 *   tốc độ > mục tiêu + DEADBAND  => công suất 0 (để bánh tự chậm lại — KHÔNG đảo chiều!)
 *   ở giữa                         => giữ nguyên công suất cũ
 * Tự động bắn 3 quả ở giây thứ 2,5 và in CSV (t,target,rpm,power) để so sánh với P4_FlywheelCustom.
 */
@TeleOp(name = "S4_3_BangBang", group = "Z-LoiGiai")
public class S4_3_BangBang extends OpMode {
    static final double TARGET_RPM = 2200, DEADBAND_RPM = 20;
    private DcMotorEx flywheel;
    private Servo feeder;
    private double power = 0;
    private final ElapsedTime clock = new ElapsedTime();
    private double lastLog = 0;
    private int shots = 0;
    private boolean pushing = false;
    private double phaseStart = 0;

    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "shooter_motor");
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        feeder = hardwareMap.get(Servo.class, "feeder_servo");
        feeder.setPosition(0);
        System.out.println("t,target,rpm,power");
    }

    @Override
    public void start() {
        clock.reset();
    }

    @Override
    public void loop() {
        double rpm = flywheel.getVelocity() * 60 / 28.0;
        if (rpm < TARGET_RPM - DEADBAND_RPM) power = 1.0;
        else if (rpm > TARGET_RPM + DEADBAND_RPM) power = 0.0;
        flywheel.setPower(power);

        double t = clock.seconds();       // bắn 3 quả, mỗi quả khi đã đủ tốc
        if (t > 2.5 && shots < 3) {
            boolean ready = Math.abs(rpm - TARGET_RPM) < 50;
            if (!pushing && ready && t - phaseStart > 0.15) { feeder.setPosition(1); pushing = true; phaseStart = t; }
            else if (pushing && t - phaseStart > 0.15) { feeder.setPosition(0); pushing = false; phaseStart = t; shots++; }
        }
        if (t - lastLog >= 0.02) {
            lastLog = t;
            System.out.printf("%.2f,%.0f,%.0f,%.2f%n", t, TARGET_RPM, rpm, power);
        }
        telemetry.addData("RPM", "%.0f / %.0f", rpm, TARGET_RPM);
        telemetry.addData("Công suất", power);
        telemetry.addData("Đã bắn", shots);
    }
}
