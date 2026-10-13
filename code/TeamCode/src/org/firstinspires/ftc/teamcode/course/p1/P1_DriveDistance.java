package org.firstinspires.ftc.teamcode.course.p1;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;

/**
 * PHẦN 1 — Ví dụ 1.3: Đi thẳng đúng 24 inch bằng encoder (cấu hình MecDynamic Bot hoặc Shooter Bot).
 *
 *  Đổi inch -> tick:  tick/inch = tick/vòng / (π × đường kính bánh)
 *  Bánh 4 in, motor goBILDA 19,2:1 (537,6 tick/vòng)  =>  537,6 / (π × 4) ≈ 42,8 tick/inch
 *
 *  Điều khiển P: công suất = kP × (số tick còn lại), kẹp trong [MIN_POWER, MAX_POWER]
 *  Telemetry so sánh quãng đường theo ENCODER với quãng đường THẬT (SimTruth, chỉ có trong mô phỏng).
 */
@Autonomous(name = "P1_DriveDistance", group = "P1-MotorSensor")
public class P1_DriveDistance extends LinearOpMode {
    static final double TICKS_PER_REV = 537.6;
    static final double WHEEL_DIAMETER_IN = 4.0;
    static final double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_IN);
    static final double KP = 0.003, MIN_POWER = 0.08, MAX_POWER = 0.6, TOLERANCE_TICKS = 10;

    private final MecanumDrive drive = new MecanumDrive();

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        telemetry.addData("tick/inch", "%.2f", TICKS_PER_INCH);
        telemetry.update();
        waitForStart();

        driveInches(24);
        sleep(700);
        driveInches(-24);
        while (opModeIsActive()) idle();
    }

    private void driveInches(double inches) {
        drive.resetEncoders();
        double target = inches * TICKS_PER_INCH;
        double[] start = SimTruth.poseInchesDegrees();
        while (opModeIsActive()) {
            double error = target - drive.averageForwardTicks();
            if (Math.abs(error) < TOLERANCE_TICKS) break;
            double power = KP * error;
            power = Math.signum(power) * Math.max(MIN_POWER, Math.min(MAX_POWER, Math.abs(power)));
            drive.drive(power, 0, 0);

            double[] now = SimTruth.poseInchesDegrees();
            telemetry.addData("Mục tiêu", "%.0f tick (%.1f in)", target, inches);
            telemetry.addData("Encoder", "%.0f tick = %.2f in", drive.averageForwardTicks(), drive.averageForwardTicks() / TICKS_PER_INCH);
            telemetry.addData("Quãng đường THẬT", "%.2f in", Math.hypot(now[0] - start[0], now[1] - start[1]));
            telemetry.addData("Công suất", "%.2f", power);
            telemetry.update();
        }
        drive.stop();
        sleep(300);
        double[] end = SimTruth.poseInchesDegrees();
        telemetry.addData("XONG — encoder", "%.2f in", drive.averageForwardTicks() / TICKS_PER_INCH);
        telemetry.addData("XONG — thật", "%.2f in", Math.hypot(end[0] - start[0], end[1] - start[1]));
        telemetry.update();
    }
}
