package org.firstinspires.ftc.teamcode.course.solutions;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.course.p2.DecodePoses;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * LỜI GIẢI Bài tập 2.3 — Thêm lượt hút bóng thứ hai (vạch y = 60) vào Autonomous của ví dụ 2.3.
 *
 *   START -> bắn 1 -> hút vạch y = 84 -> bắn 2 -> hút vạch y = 60 -> bắn 3 -> về BASE   (phải xong trước 30 s)
 *
 * Mỗi trạng thái vẫn theo đúng một khuôn: "việc trước đã xong chưa? -> ra lệnh mới + đổi trạng thái".
 * Đặt robot tại START (56, 9) nhìn lên trước khi INIT.
 */
@Autonomous(name = "S2_3_ThreeCycles", group = "Z-LoiGiai")
public class S2_3_ThreeCycles extends OpMode {
    enum State {TO_SHOOT_1, SHOOT_1, TO_INTAKE, INTAKE, TO_SHOOT_2, SHOOT_2,
                TO_INTAKE_2, INTAKE_2, TO_SHOOT_3, SHOOT_3, PARK, DONE}

    private final DecodePoses P = new DecodePoses(false);
    private final PoseFactory pf = PoseFactory.degrees();
    private final Pose INTAKE2_START = pf.of(46, 60, 180), INTAKE2_END = pf.of(20, 60, 180);
    private Follower follower;
    private Path toShoot, toIntake, intakeRun, backToShoot, toIntake2, intakeRun2, back2, park;
    private State state;
    private final ElapsedTime timer = new ElapsedTime(), matchTimer = new ElapsedTime();
    private double doneTime = -1;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(P.START);
        toShoot = Paths.line(P.START, P.SHOOT).linear(P.START, P.SHOOT);
        toIntake = Paths.line(P.SHOOT, P.INTAKE_START).linear(P.SHOOT, P.INTAKE_START);
        intakeRun = Paths.line(P.INTAKE_START, P.INTAKE_END).constant(P.INTAKE_START)
                .with(Constants.foresightConfig.maxPathSpeed.at(0.35));
        backToShoot = Paths.curve(P.INTAKE_END, pf.of(44, 70, 0), P.SHOOT).linear(P.INTAKE_END, P.SHOOT);
        // ----- phần thêm mới cho lượt thứ hai -----
        toIntake2 = Paths.line(P.SHOOT, INTAKE2_START).linear(P.SHOOT, INTAKE2_START);
        intakeRun2 = Paths.line(INTAKE2_START, INTAKE2_END).constant(INTAKE2_START)
                .with(Constants.foresightConfig.maxPathSpeed.at(0.35));
        back2 = Paths.curve(INTAKE2_END, pf.of(44, 60, 0), P.SHOOT).linear(INTAKE2_END, P.SHOOT);
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
            case SHOOT_2:                                   // trước đây: đi park. Bây giờ: tới vạch thứ hai
                if (timer.seconds() > 1.5) { follower.follow(toIntake2); state = State.TO_INTAKE_2; }
                break;
            case TO_INTAKE_2:
                if (!follower.isBusy()) { follower.follow(intakeRun2); state = State.INTAKE_2; }
                break;
            case INTAKE_2:
                if (!follower.isBusy()) { follower.follow(back2); state = State.TO_SHOOT_3; }
                break;
            case TO_SHOOT_3:
                if (!follower.isBusy()) { timer.reset(); state = State.SHOOT_3; }
                break;
            case SHOOT_3:
                if (timer.seconds() > 1.5) { follower.follow(park); state = State.PARK; }
                break;
            case PARK:
                if (!follower.isBusy()) { doneTime = matchTimer.seconds(); state = State.DONE; }
                break;
            case DONE:
                break;
        }
        Pose p = follower.pose();
        telemetry.addData("Trạng thái", state);
        telemetry.addData("Thời gian", "%.1f s", matchTimer.seconds());
        if (doneTime > 0) telemetry.addData("Xong toàn bộ sau", "%.1f s %s", doneTime, doneTime <= 30 ? "(kịp 30 s)" : "(QUÁ 30 s!)");
        telemetry.addData("Vị trí", "(%.1f, %.1f, %.0f°)", p.x(), p.y(), AngleUnit.normalizeDegrees(Math.toDegrees(p.heading())));
    }
}
