package org.firstinspires.ftc.teamcode.course.solutions;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;
import org.firstinspires.ftc.teamcode.course.util.TagLocalizer;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * LỜI GIẢI Bài tập 3.5 — Trong TeleOp, bấm Y để "định vị lại" Pedro bằng thẻ goal.
 * Để thấy tác dụng, OpMode cố tình khởi tạo Pedro SAI 10 in (giống odometry bị trôi sau va chạm).
 * Dùng phiên bản robotPoseWithHeading(): lấy hướng từ odometry (ít nhiễu) + range/bearing từ camera.
 */
@TeleOp(name = "S3_5_RelocalizeTeleOp", group = "Z-LoiGiai")
public class S3_5_RelocalizeTeleOp extends OpMode {
    private Follower follower;
    private SimAprilTagCamera camera;
    private String last = "chưa định vị lại";

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        camera = new SimAprilTagCamera();
    }

    @Override
    public void start() {
        Pose truth = SimTruth.pedroPose();
        follower.setPose(new Pose(truth.x() + 10, truth.y() - 5, truth.heading()));   // CỐ TÌNH sai
        follower.manual(DrivePowers.zero());
    }

    @Override
    public void loop() {
        follower.update();
        follower.manual(new DrivePowers(-gamepad1.left_stick_y * 0.6, -gamepad1.left_stick_x * 0.6, -gamepad1.right_stick_x * 0.5));
        if (gamepad1.yWasPressed()) {
            SimTagDetection d = camera.getDetection(SimAprilTagCamera.BLUE_GOAL_ID);
            if (d == null) d = camera.getDetection(SimAprilTagCamera.RED_GOAL_ID);
            if (d != null) {
                Pose before = follower.pose();
                double[] e = TagLocalizer.robotPoseWithHeading(d, Math.toDegrees(before.heading()), 8, 0, 0);
                follower.setPose(new Pose(e[0], e[1], before.heading()));
                last = String.format("đã sửa %.1f in", Math.hypot(e[0] - before.x(), e[1] - before.y()));
            } else last = "không thấy thẻ goal!";
        }
        Pose p = follower.pose();
        double[] t = SimTruth.poseInchesDegrees();
        telemetry.addData("Pedro", "(%.1f, %.1f)", p.x(), p.y());
        telemetry.addData("Thật ", "(%.1f, %.1f)", t[0], t[1]);
        telemetry.addData("Sai lệch", "%.1f in", Math.hypot(p.x() - t[0], p.y() - t[1]));
        telemetry.addData("Y: định vị lại", last);
    }
}
