package org.firstinspires.ftc.teamcode.course.p2;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * PHẦN 2 — Ví dụ 2.2: Hình vuông 48 in, robot LUÔN nhìn lên (heading không đổi) => phải chạy ngang (strafe).
 * Viết theo kiểu LinearOpMode. Đặt robot tại (48, 48) nhìn lên (90°) trước khi INIT.
 * Một đường đi ghép (compound path) gồm 4 đoạn thẳng: Paths.path(a, b, c, d).
 */
@Autonomous(name = "P2_SquarePath", group = "P2-Pedro")
public class P2_SquarePath extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        PoseFactory pf = PoseFactory.degrees();
        Pose a = pf.of(48, 48, 90), b = pf.of(96, 48, 90), c = pf.of(96, 96, 90), d = pf.of(48, 96, 90);
        double h = Math.toRadians(90);

        Path square = Paths.path(
                Paths.line(a, b).constant(h),
                Paths.line(b, c).constant(h),
                Paths.line(c, d).constant(h),
                Paths.line(d, a).constant(h));

        Follower follower = Constants.create(hardwareMap);
        follower.setPose(a);
        waitForStart();
        follower.setPose(a);

        for (int lap = 1; lap <= 2 && opModeIsActive(); lap++) {
            follower.follow(square);
            do {
                follower.update();
                Pose p = follower.pose();
                double[] t = SimTruth.poseInchesDegrees();
                telemetry.addData("Vòng", lap);
                telemetry.addData("Đoạn đang chạy (pathIndex)", follower.pathIndex());
                telemetry.addData("Pedro", "(%.1f, %.1f, %.0f°)", p.x(), p.y(), Math.toDegrees(p.heading()));
                telemetry.addData("Thật ", "(%.1f, %.1f, %.0f°)", t[0], t[1], t[2]);
                telemetry.update();
            } while (opModeIsActive() && follower.isBusy());
        }
        while (opModeIsActive()) follower.update();          // giữ vị trí cuối
    }
}
