package org.firstinspires.ftc.teamcode.course.solutions;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;

/**
 * LỜI GIẢI Bài tập 1.5 — Chạy dọc tường, giữ tường ở bên PHẢI robot cách 12 in, dừng khi còn 10 in tới tường phía trước.
 * Đặt robot (MecDynamic) ở (30, 30) nhìn sang PHẢI (0°): khi đó tường bên phải robot chính là tường dưới (y = 0)
 * và tường phía trước là tường phải (x = 144). (Phía trên sân có goal và ramp chắn đường nên ta chạy dọc cạnh dưới.)
 *  - right_distance đo tới tường bên phải robot -> điều khiển P cho chuyển động NGANG
 *  - front_distance đo tới tường trước -> dừng
 */
@Autonomous(name = "S1_5_WallFollow", group = "Z-LoiGiai")
public class S1_5_WallFollow extends LinearOpMode {
    static final double WALL_IN = 12, KP_SIDE = 0.06, FORWARD = 0.35;

    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive();
        drive.init(hardwareMap);
        DistanceSensor right = hardwareMap.get(DistanceSensor.class, "right_distance");
        DistanceSensor front = hardwareMap.get(DistanceSensor.class, "front_distance");
        waitForStart();

        while (opModeIsActive()) {
            double r = right.getDistance(DistanceUnit.INCH);
            double f = front.getDistance(DistanceUnit.INCH);
            boolean frontValid = f < 300;
            if (frontValid && f < 10) break;
            double side = 0;
            if (r < 300) side = Math.max(-0.3, Math.min(0.3, KP_SIDE * (r - WALL_IN)));  // xa tường => sang phải
            drive.drive(FORWARD, side, 0);
            telemetry.addData("Cách tường phải", r < 300 ? String.format("%.1f in", r) : "ngoài tầm");
            telemetry.addData("Cách tường trước", frontValid ? String.format("%.1f in", f) : "ngoài tầm");
            telemetry.update();
        }
        drive.stop();
        while (opModeIsActive()) idle();
    }
}
