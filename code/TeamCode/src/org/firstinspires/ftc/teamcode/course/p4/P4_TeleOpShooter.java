package org.firstinspires.ftc.teamcode.course.p4;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.course.mechanisms.Shooter;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;
import org.firstinspires.ftc.teamcode.course.util.ColorUtil;
import org.firstinspires.ftc.teamcode.course.util.InterpLUT;
import org.firstinspires.ftc.teamcode.course.util.ShooterTable;

/**
 * PHẦN 4 — Ví dụ 4.5: TeleOp đầy đủ cho Shooter Bot.
 *   cần trái/phải : lái                     LB (giữ)  : tự ngắm goal xanh bằng AprilTag
 *   RT (bóp)      : quay flywheel (RPM tự tra theo khoảng cách camera)
 *   A : bắn 1 quả     B : bắn 3 quả         RB (giữ) : bật intake — chỉ nạp được trong VÙNG NẠP (2 góc dưới sân)
 */
@TeleOp(name = "P4_TeleOpShooter", group = "P4-Flywheel")
public class P4_TeleOpShooter extends OpMode {
    static final double K_TURN = 0.015;
    private final MecanumDrive drive = new MecanumDrive();
    private final Shooter shooter = new Shooter();
    private SimAprilTagCamera camera;
    private final InterpLUT table = ShooterTable.create();
    private DcMotor intake;
    private ColorSensor ballColor;
    private DistanceSensor ballDistance;
    private double lastRange = 60;

    @Override
    public void init() {
        drive.init(hardwareMap);
        shooter.init(hardwareMap);
        camera = new SimAprilTagCamera();
        intake = hardwareMap.get(DcMotor.class, "intake_motor");
        ballColor = hardwareMap.get(ColorSensor.class, "ball_sensor");
        ballDistance = hardwareMap.get(DistanceSensor.class, "ball_sensor");
    }

    @Override
    public void loop() {
        SimTagDetection goal = camera.getDetection(SimAprilTagCamera.BLUE_GOAL_ID);
        if (goal != null) lastRange = goal.ftcPose.range;

        double rotate = gamepad1.right_stick_x * 0.5;
        if (gamepad1.left_bumper && goal != null) rotate = Range.clip(-K_TURN * goal.ftcPose.bearing, -0.3, 0.3);
        drive.drive(-gamepad1.left_stick_y * 0.7, gamepad1.left_stick_x * 0.7, rotate);

        shooter.setTargetRpm(gamepad1.right_trigger > 0.5 ? table.get(lastRange) : 0);
        if (gamepad1.aWasPressed()) shooter.shoot(1);
        if (gamepad1.bWasPressed()) shooter.shoot(3);
        shooter.update();

        intake.setPower(gamepad1.right_bumper ? 1.0 : 0.0);

        telemetry.addData("Goal", goal == null ? "không thấy" : String.format("range %.0f in, bearing %.1f°", goal.ftcPose.range, goal.ftcPose.bearing));
        telemetry.addData("Flywheel", "%.0f / %.0f RPM %s", shooter.getRpm(), shooter.getTargetRpm(), shooter.atSpeed() ? "(SẴN SÀNG)" : "");
        telemetry.addData("Bóng kế tiếp", ColorUtil.artifactColor(ballColor, ballDistance));
        telemetry.addData("Bắn", shooter.getState() + " | đã bắn " + shooter.getShotsFired());
    }
}
