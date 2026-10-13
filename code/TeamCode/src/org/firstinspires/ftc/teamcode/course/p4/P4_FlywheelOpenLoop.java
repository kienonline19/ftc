package org.firstinspires.ftc.teamcode.course.p4;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * PHẦN 4 — Ví dụ 4.1: Flywheel ĐIỀU KHIỂN HỞ (open loop) — chỉ đặt công suất, không đo tốc độ để sửa.
 * Cấu hình: Shooter Bot.
 *   dpad lên/xuống : công suất +0,05 / −0,05         A : đẩy 1 quả bóng
 *   X              : đổi BRAKE / FLOAT khi tắt máy   B : bật/tắt flywheel
 * Thí nghiệm: chạy cùng một công suất (0,40) nhiều lần (STOP rồi INIT lại) — mỗi lần pin một điện áp khác
 * => RPM khác nhau => bóng bay xa/gần khác nhau. Đó là lý do cần điều khiển VÒNG KÍN.
 */
@TeleOp(name = "P4_FlywheelOpenLoop", group = "P4-Flywheel")
public class P4_FlywheelOpenLoop extends OpMode {
    private DcMotorEx flywheel;
    private Servo feeder;
    private VoltageSensor battery;
    private double power = 0.40;
    private boolean on = true, brake = false;
    private final ElapsedTime feedTimer = new ElapsedTime();
    private boolean feeding = false;

    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "shooter_motor");
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        feeder = hardwareMap.get(Servo.class, "feeder_servo");
        feeder.setPosition(0);
        battery = hardwareMap.voltageSensor.iterator().next();
    }

    @Override
    public void loop() {
        if (gamepad1.dpadUpWasPressed()) { power = Math.min(1, power + 0.05); on = true; }
        if (gamepad1.dpadDownWasPressed()) power = Math.max(0, power - 0.05);
        if (gamepad1.bWasPressed()) on = !on;
        if (gamepad1.xWasPressed()) {
            brake = !brake;
            flywheel.setZeroPowerBehavior(brake ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT);
        }
        flywheel.setPower(on ? power : 0);

        // Đẩy bóng: servo lên 1 trong 150 ms rồi về 0 (KHÔNG dùng sleep() trong OpMode lặp!)
        if (gamepad1.aWasPressed() && !feeding) { feeder.setPosition(1); feedTimer.reset(); feeding = true; }
        if (feeding && feedTimer.milliseconds() > 150) { feeder.setPosition(0); feeding = false; }

        double tps = flywheel.getVelocity();
        double rpm = tps * 60 / 28.0;
        double predicted = power * 6000 * battery.getVoltage() / 12.0;   // ước lượng thô, bỏ qua ma sát
        telemetry.addData("Công suất", "%.2f %s", power, on ? "(BẬT)" : "(TẮT)");
        telemetry.addData("Điện áp pin", "%.2f V", battery.getVoltage());
        telemetry.addData("Tốc độ đo", "%.0f tick/s = %.0f RPM", tps, rpm);
        telemetry.addData("Ước lượng thô", "%.0f RPM (công suất × 6000 × V/12)", on ? predicted : 0);
        telemetry.addData("Khi tắt", brake ? "BRAKE" : "FLOAT");
    }
}
