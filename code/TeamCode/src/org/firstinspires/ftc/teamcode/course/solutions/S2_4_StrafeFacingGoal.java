package org.firstinspires.ftc.teamcode.course.solutions;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.course.p2.DecodePoses;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * LỜI GIẢI Bài tập 2.4 — Chạy ngang qua vùng bắn mà robot LUÔN nhìn vào goal xanh: heading "facingPoint".
 * Đi từ (48, 100) sang (100, 100) rồi quay lại. Đặt robot ở (48, 100), hướng tùy ý (nên nhìn về goal).
 * So sánh: .facingPoint(GOAL) — hướng đổi liên tục để luôn chĩa vào goal; .constant(...) — hướng cố định.
 */
@Autonomous(name = "S2_4_StrafeFacingGoal", group = "Z-LoiGiai")
public class S2_4_StrafeFacingGoal extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        PoseFactory pf = PoseFactory.degrees();
        Pose goal = pf.of(DecodePoses.GOAL_X, DecodePoses.GOAL_Y, 0);
        Pose left = pf.of(48, 100, DecodePoses.aimHeadingDeg(48, 100));
        Pose right = pf.of(100, 100, DecodePoses.aimHeadingDeg(100, 100));
        Path go = Paths.line(left, right).facingPoint(goal);
        Path back = Paths.line(right, left).facingPoint(goal);

        Follower follower = Constants.create(hardwareMap);
        follower.setPose(left);
        waitForStart();
        follower.setPose(left);
        for (int i = 0; i < 4 && opModeIsActive(); i++) {
            follower.follow(i % 2 == 0 ? go : back);
            do {
                follower.update();
                Pose p = follower.pose();
                double aim = DecodePoses.aimHeadingDeg(p.x(), p.y());
                double err = Math.toDegrees(p.heading()) - aim;
                while (err > 180) err -= 360;
                while (err <= -180) err += 360;
                telemetry.addData("Sai lệch hướng so với goal", "%.1f°", err);
                telemetry.update();
            } while (opModeIsActive() && follower.isBusy());
        }
        while (opModeIsActive()) follower.update();
    }
}
