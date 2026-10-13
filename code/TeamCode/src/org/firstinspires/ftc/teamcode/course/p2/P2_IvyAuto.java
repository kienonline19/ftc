package org.firstinspires.ftc.teamcode.course.p2;

import com.pedropathing.api.Paths;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.ivy.pedro.PedroCommands;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;

/**
 * PHẦN 2 — Ví dụ 2.4: Cùng kịch bản với P2_DecodeAutoBlue nhưng viết bằng LỆNH (command) của thư viện Ivy.
 *
 * Thay vì tự viết máy trạng thái, ta mô tả "kịch bản" bằng các khối:
 *   sequential(a, b, c)  : chạy lần lượt          parallel(a, b) : chạy đồng thời, xong khi TẤT CẢ xong
 *   instant(() -> ...)   : làm ngay một việc       waitMs(t) / waitUntil(điều kiện) : chờ
 *   PedroCommands.follow(follower, path) : chạy một đường đi (xong khi tới cuối đường — atParametricEnd)
 * Mỗi vòng lặp chỉ cần: follower.update(); Scheduler.execute();
 */
@Autonomous(name = "P2_IvyAuto", group = "P2-Pedro")
public class P2_IvyAuto extends OpMode {
    private Follower follower;
    private DecodePoses P;
    private Servo hand;            // tay gắp có sẵn trên MecDynamic/Shooter Bot, dùng để "minh họa" cơ cấu chạy song song
    private String status = "";

    private Command say(String s) {
        return instant(() -> status = s);
    }

    private Command drive(Path path) {
        return sequential(
                PedroCommands.follow(follower, path),
                waitUntil(() -> !follower.isBusy()));      // chờ robot dừng hẳn (quan trọng trước khi bắn)
    }

    @Override
    public void init() {
        Scheduler.reset();
        P = new DecodePoses(false);
        follower = Constants.create(hardwareMap);
        follower.setPose(P.START);
        hand = hardwareMap.get(Servo.class, "hand_servo");

        Path toShoot = Paths.line(P.START, P.SHOOT).linear(P.START, P.SHOOT);
        Path toIntake = Paths.line(P.SHOOT, P.INTAKE_START).linear(P.SHOOT, P.INTAKE_START);
        Path intakeRun = Paths.line(P.INTAKE_START, P.INTAKE_END).constant(P.INTAKE_START)
                .with(Constants.foresightConfig.maxPathSpeed.at(0.35));
        Path back = Paths.through(P.INTAKE_END, new Pose(44, 70, 0), P.SHOOT).linear(P.INTAKE_END, P.SHOOT);
        Path park = Paths.line(P.SHOOT, P.BASE).linear(P.SHOOT, P.BASE);

        Command auto = sequential(
                say("Đi tới vị trí bắn"),
                drive(toShoot),
                say("Bắn lần 1"), waitMs(1500),
                say("Tới vạch bóng"),
                parallel(drive(toIntake), sequential(waitMs(300), instant(() -> hand.setPosition(1)))),
                say("Hút bóng"),
                drive(intakeRun),
                instant(() -> hand.setPosition(0)),
                say("Quay lại vị trí bắn"),
                drive(back),
                say("Bắn lần 2"), waitMs(1500),
                say("Về base"),
                drive(park),
                say("XONG"));
        Scheduler.schedule(auto);
    }

    @Override
    public void start() {
        follower.setPose(P.START);
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        Pose p = follower.pose();
        telemetry.addData("Đang làm", status);
        telemetry.addData("Vị trí", "(%.1f, %.1f, %.0f°)", p.x(), p.y(), AngleUnit.normalizeDegrees(Math.toDegrees(p.heading())));
    }
}
