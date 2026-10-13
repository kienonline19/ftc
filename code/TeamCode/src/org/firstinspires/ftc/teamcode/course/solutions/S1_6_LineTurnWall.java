package org.firstinspires.ftc.teamcode.course.solutions;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;

/**
 * LỜI GIẢI Bài tập 1.6 — Máy trạng thái (enum + switch) ghép 3 cảm biến:
 *   TÌM_VẠCH (cảm biến màu) -> QUAY_TRÁI_90 (IMU) -> TỚI_TƯỜNG (cảm biến khoảng cách) -> XONG
 * Đặt robot (MecDynamic) ở (96, 56) nhìn lên (90°).
 */
@Autonomous(name = "S1_6_LineTurnWall", group = "Z-LoiGiai")
public class S1_6_LineTurnWall extends OpMode {
    enum State {FIND_LINE, TURN_LEFT, TO_WALL, DONE}

    private final MecanumDrive drive = new MecanumDrive();
    private ColorSensor color;
    private DistanceSensor front;
    private IMU imu;
    private State state = State.FIND_LINE;
    private double background, turnTarget;

    @Override
    public void init() {
        drive.init(hardwareMap);
        color = hardwareMap.get(ColorSensor.class, "color_sensor");
        front = hardwareMap.get(DistanceSensor.class, "front_distance");
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
    }

    @Override
    public void init_loop() {
        background = (color.red() + color.green() + color.blue()) / 3.0;
    }

    @Override
    public void start() {
        state = State.FIND_LINE;
    }

    private double heading() {
        return imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
    }

    @Override
    public void loop() {
        switch (state) {
            case FIND_LINE: {
                double brightness = (color.red() + color.green() + color.blue()) / 3.0;
                if (brightness > background + 15) {
                    drive.stop();
                    turnTarget = AngleUnit.normalizeDegrees(heading() + 90);   // quay TRÁI 90°
                    state = State.TURN_LEFT;
                } else drive.drive(0.25, 0, 0);
                break;
            }
            case TURN_LEFT: {
                double error = AngleUnit.normalizeDegrees(turnTarget - heading());
                if (Math.abs(error) < 1.0) { drive.stop(); state = State.TO_WALL; }
                else drive.drive(0, 0, -Math.signum(error) * Math.max(0.06, Math.min(0.4, Math.abs(0.02 * error))));
                break;
            }
            case TO_WALL: {
                double d = front.getDistance(DistanceUnit.INCH);
                double err = (d > 300 ? 100 : d) - 10;
                if (Math.abs(err) < 0.3) { drive.stop(); state = State.DONE; }
                else drive.drive(Math.max(-0.5, Math.min(0.5, 0.05 * err)), 0, 0);
                break;
            }
            case DONE:
                drive.stop();
                break;
        }
        telemetry.addData("Trạng thái", state);
        telemetry.addData("Hướng", "%.1f°", heading());
    }
}
