package org.firstinspires.ftc.teamcode.course.solutions;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.config.Modifier;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * LỜI GIẢI Bài tập 2.6 — Hình vuông có góc "sắc".
 * Mặc định ForesightConfig.pathSkip = true: khi robot bắt đầu phải phanh ở cuối một đoạn, follower NHẢY SANG đoạn kế tiếp
 * để giữ đà => góc bị bo tròn. Tắt pathSkip CHỈ cho đường này bằng một Modifier: pathSkip.at(false).
 * Đặt robot tại (48, 48) nhìn lên (90°).
 */
@Autonomous(name = "S2_6_SharpSquare", group = "Z-LoiGiai")
public class S2_6_SharpSquare extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        PoseFactory pf = PoseFactory.degrees();
        Pose a = pf.of(48, 48, 90), b = pf.of(96, 48, 90), c = pf.of(96, 96, 90), d = pf.of(48, 96, 90);
        double h = Math.toRadians(90);
        Modifier noSkip = Constants.foresightConfig.pathSkip.at(false);   // chỉ có hiệu lực khi đang chạy đường này

        Path square = Paths.path(
                Paths.line(a, b).constant(h),
                Paths.line(b, c).constant(h),
                Paths.line(c, d).constant(h),
                Paths.line(d, a).constant(h)).with(noSkip);

        Follower follower = Constants.create(hardwareMap);
        follower.setPose(a);
        waitForStart();
        follower.setPose(a);
        follower.follow(square);
        while (opModeIsActive()) {
            follower.update();
            telemetry.addData("Đoạn", follower.pathIndex());
            telemetry.addData("Đang chạy", follower.isBusy());
            telemetry.update();
        }
    }
}
