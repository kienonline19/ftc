package org.firstinspires.ftc.teamcode.course.solutions;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.course.p2.DecodePoses;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;
import org.firstinspires.ftc.teamcode.course.util.Pidf;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * LỜI GIẢI Bài tập 2.5 — TeleOp lái theo hệ SÂN, giữ LB để "khóa hướng" vào goal xanh:
 * người lái vẫn điều khiển tiến/lùi/ngang bằng cần trái, còn việc QUAY do bộ điều khiển P đảm nhận.
 *   góc mục tiêu = atan2(goalY − y, goalX − x)
 *   turn = kP × (góc mục tiêu − hướng hiện tại)    (Pedro: turn > 0 = quay ngược chiều kim đồng hồ)
 */
@TeleOp(name = "S2_5_HeadingLockTeleOp", group = "Z-LoiGiai")
public class S2_5_HeadingLockTeleOp extends OpMode {
    private Follower follower;
    private final Pidf headingPid = new Pidf(1.5, 0, 0.05, 0);

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void start() {
        follower.setPose(SimTruth.pedroPose());     // chỉ trong mô phỏng
        follower.manual(DrivePowers.zero());
    }

    @Override
    public void loop() {
        follower.update();
        Pose p = follower.pose();
        double turn = -gamepad1.right_stick_x;
        double errDeg = 0;
        if (gamepad1.left_bumper) {
            double target = Math.atan2(DecodePoses.GOAL_Y - p.y(), DecodePoses.GOAL_X - p.x());
            double err = target - p.heading();
            err = Math.atan2(Math.sin(err), Math.cos(err));          // chuẩn hóa về (−π, π]
            errDeg = Math.toDegrees(err);
            turn = headingPid.calculate(0, -err);                     // = kP·err + kD·d(err)/dt
        }
        DrivePowers stick = new DrivePowers(-gamepad1.left_stick_y, -gamepad1.left_stick_x, turn);
        follower.manual(ManualDrive.fieldCentric(stick, p.heading() - Math.PI / 2));
        telemetry.addData("Khóa hướng (LB)", gamepad1.left_bumper ? "BẬT" : "tắt");
        telemetry.addData("Sai lệch hướng", "%.1f°", errDeg);
    }
}
