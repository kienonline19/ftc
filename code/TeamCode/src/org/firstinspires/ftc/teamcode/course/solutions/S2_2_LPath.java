package org.firstinspires.ftc.teamcode.course.solutions;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * LỜI GIẢI Bài tập 2.2 — Đường chữ L: (24,24) -> (24,96) -> (96,96), robot luôn nhìn theo hướng đi (tangent).
 * Phương án 1: hai đoạn thẳng ghép lại (góc nhọn: robot phải xoay 90° tại góc).
 * Phương án 2: một đường cong Bézier bậc 2 với điểm điều khiển ở góc (24,96) => góc được bo tròn, chạy mượt hơn.
 * Bấm A trong lúc INIT để chọn phương án 2.  Đặt robot tại (24, 24) nhìn lên (90°).
 */
@Autonomous(name = "S2_2_LPath", group = "Z-LoiGiai")
public class S2_2_LPath extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        PoseFactory pf = PoseFactory.degrees();
        Pose a = pf.of(24, 24, 90), corner = pf.of(24, 96, 0), b = pf.of(96, 96, 0);
        Path twoLines = Paths.path(Paths.line(a, corner).tangent(), Paths.line(corner, b).tangent());
        Path smooth = Paths.curve(a, corner, b).tangent();

        Follower follower = Constants.create(hardwareMap);
        boolean useCurve = false;
        while (opModeInInit()) {
            if (gamepad1.aWasPressed()) useCurve = !useCurve;
            telemetry.addData("Phương án (A để đổi)", useCurve ? "2 - đường cong Bézier" : "1 - hai đoạn thẳng");
            telemetry.update();
        }
        follower.setPose(a);
        follower.follow(useCurve ? smooth : twoLines);
        while (opModeIsActive()) {
            follower.update();
            Pose p = follower.pose();
            telemetry.addData("Vị trí", "(%.1f, %.1f, %.0f°)", p.x(), p.y(), Math.toDegrees(p.heading()));
            telemetry.addData("Đang chạy", follower.isBusy());
            telemetry.update();
        }
    }
}
