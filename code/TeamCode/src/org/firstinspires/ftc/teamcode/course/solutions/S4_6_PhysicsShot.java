package org.firstinspires.ftc.teamcode.course.solutions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.course.mechanisms.Shooter;
import org.firstinspires.ftc.teamcode.course.p2.DecodePoses;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;
import org.firstinspires.ftc.teamcode.course.util.ShooterPhysics;
import org.firstinspires.ftc.teamcode.course.util.ShooterTable;
import org.firstinspires.ftc.teamcode.course.util.TagLocalizer;

/**
 * LỜI GIẢI Bài tập 4.6 — Bắn bằng RPM tính từ VẬT LÝ:
 *   camera -> vị trí robot (TagLocalizer) -> điểm bóng rời nòng -> khoảng cách tới tâm goal -> ShooterPhysics.requiredRpm()
 * In ra để so sánh với bảng tra ShooterTable. Đặt Shooter Bot trong vùng bắn, nhìn vào goal xanh.
 */
@Autonomous(name = "S4_6_PhysicsShot", group = "Z-LoiGiai")
public class S4_6_PhysicsShot extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Shooter shooter = new Shooter();
        shooter.init(hardwareMap);
        SimAprilTagCamera camera = new SimAprilTagCamera();
        waitForStart();

        SimTagDetection d = null;
        while (opModeIsActive() && d == null) d = camera.getDetection(SimAprilTagCamera.BLUE_GOAL_ID);
        if (d == null) return;
        double[] pose = TagLocalizer.robotPose(d, 8, 0, 0);
        double h = Math.toRadians(pose[2]);
        double lx = pose[0] + 8 * Math.cos(h), ly = pose[1] + 8 * Math.sin(h);      // điểm bóng rời nòng
        double dist = Math.hypot(DecodePoses.GOAL_X - lx, DecodePoses.GOAL_Y - ly);
        double rpmPhysics = ShooterPhysics.requiredRpm(dist);
        double rpmTable = ShooterTable.create().get(d.ftcPose.range);

        shooter.setTargetRpm(rpmPhysics);
        shooter.shoot(3);
        while (opModeIsActive() && shooter.isBusy()) {
            shooter.update();
            telemetry.addData("Khoảng cách tới tâm goal", "%.1f in", dist);
            telemetry.addData("RPM theo vật lý / theo bảng", "%.0f / %.0f", rpmPhysics, rpmTable);
            telemetry.addData("Flywheel", "%.0f RPM | %s", shooter.getRpm(), shooter.getState());
            telemetry.update();
        }
        shooter.stop();
        while (opModeIsActive()) idle();
    }
}
