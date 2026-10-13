package org.firstinspires.ftc.teamcode.course.p3;

import com.pedropathing.api.Paths;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.course.p2.DecodePoses;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;
import org.firstinspires.ftc.teamcode.course.util.TagLocalizer;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * PHẦN 3 — Ví dụ 3.4: "Định vị lại" (relocalization) bằng AprilTag rồi chạy Pedro tới vị trí bắn.
 *
 * Đặt robot Ở BẤT KỲ ĐÂU trong nửa dưới sân, quay về phía goal xanh (thấy được thẻ 20). Bấm INIT.
 *   - Trong init_loop: ước lượng vị trí từ thẻ, lấy TRUNG BÌNH nhiều lần đo để giảm nhiễu.
 *   - X (trong lúc INIT): bật/tắt dùng AprilTag. Khi TẮT, robot "tưởng" mình đang ở START cố định -> sẽ đi sai!
 *   - START: đặt vị trí cho Pedro rồi đi tới vị trí bắn (60, 86).
 */
@Autonomous(name = "P3_Relocalize", group = "P3-AprilTag")
public class P3_Relocalize extends OpMode {
    static final double CAM_FWD = 8, CAM_LEFT = 0, CAM_YAW = 0;
    private SimAprilTagCamera camera;
    private Follower follower;
    private final DecodePoses P = new DecodePoses(false);
    private boolean useAprilTag = true;
    private double sumX, sumY, sumCos, sumSin;
    private int samples = 0;
    private Pose startEstimate;

    @Override
    public void init() {
        camera = new SimAprilTagCamera(CAM_FWD, CAM_LEFT, 10, CAM_YAW);
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void init_loop() {
        if (gamepad1.xWasPressed()) useAprilTag = !useAprilTag;
        for (SimTagDetection d : camera.getDetections()) {
            if (d.id != SimAprilTagCamera.BLUE_GOAL_ID && d.id != SimAprilTagCamera.RED_GOAL_ID) continue;
            double[] e = TagLocalizer.robotPose(d, CAM_FWD, CAM_LEFT, CAM_YAW);
            sumX += e[0];
            sumY += e[1];
            sumCos += Math.cos(Math.toRadians(e[2]));     // trung bình GÓC phải dùng cos/sin, không cộng thẳng độ!
            sumSin += Math.sin(Math.toRadians(e[2]));
            samples++;
        }
        double[] t = SimTruth.poseInchesDegrees();
        telemetry.addData("Dùng AprilTag (X để đổi)", useAprilTag ? "CÓ" : "KHÔNG (giả sử đang ở START)");
        if (samples > 0) {
            double hx = Math.toDegrees(Math.atan2(sumSin, sumCos));
            telemetry.addData("Ước lượng (" + samples + " lần đo)", "(%.1f, %.1f, %.1f°)", sumX / samples, sumY / samples, hx);
        } else telemetry.addData("Ước lượng", "chưa thấy thẻ goal nào");
        telemetry.addData("Vị trí thật", "(%.1f, %.1f, %.1f°)", t[0], t[1], t[2]);
    }

    @Override
    public void start() {
        if (useAprilTag && samples > 0) {
            startEstimate = new Pose(sumX / samples, sumY / samples, Math.atan2(sumSin, sumCos));
        } else {
            startEstimate = P.START;       // "đoán mò" — robot sẽ đi lệch nếu thật ra không ở START
        }
        follower.setPose(startEstimate);
        follower.follow(Paths.line(startEstimate, P.SHOOT).linear(startEstimate, P.SHOOT));
    }

    @Override
    public void loop() {
        follower.update();
        double[] t = SimTruth.poseInchesDegrees();
        telemetry.addData("Pedro bắt đầu từ", "(%.1f, %.1f)", startEstimate.x(), startEstimate.y());
        telemetry.addData("Mục tiêu", "(%.1f, %.1f)", P.SHOOT.x(), P.SHOOT.y());
        telemetry.addData("Vị trí thật", "(%.1f, %.1f, %.1f°)", t[0], t[1], t[2]);
        telemetry.addData("Sai lệch thật so với mục tiêu", "%.1f in", Math.hypot(t[0] - P.SHOOT.x(), t[1] - P.SHOOT.y()));
    }
}
