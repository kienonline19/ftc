package org.firstinspires.ftc.teamcode.course.solutions;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.math.Velocity;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;
import org.firstinspires.ftc.teamcode.course.util.TagLocalizer;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * LỜI GIẢI Bài tập 3.6 — "Lọc bù": hòa trộn vị trí từ camera vào odometry một cách TỪ TỪ.
 *
 *   pose_mới = pose_odometry + K × (pose_camera − pose_odometry)      (0 < K ≤ 1)
 *
 * Chỉ dùng lần đo "tốt": thẻ goal, range ≤ 100 in, robot chạy chậm (< 10 in/s), và camera không lệch odometry
 * quá 24 in (lệch nhiều thế thường là đọc sai). Cập nhật tối đa 10 lần/giây (robot thật: Pinpoint nhận lệnh
 * đặt vị trí qua I²C, không nên gửi mỗi vòng lặp).
 * Giống bài 3.5, Pedro được khởi tạo SAI (+10, −5) in để thấy bộ lọc kéo dần vị trí về đúng. B: bật/tắt lọc.
 */
@TeleOp(name = "S3_6_BlendLocalize", group = "Z-LoiGiai")
public class S3_6_BlendLocalize extends OpMode {
    static final double K_BLEND = 0.3, MAX_RANGE = 100, MAX_SPEED = 10, MAX_JUMP = 24, PERIOD_SEC = 0.1;
    static final double CAM_FWD = 8, CAM_LEFT = 0, CAM_YAW = 0;

    private Follower follower;
    private SimAprilTagCamera camera;
    private final ElapsedTime sinceUpdate = new ElapsedTime();
    private boolean blend = true;
    private int used = 0, rejected = 0;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        camera = new SimAprilTagCamera(CAM_FWD, CAM_LEFT, 10, CAM_YAW);
    }

    @Override
    public void start() {
        Pose truth = SimTruth.pedroPose();
        follower.setPose(new Pose(truth.x() + 10, truth.y() - 5, truth.heading()));   // CỐ TÌNH sai
        follower.manual(DrivePowers.zero());
        sinceUpdate.reset();
    }

    @Override
    public void loop() {
        follower.update();
        follower.manual(new DrivePowers(-gamepad1.left_stick_y * 0.6, -gamepad1.left_stick_x * 0.6,
                -gamepad1.right_stick_x * 0.5));
        if (gamepad1.bWasPressed()) blend = !blend;

        if (blend && sinceUpdate.seconds() >= PERIOD_SEC) {
            Pose p = follower.pose();
            Velocity v = follower.velocity();
            double speed = Math.hypot(v.vx, v.vy);
            SimTagDetection good = null;
            for (SimTagDetection d : camera.getDetections()) {
                boolean isGoal = d.id == SimAprilTagCamera.BLUE_GOAL_ID || d.id == SimAprilTagCamera.RED_GOAL_ID;
                if (!isGoal) continue;
                if (d.ftcPose.range > MAX_RANGE || speed > MAX_SPEED) { rejected++; continue; }
                if (good == null || d.ftcPose.range < good.ftcPose.range) good = d;
            }
            if (good != null) {
                double[] e = TagLocalizer.robotPoseWithHeading(good, Math.toDegrees(p.heading()), CAM_FWD, CAM_LEFT, CAM_YAW);
                if (Math.hypot(e[0] - p.x(), e[1] - p.y()) > MAX_JUMP) {
                    rejected++;                                                     // nghi ngờ đọc sai
                } else {
                    follower.setX(p.x() + K_BLEND * (e[0] - p.x()));
                    follower.setY(p.y() + K_BLEND * (e[1] - p.y()));
                    used++;
                }
            }
            sinceUpdate.reset();
        }

        Pose p = follower.pose();
        double[] t = SimTruth.poseInchesDegrees();
        telemetry.addData("Lọc bù (B)", blend ? "BẬT" : "TẮT");
        telemetry.addData("Pedro", "(%.1f, %.1f)", p.x(), p.y());
        telemetry.addData("Thật ", "(%.1f, %.1f)", t[0], t[1]);
        telemetry.addData("Sai lệch", "%.2f in", Math.hypot(p.x() - t[0], p.y() - t[1]));
        telemetry.addData("Lần đo dùng / bỏ", "%d / %d", used, rejected);
    }
}
