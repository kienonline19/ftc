package org.firstinspires.ftc.teamcode.course.p2;

import com.pedropathing.api.Paths;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;
import org.firstinspires.ftc.teamcode.pedropathing.tuning.Constants;

/**
 * PHẦN 2 — Ví dụ 2.5: TeleOp dùng Pedro Pathing.
 *   cần trái / phải : lái tay. Pedro dùng quy ước TOÁN HỌC: tiến (+), sang TRÁI (+), quay NGƯỢC chiều kim đồng hồ (+)
 *   X : đổi lái theo hệ ROBOT <-> theo hệ SÂN (field-centric)
 *   Y : tự lái tới vị trí bắn (đường thẳng từ vị trí hiện tại)
 *   A : giữ chặt vị trí hiện tại (hold) — chống bị đẩy
 *   B : trở lại lái tay
 * Vì là TeleOp nên vị trí bắt đầu lấy từ SimTruth (trong trận thật: lấy từ cuối Autonomous hoặc AprilTag).
 */
@TeleOp(name = "P2_TeleOpPedro", group = "P2-Pedro")
public class P2_TeleOpPedro extends OpMode {
    private Follower follower;
    private final DecodePoses P = new DecodePoses(false);
    private boolean fieldCentric = false;

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void start() {
        follower.setPose(SimTruth.pedroPose());      // CHỈ trong mô phỏng
        follower.manual(DrivePowers.zero());          // bắt đầu ở chế độ lái tay
    }

    @Override
    public void loop() {
        follower.update();
        if (gamepad1.xWasPressed()) fieldCentric = !fieldCentric;
        if (gamepad1.yWasPressed()) {
            Pose now = follower.pose();
            follower.follow(Paths.line(now, P.SHOOT).linear(now, P.SHOOT));
        }
        if (gamepad1.aWasPressed()) follower.hold(follower.pose());
        if (gamepad1.bWasPressed()) follower.manual(DrivePowers.zero());

        if (follower.manual()) {      // đang ở chế độ lái tay
            DrivePowers stick = new DrivePowers(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);
            // Lái theo hệ SÂN: "đẩy cần lên" = robot đi về phía +y của sân, bất kể robot đang quay hướng nào.
            // (Người lái đứng ở cạnh dưới sân nhìn lên => +y là "phía trước" của người lái.)
            follower.manual(fieldCentric
                    ? ManualDrive.fieldCentric(stick, follower.pose().heading() - Math.PI / 2)
                    : stick);
        }
        Pose p = follower.pose();
        telemetry.addData("Chế độ", follower.mode() + (fieldCentric ? " (theo hệ sân)" : " (theo hệ robot)"));
        telemetry.addData("Vị trí", "(%.1f, %.1f, %.0f°)", p.x(), p.y(), AngleUnit.normalizeDegrees(Math.toDegrees(p.heading())));
        telemetry.addLine("Y: tới vị trí bắn | A: giữ vị trí | B: lái tay | X: đổi hệ lái");
    }
}
