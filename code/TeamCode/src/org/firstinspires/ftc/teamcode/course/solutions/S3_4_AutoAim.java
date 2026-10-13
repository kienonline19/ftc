package org.firstinspires.ftc.teamcode.course.solutions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;

/**
 * LỜI GIẢI Bài tập 3.4 — Autonomous tự TÌM rồi NGẮM thẻ goal xanh (ID 20).
 *   TÌM  : chưa thấy thẻ => quay trái chậm.
 *   NGẮM : rotate = −K_TURN × bearing (MecanumDrive: rotate > 0 = quay phải), cộng thêm lực tối thiểu MIN_TURN
 *          để thắng ma sát khi sai số đã nhỏ.
 *   XONG : |bearing| < 1° liên tục 0,3 s.
 * Chống "rớt khung": mất thẻ dưới 0,25 s thì chỉ đứng yên chờ, KHÔNG quay lại chế độ tìm.
 * Thử: đặt robot tại (60, 40) nhìn sang phải (0°) rồi chạy.
 */
@Autonomous(name = "S3_4_AutoAim", group = "Z-LoiGiai")
public class S3_4_AutoAim extends LinearOpMode {
    static final double K_TURN = 0.015, MIN_TURN = 0.03, MAX_TURN = 0.3, SEARCH_TURN = 0.2;
    static final double TOLERANCE_DEG = 1.0, STABLE_SEC = 0.3, LOST_SEC = 0.25;

    @Override
    public void runOpMode() {
        MecanumDrive drive = new MecanumDrive();
        drive.init(hardwareMap);
        SimAprilTagCamera camera = new SimAprilTagCamera();
        ElapsedTime total = new ElapsedTime(), inTolerance = new ElapsedTime(), lastSeen = new ElapsedTime();

        waitForStart();
        total.reset();
        lastSeen.reset();
        boolean done = false;
        String state = "TÌM";

        while (opModeIsActive() && !done) {
            SimTagDetection tag = camera.getDetection(SimAprilTagCamera.BLUE_GOAL_ID);
            if (tag != null) lastSeen.reset();

            if (tag == null && lastSeen.seconds() > LOST_SEC) {          // mất thẻ thật sự => TÌM
                state = "TÌM";
                drive.drive(0, 0, -SEARCH_TURN);                          // âm = quay trái (ngược kim đồng hồ)
                inTolerance.reset();
            } else if (tag == null) {                                     // rớt 1–2 khung hình => đứng chờ
                drive.stop();
            } else {                                                      // NGẮM
                state = "NGẮM";
                double bearing = tag.ftcPose.bearing;
                double rotate = 0;
                if (Math.abs(bearing) > TOLERANCE_DEG) {
                    rotate = Range.clip(-K_TURN * bearing, -MAX_TURN, MAX_TURN);
                    rotate += Math.signum(rotate) * MIN_TURN;
                    inTolerance.reset();
                }
                drive.drive(0, 0, rotate);
                if (inTolerance.seconds() >= STABLE_SEC) done = true;
                telemetry.addData("bearing", "%.2f°", bearing);
            }
            telemetry.addData("Trạng thái", state);
            telemetry.addData("Thời gian", "%.2f s", total.seconds());
            telemetry.update();
        }

        drive.stop();
        double doneTime = total.seconds();
        while (opModeIsActive()) {
            SimTagDetection tag = camera.getDetection(SimAprilTagCamera.BLUE_GOAL_ID);
            telemetry.addData("Trạng thái", "XONG sau %.2f s", doneTime);
            if (tag != null) telemetry.addData("bearing", "%.2f°", tag.ftcPose.bearing);
            telemetry.update();
        }
    }
}
