package org.firstinspires.ftc.teamcode.course.p1;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;

/**
 * PHẦN 1 — Ví dụ 1.6: Tìm vạch trắng trên sân bằng cảm biến màu nhìn xuống sàn ("color_sensor").
 *
 * Cảm biến màu của virtual_robot lấy trung bình màu của một vùng nhỏ ảnh sân ngay dưới tâm robot.
 * Thảm xám có R≈G≈B ở mức trung bình; vạch trắng làm cả ba kênh TĂNG lên. Ta dùng "độ sáng" = (R+G+B)/3
 * và so với mức nền đo lúc INIT (hiệu chuẩn) — cách làm này vẫn đúng khi đổi ảnh sân/ánh sáng.
 */
@Autonomous(name = "P1_FindLine", group = "P1-MotorSensor")
public class P1_FindLine extends LinearOpMode {
    static final double JUMP = 25;      // sáng hơn nền bao nhiêu thì coi là vạch trắng
    private final MecanumDrive drive = new MecanumDrive();

    @Override
    public void runOpMode() throws InterruptedException {
        drive.init(hardwareMap);
        ColorSensor color = hardwareMap.get(ColorSensor.class, "color_sensor");

        double background = 0;
        while (opModeInInit()) {                 // hiệu chuẩn: đo độ sáng nền khi robot đứng yên
            background = (color.red() + color.green() + color.blue()) / 3.0;
            telemetry.addData("Độ sáng nền", "%.0f", background);
            telemetry.update();
            sleep(50);
        }

        while (opModeIsActive()) {
            double brightness = (color.red() + color.green() + color.blue()) / 3.0;
            boolean onLine = brightness > background + JUMP;
            drive.drive(onLine ? 0 : 0.25, 0, 0);
            telemetry.addData("RGB", "%d %d %d", color.red(), color.green(), color.blue());
            telemetry.addData("Độ sáng", "%.0f (nền %.0f)", brightness, background);
            telemetry.addData("Trạng thái", onLine ? "ĐÃ THẤY VẠCH — dừng" : "đang tìm...");
            telemetry.update();
            if (onLine) break;
        }
        drive.stop();
        while (opModeIsActive()) idle();
    }
}
