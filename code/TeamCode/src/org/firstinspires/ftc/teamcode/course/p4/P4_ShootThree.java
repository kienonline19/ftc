package org.firstinspires.ftc.teamcode.course.p4;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.course.mechanisms.Shooter;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;
import org.firstinspires.ftc.teamcode.course.util.InterpLUT;
import org.firstinspires.ftc.teamcode.course.util.ShooterTable;

/**
 * PHẦN 4 — Ví dụ 4.4: Bắn 3 quả từ một vị trí cố định.
 *   1) Đo khoảng cách tới thẻ goal bằng camera (lấy trung bình 10 lần đo).
 *   2) Tra bảng -> RPM cần thiết.  3) Quay flywheel, chờ ổn định.  4) Bắn 3 quả (máy trạng thái trong Shooter).
 * Đặt robot (Shooter Bot) ở đâu đó trong vùng bắn lớn, nhìn thẳng vào goal xanh, ví dụ (60, 86, 136°).
 */
@Autonomous(name = "P4_ShootThree", group = "P4-Flywheel")
public class P4_ShootThree extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Shooter shooter = new Shooter();
        shooter.init(hardwareMap);
        SimAprilTagCamera camera = new SimAprilTagCamera();
        InterpLUT table = ShooterTable.create();
        waitForStart();

        double sum = 0;
        int n = 0;
        ElapsedTime t = new ElapsedTime();
        while (opModeIsActive() && n < 10 && t.seconds() < 2) {
            SimTagDetection d = camera.getDetection(SimAprilTagCamera.BLUE_GOAL_ID);
            if (d != null) { sum += d.ftcPose.range; n++; }
            sleep(30);
        }
        double range = n > 0 ? sum / n : 60;                 // không thấy thẻ -> đoán 60 in
        double rpm = table.get(range);
        shooter.setTargetRpm(rpm);
        shooter.shoot(3);

        while (opModeIsActive() && shooter.isBusy()) {
            shooter.update();
            telemetry.addData("Khoảng cách (camera)", n > 0 ? String.format("%.1f in (%d lần đo)", range, n) : "không thấy thẻ!");
            telemetry.addData("RPM mục tiêu / đo", "%.0f / %.0f", rpm, shooter.getRpm());
            telemetry.addData("Trạng thái", shooter.getState() + " | đã bắn " + shooter.getShotsFired());
            telemetry.update();
        }
        shooter.stop();
        telemetry.addData("XONG", "đã bắn %d quả ở %.0f RPM", shooter.getShotsFired(), rpm);
        telemetry.update();
        while (opModeIsActive()) idle();
    }
}
