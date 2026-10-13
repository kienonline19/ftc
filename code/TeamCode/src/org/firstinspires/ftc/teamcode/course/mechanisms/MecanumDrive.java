package org.firstinspires.ftc.teamcode.course.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Khung gầm mecanum 4 bánh — giống MecanumDrive ở chương 20 sách "Learn Java for FTC".
 * Quy ước của sách: forward > 0 = tiến, right > 0 = đi ngang sang PHẢI, rotate > 0 = quay PHẢI (cùng chiều kim đồng hồ).
 * (Lưu ý: Pedro Pathing và FTC SDK dùng quy ước ngược lại cho ngang/quay: trái và ngược chiều kim đồng hồ là dương.)
 */
public class MecanumDrive {
    private DcMotor frontLeft, frontRight, backLeft, backRight;

    public void init(HardwareMap hw) {
        frontLeft = hw.get(DcMotor.class, "front_left_motor");
        frontRight = hw.get(DcMotor.class, "front_right_motor");
        backLeft = hw.get(DcMotor.class, "back_left_motor");
        backRight = hw.get(DcMotor.class, "back_right_motor");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotor m : new DcMotor[]{frontLeft, frontRight, backLeft, backRight}) {
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    /** Điều khiển theo hệ trục ROBOT. */
    public void drive(double forward, double right, double rotate) {
        double fl = forward + right + rotate;
        double fr = forward - right - rotate;
        double bl = forward - right + rotate;
        double br = forward + right - rotate;
        double max = Math.max(1.0, Math.max(Math.max(Math.abs(fl), Math.abs(fr)), Math.max(Math.abs(bl), Math.abs(br))));
        frontLeft.setPower(fl / max);
        frontRight.setPower(fr / max);
        backLeft.setPower(bl / max);
        backRight.setPower(br / max);
    }

    /**
     * Điều khiển theo hệ trục SÂN (field-oriented): "tiến" luôn là hướng +y của sân, "phải" là hướng +x.
     * @param robotHeadingRad hướng robot (radian) — 0 = robot nhìn theo +x, tăng ngược chiều kim đồng hồ
     */
    public void driveFieldRelative(double fieldForward, double fieldRight, double rotate, double robotHeadingRad) {
        // Vector mong muốn trong hệ sân: (x = fieldRight, y = fieldForward). Đổi sang hệ robot.
        double cos = Math.cos(robotHeadingRad), sin = Math.sin(robotHeadingRad);
        double robotForward = fieldRight * cos + fieldForward * sin;
        double robotLeft = -fieldRight * sin + fieldForward * cos;
        drive(robotForward, -robotLeft, rotate);
    }

    public void stop() {
        drive(0, 0, 0);
    }

    /** Trung bình vị trí encoder 4 bánh (tick) — dùng khi đi thẳng. */
    public double averageForwardTicks() {
        return (frontLeft.getCurrentPosition() + frontRight.getCurrentPosition()
                + backLeft.getCurrentPosition() + backRight.getCurrentPosition()) / 4.0;
    }

    public void resetEncoders() {
        for (DcMotor m : new DcMotor[]{frontLeft, frontRight, backLeft, backRight}) {
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}
