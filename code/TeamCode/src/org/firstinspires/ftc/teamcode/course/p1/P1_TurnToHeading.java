package org.firstinspires.ftc.teamcode.course.p1;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;

/**
 * PHẦN 1 — Ví dụ 1.4: Quay tới một góc bằng IMU + điều khiển P.
 *
 *  sai số = góc mục tiêu − góc hiện tại, chuẩn hóa về (−180°, 180°]
 *  IMU: yaw dương = quay NGƯỢC chiều kim đồng hồ (sang trái).
 *  MecanumDrive (theo sách): rotate dương = quay THUẬN chiều kim đồng hồ (sang phải)
 *  => rotate = −kP × sai số
 */
@Autonomous(name = "P1_TurnToHeading", group = "P1-MotorSensor")
public class P1_TurnToHeading extends LinearOpMode {
    static final double KP = 0.02, MIN_TURN = 0.06, MAX_TURN = 0.5, TOLERANCE_DEG = 1.0;
    private final MecanumDrive drive = new MecanumDrive();
    private IMU imu;

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
        imu.resetYaw();                      // hướng lúc bắt đầu = 0°
        waitForStart();

        double[] targets = {90, 180, -90, 0};
        for (double t : targets) {
            turnTo(t);
            sleep(500);
        }
        while (opModeIsActive()) idle();
    }

    private void turnTo(double targetDeg) {
        ElapsedTime settled = new ElapsedTime();
        while (opModeIsActive()) {
            double heading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
            double error = AngleUnit.normalizeDegrees(targetDeg - heading);
            if (Math.abs(error) > TOLERANCE_DEG) settled.reset();
            if (settled.milliseconds() > 250) break;            // nằm trong dung sai đủ 0,25 s
            double turn = KP * error;
            turn = Math.signum(turn) * Math.max(MIN_TURN, Math.min(MAX_TURN, Math.abs(turn)));
            if (Math.abs(error) <= TOLERANCE_DEG) turn = 0;
            drive.drive(0, 0, -turn);                           // dấu trừ: đổi sang quy ước của sách

            telemetry.addData("Mục tiêu", "%.0f°", targetDeg);
            telemetry.addData("Hiện tại", "%.1f°", heading);
            telemetry.addData("Sai số", "%.1f°", error);
            telemetry.update();
        }
        drive.stop();
    }
}
