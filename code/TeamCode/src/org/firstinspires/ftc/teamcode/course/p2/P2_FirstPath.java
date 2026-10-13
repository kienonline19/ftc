package org.firstinspires.ftc.teamcode.course.p2;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * PHẦN 2 — Ví dụ 2.1: Đường đi đầu tiên với Pedro Pathing (v3).
 *
 * TRƯỚC KHI BẤM INIT: đặt robot tại START = (24, 24) nhìn LÊN (90°)
 *   -> click trái vào giao điểm ô thảm thứ 1 tính từ góc dưới-trái (mỗi ô 24 in), click phải phía trên robot.
 * Robot đi thẳng lên 48 in, sau đó đi đường cong về chỗ cũ.
 * Dùng cấu hình "MecDynamic Bot" hoặc "Shooter Bot" (hằng số Pedro đã được tinh chỉnh cho khung gầm này).
 */
@Autonomous(name = "P2_FirstPath", group = "P2-Pedro")
public class P2_FirstPath extends OpMode {
    private final PoseFactory pf = PoseFactory.degrees();       // nhập góc bằng ĐỘ cho dễ đọc
    private final Pose START = pf.of(24, 24, 90);
    private final Pose TOP = pf.of(24, 72, 90);
    private final Pose CONTROL = pf.of(60, 48, 0);              // điểm điều khiển của đường cong (heading bị bỏ qua)

    private Follower follower;
    private Path up, back;
    private int step = 0;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        up = Paths.line(START, TOP).constant(START.heading());           // giữ hướng 90° suốt đường
        back = Paths.curve(TOP, CONTROL, START).tangent();               // hướng robot = tiếp tuyến đường cong
        follower.setPose(START);
    }

    @Override
    public void init_loop() {
        double[] t = SimTruth.poseInchesDegrees();
        telemetry.addData("START mong muốn", "(24.0, 24.0, 90°)");
        telemetry.addData("Robot đang ở  ", "(%.1f, %.1f, %.0f°)  <- chỉ trong mô phỏng", t[0], t[1], t[2]);
    }

    @Override
    public void start() {
        follower.setPose(START);
        follower.follow(up);
        step = 1;
    }

    @Override
    public void loop() {
        follower.update();                                   // BẮT BUỘC: gọi mỗi vòng lặp!
        if (step == 1 && !follower.isBusy()) {
            follower.follow(back);
            step = 2;
        } else if (step == 2 && !follower.isBusy()) {
            step = 3;                                        // xong: follower tự giữ (hold) điểm cuối
        }
        Pose p = follower.pose();
        telemetry.addData("Bước", step);
        telemetry.addData("Pedro nghĩ robot ở", "(%.1f, %.1f, %.0f°)", p.x(), p.y(), Math.toDegrees(p.heading()));
        telemetry.addData("Hoàn thành đường", "%.0f %%", 100 * follower.completion());
        telemetry.addData("Chế độ follower", follower.mode());
    }
}
