package org.firstinspires.ftc.teamcode.course.p3;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;

/**
 * PHẦN 3 — Ví dụ 3.3: Ngắm và tiến tới thẻ AprilTag (giống mẫu RobotAutoDriveToAprilTagOmni của FTC SDK).
 *
 *   giữ LB : chỉ NGẮM — quay cho bearing -> 0 (robot nhìn thẳng vào thẻ goal)
 *   giữ A  : NGẮM + TIẾN + CĂN GIỮA — dừng cách thẻ DESIRED_RANGE inch, nhìn thẳng vào mặt thẻ
 *   thả nút: lái tay bằng gamepad
 *
 * Quy ước của MecanumDrive (sách): forward > 0 tiến, right > 0 sang phải, rotate > 0 quay phải (thuận kim đồng hồ).
 *   bearing > 0 (thẻ lệch TRÁI)            => cần quay trái   => rotate = −K_TURN × bearing
 *   range > DESIRED (còn xa)              => cần tiến         => forward = K_DRIVE × (range − DESIRED)
 *   yaw > 0 (robot lệch TRÁI đường pháp tuyến của thẻ) => cần sang phải => right = K_STRAFE × yaw
 */
@TeleOp(name = "P3_AimAndApproach", group = "P3-AprilTag")
public class P3_AimAndApproach extends OpMode {
    static final int TARGET_ID = SimAprilTagCamera.BLUE_GOAL_ID;
    static final double DESIRED_RANGE = 50;     // gần hơn ~42 in thẻ (cao 29,5 in) sẽ ra khỏi khung hình dọc của camera
    static final double K_DRIVE = 0.03, K_TURN = 0.015, K_STRAFE = 0.02;
    static final double MAX_DRIVE = 0.5, MAX_TURN = 0.3, MAX_STRAFE = 0.4;

    private final MecanumDrive drive = new MecanumDrive();
    private SimAprilTagCamera camera;

    @Override
    public void init() {
        drive.init(hardwareMap);
        camera = new SimAprilTagCamera();
    }

    @Override
    public void loop() {
        SimTagDetection tag = camera.getDetection(TARGET_ID);
        double forward = -gamepad1.left_stick_y * 0.6, right = gamepad1.left_stick_x * 0.6, rotate = gamepad1.right_stick_x * 0.4;

        if (tag != null && (gamepad1.left_bumper || gamepad1.a)) {
            rotate = Range.clip(-K_TURN * tag.ftcPose.bearing, -MAX_TURN, MAX_TURN);
            if (gamepad1.a) {
                forward = Range.clip(K_DRIVE * (tag.ftcPose.range - DESIRED_RANGE), -MAX_DRIVE, MAX_DRIVE);
                right = Range.clip(K_STRAFE * tag.ftcPose.yaw, -MAX_STRAFE, MAX_STRAFE);
            }
        }
        drive.drive(forward, right, rotate);

        if (tag == null) telemetry.addData("Thẻ " + TARGET_ID, "KHÔNG THẤY — hãy quay robot về phía goal xanh");
        else {
            telemetry.addData("range", "%.1f in (mục tiêu %.0f)", tag.ftcPose.range, DESIRED_RANGE);
            telemetry.addData("bearing", "%.1f°", tag.ftcPose.bearing);
            telemetry.addData("yaw", "%.1f°", tag.ftcPose.yaw);
        }
        telemetry.addData("Lệnh", "forward %.2f | right %.2f | rotate %.2f", forward, right, rotate);
    }
}
