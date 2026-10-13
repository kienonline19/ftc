package org.firstinspires.ftc.teamcode.course.p1;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/**
 * PHẦN 1 — Ví dụ 1.1: Motor và encoder (cấu hình "Programming Board").
 *
 * Điều khiển:
 *   cần trái (lên/xuống) : công suất motor (RUN_USING_ENCODER)
 *   A                    : reset encoder về 0
 *   B                    : đổi chế độ khi công suất = 0 (BRAKE <-> FLOAT)
 *   dpad lên / xuống     : RUN_TO_POSITION quay thêm +1 / -1 vòng
 * Kéo thanh "Motor Inertia" sang phải để motor có quán tính, rồi so sánh BRAKE và FLOAT.
 */
@TeleOp(name = "P1_MotorEncoder", group = "P1-MotorSensor")
public class P1_MotorEncoder extends OpMode {
    private DcMotorEx motor;
    private double ticksPerRev;
    private boolean brake = true;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        ticksPerRev = motor.getMotorType().getTicksPerRev();     // NeveRest 40: 1120 tick/vòng
        telemetry.addData("Tick mỗi vòng", ticksPerRev);
    }

    @Override
    public void loop() {
        if (gamepad1.aWasPressed()) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        if (gamepad1.bWasPressed()) {
            brake = !brake;
            motor.setZeroPowerBehavior(brake ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT);
        }
        if (gamepad1.dpadUpWasPressed() || gamepad1.dpadDownWasPressed()) {
            int delta = (int) Math.round(gamepad1.dpad_up ? ticksPerRev : -ticksPerRev);
            motor.setTargetPosition(motor.getCurrentPosition() + delta);   // PHẢI đặt mục tiêu trước...
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);                // ...rồi mới đổi chế độ
            motor.setPower(0.5);                                           // công suất tối đa khi chạy tới đích
        }

        if (motor.getMode() == DcMotor.RunMode.RUN_TO_POSITION) {
            if (!motor.isBusy()) motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);  // tới đích => trả lại cần điều khiển
        } else {
            motor.setPower(-gamepad1.left_stick_y);   // đẩy cần lên => left_stick_y âm => công suất dương
        }

        int ticks = motor.getCurrentPosition();
        double rotations = ticks / ticksPerRev;
        double ticksPerSecond = motor.getVelocity();
        double rpm = ticksPerSecond / ticksPerRev * 60.0;

        telemetry.addData("Chế độ", motor.getMode());
        telemetry.addData("Khi công suất = 0", brake ? "BRAKE (phanh)" : "FLOAT (thả trôi)");
        telemetry.addData("Công suất", "%.2f", motor.getPower());
        telemetry.addData("Encoder", "%d tick = %.2f vòng = %.0f độ", ticks, rotations, rotations * 360);
        telemetry.addData("Vận tốc", "%.0f tick/s = %.1f RPM", ticksPerSecond, rpm);
    }
}
