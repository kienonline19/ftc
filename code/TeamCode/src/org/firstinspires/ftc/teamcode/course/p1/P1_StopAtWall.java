package org.firstinspires.ftc.teamcode.course.p1;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;

/**
 * PHẦN 1 — Ví dụ 1.5: Chạy tới gần tường rồi dừng cách tường 10 inch (cảm biến khoảng cách "front_distance").
 *
 * Đặc điểm cảm biến khoảng cách trong virtual_robot (giống cảm biến REV 2m):
 *   - chỉ đo được từ 5 cm tới 100 cm; xa hơn trả về 820 cm ("ngoài tầm"); gần hơn trả về 4,9 cm;
 *   - chỉ "thấy" TƯỜNG sân, và chỉ khi robot gần như vuông góc với tường (lệch <= 7°).
 * => Khi chưa vào tầm đo, cho robot chạy tốc độ vừa phải; khi đã vào tầm, dùng điều khiển P.
 */
@Autonomous(name = "P1_StopAtWall", group = "P1-MotorSensor")
public class P1_StopAtWall extends LinearOpMode {
    static final double TARGET_IN = 10, KP = 0.05, MAX_POWER = 0.5, MIN_POWER = 0.07;
    private final MecanumDrive drive = new MecanumDrive();

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        DistanceSensor front = hardwareMap.get(DistanceSensor.class, "front_distance");
        waitForStart();

        while (opModeIsActive()) {
            double d = front.getDistance(DistanceUnit.INCH);
            boolean outOfRange = d > 300;                          // 820 cm = "không thấy gì"
            double error = outOfRange ? 100 : d - TARGET_IN;
            double power = Math.max(-MAX_POWER, Math.min(MAX_POWER, KP * error));
            if (!outOfRange && Math.abs(error) < 0.3) power = 0;
            else if (Math.abs(power) < MIN_POWER) power = Math.signum(power) * MIN_POWER;
            drive.drive(power, 0, 0);

            telemetry.addData("Khoảng cách", outOfRange ? "ngoài tầm đo" : String.format("%.1f in", d));
            telemetry.addData("Công suất", "%.2f", power);
            telemetry.update();
        }
    }
}
