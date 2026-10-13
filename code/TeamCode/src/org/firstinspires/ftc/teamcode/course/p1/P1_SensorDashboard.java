package org.firstinspires.ftc.teamcode.course.p1;

import android.graphics.Color;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * PHẦN 1 — Ví dụ 1.2: Bảng theo dõi mọi cảm biến của "Programming Board" (chương 6–11 của sách).
 *  - Bấm nút "touch" trên board, kéo thanh trượt pot / màu R,G,B / khoảng cách, xoay board bằng chuột.
 *  - Servo đi theo góc của biến trở (pot): đây là vòng lặp ĐỌC CẢM BIẾN -> QUYẾT ĐỊNH -> ĐIỀU KHIỂN.
 */
@TeleOp(name = "P1_SensorDashboard", group = "P1-MotorSensor")
public class P1_SensorDashboard extends OpMode {
    private DigitalChannel touch;
    private AnalogInput pot;
    private ColorSensor color;
    private DistanceSensor distance;
    private IMU imu;
    private Servo servo;

    @Override
    public void init() {
        touch = hardwareMap.get(DigitalChannel.class, "touch_sensor");
        touch.setMode(DigitalChannel.Mode.INPUT);
        pot = hardwareMap.get(AnalogInput.class, "pot");
        color = hardwareMap.get(ColorSensor.class, "sensor_color_distance");
        distance = hardwareMap.get(DistanceSensor.class, "sensor_color_distance");   // cùng tên với cảm biến màu!
        servo = hardwareMap.get(Servo.class, "servo");
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
    }

    @Override
    public void loop() {
        boolean pressed = !touch.getState();             // DigitalChannel: false = đang bị nhấn
        double potAngle = Range.scale(pot.getVoltage(), 0, pot.getMaxVoltage(), 0, 270);
        servo.setPosition(potAngle / 270.0);             // servo bám theo biến trở

        int r = color.red(), g = color.green(), b = color.blue();
        float[] hsv = new float[3];
        Color.RGBToHSV(r, g, b, hsv);                    // hsv[0] = sắc độ (hue) 0..360

        telemetry.addData("Nút chạm", pressed ? "ĐANG NHẤN" : "thả");
        telemetry.addData("Biến trở", "%.2f V -> %.0f độ", pot.getVoltage(), potAngle);
        telemetry.addData("Servo", "%.2f", servo.getPosition());
        telemetry.addData("Màu RGB", "%d, %d, %d (hue %.0f°) -> %s", r, g, b, hsv[0], tenMau(hsv));
        telemetry.addData("Khoảng cách", "%.1f cm", distance.getDistance(DistanceUnit.CM));
        telemetry.addData("Góc quay (IMU yaw)", "%.1f độ", imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));
    }

    /** Phân loại màu theo sắc độ (hue) — bền vững hơn so sánh trực tiếp R, G, B khi ánh sáng thay đổi. */
    static String tenMau(float[] hsv) {
        if (hsv[1] < 0.25 || hsv[2] < 0.15) return "không rõ / xám";
        float h = hsv[0];
        if (h < 20 || h >= 330) return "ĐỎ";
        if (h < 70) return "VÀNG";
        if (h < 170) return "XANH LÁ";
        if (h < 260) return "XANH DƯƠNG";
        return "TÍM";
    }
}
