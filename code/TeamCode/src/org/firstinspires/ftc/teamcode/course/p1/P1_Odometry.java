package org.firstinspires.ftc.teamcode.course.p1;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;

/**
 * PHẦN 1 — Ví dụ 1.7: Odometry với GoBILDA Pinpoint (chương 21.3 của sách), so với vị trí thật.
 *  - Lái robot bằng gamepad (cần trái: tiến/ngang, cần phải: quay).
 *  - A: đặt lại vị trí Pinpoint = vị trí thật (giống "relocalize" bằng AprilTag ở Phần 3).
 * Chú ý: hệ trục Pinpoint mặc định có gốc tại nơi robot bắt đầu, x = hướng robot nhìn lúc đầu.
 * Ở đây ta setPosition() để Pinpoint dùng HỆ TỌA ĐỘ KHÓA HỌC (gốc góc dưới-trái sân).
 */
@TeleOp(name = "P1_Odometry", group = "P1-MotorSensor")
public class P1_Odometry extends OpMode {
    private final MecanumDrive drive = new MecanumDrive();
    private GoBildaPinpointDriver pinpoint;

    @Override
    public void init() {
        drive.init(hardwareMap);
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.resetPosAndIMU();
        setPinpointToTruth();
    }

    private void setPinpointToTruth() {
        double[] t = SimTruth.poseInchesDegrees();
        pinpoint.setPosition(new Pose2D(DistanceUnit.INCH, t[0], t[1], AngleUnit.DEGREES, t[2]));
    }

    @Override
    public void loop() {
        drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);
        if (gamepad1.aWasPressed()) setPinpointToTruth();

        pinpoint.update();                                   // BẮT BUỘC gọi mỗi vòng lặp!
        Pose2D p = pinpoint.getPosition();
        double[] t = SimTruth.poseInchesDegrees();
        telemetry.addData("Pinpoint", "x=%.1f  y=%.1f  h=%.1f°",
                p.getX(DistanceUnit.INCH), p.getY(DistanceUnit.INCH), p.getHeading(AngleUnit.DEGREES));
        telemetry.addData("Thật    ", "x=%.1f  y=%.1f  h=%.1f°", t[0], t[1], t[2]);
        telemetry.addLine("A = đặt lại Pinpoint theo vị trí thật");
    }
}
