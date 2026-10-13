package org.firstinspires.ftc.teamcode.course.p5;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.course.mechanisms.Shooter;
import org.firstinspires.ftc.teamcode.course.p2.DecodePoses;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;
import org.firstinspires.ftc.teamcode.course.util.ColorUtil;
import org.firstinspires.ftc.teamcode.course.util.InterpLUT;
import org.firstinspires.ftc.teamcode.course.util.ShooterTable;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * PHẦN 5 — DỰ ÁN TỔNG HỢP: Autonomous DECODE đầy đủ cho Shooter Bot (liên minh XANH).
 *
 *  INIT    : đọc motif trên obelisk bằng AprilTag (robot ở START nhìn lên)
 *  1. Pedro chạy tới vị trí bắn, flywheel quay sẵn trên đường đi (tiết kiệm thời gian)
 *  2. Ngắm lại bằng AprilTag (bearing -> 0), đo range -> tra bảng RPM
 *  3. Bắn 3 quả (ghi lại màu từng quả bằng cảm biến màu)
 *  4. Tới vùng nạp góc dưới-trái, bật intake, chờ nạp đủ bóng
 *  5. Quay lại, ngắm, bắn 3 quả nữa      6. Về BASE
 *
 * Đặt Shooter Bot tại START (56, 9) nhìn lên. Bật Config: sân decode648.bmp + GAME = new Decode().
 */
@Autonomous(name = "P5_DecodeFullAuto", group = "P5-DuAn")
public class P5_DecodeFullAuto extends OpMode {
    enum State {TO_SHOOT, AIM, SHOOT, TO_LOAD, LOADING, BACK, AIM_2, SHOOT_2, PARK, DONE}

    private final DecodePoses P = new DecodePoses(false);
    private final PoseFactory pf = PoseFactory.degrees();
    private final Pose LOAD_START = pf.of(36, 14, 180), LOAD_END = pf.of(14, 14, 180);
    private final InterpLUT table = ShooterTable.create();
    private final ElapsedTime timer = new ElapsedTime(), matchTimer = new ElapsedTime();

    private Follower follower;
    private Shooter shooter;
    private SimAprilTagCamera camera;
    private DcMotor intake;
    private ColorSensor ballColor;
    private DistanceSensor ballDistance;

    private Path toShoot, toLoad, loadCreep, back, park;
    private State state = State.TO_SHOOT;
    private Shooter.State lastShooterState = Shooter.State.IDLE;
    private String motif = "?";
    private int lastId = -1, sameCount = 0;
    private final StringBuilder shotColors = new StringBuilder();
    private double doneTime = -1;                 // thời điểm xong toàn bộ (giây)

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        shooter = new Shooter();
        shooter.init(hardwareMap);
        camera = new SimAprilTagCamera();
        intake = hardwareMap.get(DcMotor.class, "intake_motor");
        ballColor = hardwareMap.get(ColorSensor.class, "ball_sensor");
        ballDistance = hardwareMap.get(DistanceSensor.class, "ball_sensor");

        toShoot = Paths.line(P.START, P.SHOOT).linear(P.START, P.SHOOT);
        toLoad = Paths.line(P.SHOOT, LOAD_START).linear(P.SHOOT, LOAD_START);
        loadCreep = Paths.line(LOAD_START, LOAD_END).constant(LOAD_START)
                .with(Constants.foresightConfig.maxPathSpeed.at(0.3));
        back = Paths.line(LOAD_END, P.SHOOT).linear(LOAD_END, P.SHOOT);
        park = Paths.line(P.SHOOT, P.BASE).linear(P.SHOOT, P.BASE);
        follower.setPose(P.START);
    }

    @Override
    public void init_loop() {
        for (SimTagDetection d : camera.getDetections()) {
            if (d.id < 21 || d.id > 23) continue;
            sameCount = (d.id == lastId) ? sameCount + 1 : 1;
            lastId = d.id;
            if (sameCount >= 5) motif = d.id == 21 ? "GPP" : d.id == 22 ? "PGP" : "PPG";
        }
        telemetry.addData("Motif đọc được", motif);
    }

    @Override
    public void start() {
        follower.setPose(P.START);
        follower.follow(toShoot);
        shooter.setTargetRpm(table.get(55));           // quay sẵn với RPM dự đoán cho vị trí bắn
        matchTimer.reset();
    }

    /** Ngắm lại bằng camera; trả về true khi đã sẵn sàng bắn. */
    private boolean aim() {
        SimTagDetection goal = camera.getDetection(SimAprilTagCamera.BLUE_GOAL_ID);
        if (goal != null) {
            shooter.setTargetRpm(table.get(goal.ftcPose.range));
            if (Math.abs(goal.ftcPose.bearing) > 1.0 && timer.seconds() < 1.5) {
                Pose p = follower.pose();
                follower.hold(new Pose(p.x(), p.y(), p.heading() + Math.toRadians(goal.ftcPose.bearing)));
                return false;
            }
        }
        return timer.seconds() > 0.4;                   // đứng yên một chút cho robot ổn định
    }

    @Override
    public void loop() {
        follower.update();
        shooter.update();
        if (shooter.getState() == Shooter.State.PUSH && lastShooterState != Shooter.State.PUSH) {
            shotColors.append(ColorUtil.artifactColor(ballColor, ballDistance));   // màu quả sắp bắn
        }
        lastShooterState = shooter.getState();

        switch (state) {
            case TO_SHOOT:
                if (!follower.isBusy()) { timer.reset(); state = State.AIM; }
                break;
            case AIM:
                if (aim()) { shooter.shoot(3); state = State.SHOOT; }
                break;
            case SHOOT:
                if (!shooter.isBusy()) {
                    shooter.setTargetRpm(0);
                    follower.follow(toLoad);
                    shotColors.append(' ');
                    state = State.TO_LOAD;
                }
                break;
            case TO_LOAD:
                if (!follower.isBusy()) {
                    intake.setPower(1);
                    follower.follow(loadCreep);
                    timer.reset();
                    state = State.LOADING;
                }
                break;
            case LOADING:
                if (follower.isBusy()) timer.reset();                   // đếm giờ khi đã vào hẳn vùng nạp
                else if (timer.seconds() > 1.5) {                       // ~0,4 s / quả => chờ dư cho 3 quả
                    intake.setPower(0);
                    follower.follow(back);
                    shooter.setTargetRpm(table.get(55));
                    state = State.BACK;
                }
                break;
            case BACK:
                if (!follower.isBusy()) { timer.reset(); state = State.AIM_2; }
                break;
            case AIM_2:
                if (aim()) { shooter.shoot(3); state = State.SHOOT_2; }
                break;
            case SHOOT_2:
                if (!shooter.isBusy()) { shooter.stop(); follower.follow(park); state = State.PARK; }
                break;
            case PARK:
                if (!follower.isBusy()) { doneTime = matchTimer.seconds(); state = State.DONE; }
                break;
            case DONE:
                break;
        }
        Pose p = follower.pose();
        telemetry.addData("Trạng thái", "%s  (%.1f s)", state, doneTime > 0 ? doneTime : matchTimer.seconds());
        telemetry.addData("Vị trí", "(%.1f, %.1f, %.0f°)", p.x(), p.y(), AngleUnit.normalizeDegrees(Math.toDegrees(p.heading())));
        telemetry.addData("Flywheel", "%.0f / %.0f RPM", shooter.getRpm(), shooter.getTargetRpm());
        telemetry.addData("Motif", motif);
        telemetry.addData("Màu đã bắn", shotColors.toString());
    }
}
