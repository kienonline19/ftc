package org.firstinspires.ftc.teamcode.course.p2;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * PHẦN 2 — Ví dụ 2.3: Autonomous kiểu DECODE bằng MÁY TRẠNG THÁI (enum + switch, chương 12 của sách).
 *
 *   START -> (đi tới vị trí bắn) -> "bắn" 1,5 s -> tới vạch bóng -> chạy chậm qua vạch (hút bóng)
 *         -> quay lại vị trí bắn -> "bắn" 1,5 s -> về BASE
 *
 * Phần 2 chỉ mô phỏng việc bắn bằng thời gian chờ; Phần 5 sẽ ghép flywheel + AprilTag thật sự.
 * Đặt robot tại START (56, 9) nhìn lên trước khi INIT. Bản ĐỎ: P2_DecodeAutoRed (đối xứng qua x = 72).
 */
@Autonomous(name = "P2_DecodeAutoBlue", group = "P2-Pedro")
public class P2_DecodeAutoBlue extends OpMode {
    enum State {TO_SHOOT_1, SHOOT_1, TO_INTAKE, INTAKE, TO_SHOOT_2, SHOOT_2, PARK, DONE}

    protected boolean red = false;
    private DecodePoses P;
    private Follower follower;
    private Path toShoot, toIntake, intakeRun, backToShoot, park;
    private State state;
    private final ElapsedTime timer = new ElapsedTime();
    private final ElapsedTime matchTimer = new ElapsedTime();
    private double doneTime = -1;                 // thời điểm xong toàn bộ kịch bản (giây)

    @Override
    public void init() {
        P = new DecodePoses(red);
        follower = Constants.create(hardwareMap);
        follower.setPose(P.START);

        toShoot = Paths.line(P.START, P.SHOOT).linear(P.START, P.SHOOT);                  // xoay dần 90° -> hướng goal
        toIntake = Paths.line(P.SHOOT, P.INTAKE_START).linear(P.SHOOT, P.INTAKE_START);
        intakeRun = Paths.line(P.INTAKE_START, P.INTAKE_END).constant(P.INTAKE_START)      // giữ hướng 180°
                .with(Constants.foresightConfig.maxPathSpeed.at(0.35));                    // chạy chậm khi hút bóng
        PoseFactory pf = red ? PoseFactory.degrees().mirrorX(72) : PoseFactory.degrees();
        backToShoot = Paths.curve(P.INTAKE_END, pf.of(44, 70, 0), P.SHOOT).linear(P.INTAKE_END, P.SHOOT);
        park = Paths.line(P.SHOOT, P.BASE).linear(P.SHOOT, P.BASE);
    }

    @Override
    public void start() {
        follower.setPose(P.START);
        follower.follow(toShoot);
        state = State.TO_SHOOT_1;
        matchTimer.reset();
    }

    @Override
    public void loop() {
        follower.update();
        switch (state) {
            case TO_SHOOT_1:
                if (!follower.isBusy()) { timer.reset(); state = State.SHOOT_1; }
                break;
            case SHOOT_1:
                if (timer.seconds() > 1.5) { follower.follow(toIntake); state = State.TO_INTAKE; }
                break;
            case TO_INTAKE:
                if (!follower.isBusy()) { follower.follow(intakeRun); state = State.INTAKE; }
                break;
            case INTAKE:
                if (!follower.isBusy()) { follower.follow(backToShoot); state = State.TO_SHOOT_2; }
                break;
            case TO_SHOOT_2:
                if (!follower.isBusy()) { timer.reset(); state = State.SHOOT_2; }
                break;
            case SHOOT_2:
                if (timer.seconds() > 1.5) { follower.follow(park); state = State.PARK; }
                break;
            case PARK:
                if (!follower.isBusy()) { doneTime = matchTimer.seconds(); state = State.DONE; }
                break;
            case DONE:
                break;
        }
        Pose p = follower.pose();
        telemetry.addData("Liên minh", red ? "ĐỎ" : "XANH");
        telemetry.addData("Trạng thái", state);
        if (doneTime > 0) telemetry.addData("Thời gian", "%.1f s (đã xong)", doneTime);
        else telemetry.addData("Thời gian", "%.1f s", matchTimer.seconds());
        telemetry.addData("Vị trí", "(%.1f, %.1f, %.0f°)", p.x(), p.y(), AngleUnit.normalizeDegrees(Math.toDegrees(p.heading())));
    }
}
