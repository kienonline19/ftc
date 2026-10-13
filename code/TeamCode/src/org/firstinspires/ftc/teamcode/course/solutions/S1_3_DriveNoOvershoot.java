package org.firstinspires.ftc.teamcode.course.solutions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;

/**
 * LỜI GIẢI Bài tập 1.3 — Đi 24 in mà KHÔNG vượt đích.
 * Hai thay đổi so với P1_DriveDistance:
 *  1) Giới hạn tốc độ theo quãng đường còn lại: v_max = sqrt(2·a·d) (giảm tốc đều trước khi tới đích),
 *     đổi ra công suất bằng hệ số kV ≈ 1 / (tốc độ tối đa).
 *  2) Chỉ kết thúc khi sai số nhỏ VÀ robot gần như đứng yên trong 200 ms ("settled").
 */
@Autonomous(name = "S1_3_DriveNoOvershoot", group = "Z-LoiGiai")
public class S1_3_DriveNoOvershoot extends LinearOpMode {
    static final double TICKS_PER_INCH = 537.6 / (Math.PI * 4.0);
    static final double KP = 0.004, MAX_POWER = 0.6, MIN_POWER = 0.06;
    static final double DECEL_IN_PER_S2 = 30;       // gia tốc hãm "an toàn" (in/s²)
    static final double MAX_SPEED_IN_PER_S = 50;    // tốc độ tối đa ước lượng của khung gầm ở công suất 1,0

    private final MecanumDrive drive = new MecanumDrive();
    private DcMotorEx fl;

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        fl = hardwareMap.get(DcMotorEx.class, "front_left_motor");
        waitForStart();
        driveInches(24);
        sleep(500);
        driveInches(-24);
        while (opModeIsActive()) idle();
    }

    private void driveInches(double inches) {
        drive.resetEncoders();
        double target = inches * TICKS_PER_INCH;
        double[] start = SimTruth.poseInchesDegrees();
        ElapsedTime settled = new ElapsedTime();
        while (opModeIsActive()) {
            double error = target - drive.averageForwardTicks();
            double speedInPerS = Math.abs(fl.getVelocity()) / TICKS_PER_INCH;
            boolean close = Math.abs(error) < 8 && speedInPerS < 1.0;
            if (!close) settled.reset();
            if (settled.milliseconds() > 200) break;

            double remainingIn = Math.abs(error) / TICKS_PER_INCH;
            double vLimit = Math.sqrt(2 * DECEL_IN_PER_S2 * remainingIn);         // tốc độ còn kịp hãm
            double powerLimit = Math.min(MAX_POWER, vLimit / MAX_SPEED_IN_PER_S);
            double power = Math.max(-powerLimit, Math.min(powerLimit, KP * error));
            if (Math.abs(error) >= 8 && Math.abs(power) < MIN_POWER) power = Math.signum(error) * MIN_POWER;
            if (Math.abs(error) < 8) power = 0;
            drive.drive(power, 0, 0);
        }
        drive.stop();
        double[] end = SimTruth.poseInchesDegrees();
        telemetry.addData("Mục tiêu", "%.1f in", inches);
        telemetry.addData("Thật", "%.2f in", Math.hypot(end[0] - start[0], end[1] - start[1]));
        telemetry.update();
    }
}
