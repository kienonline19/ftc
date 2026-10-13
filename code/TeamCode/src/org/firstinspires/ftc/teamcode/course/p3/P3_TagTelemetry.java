package org.firstinspires.ftc.teamcode.course.p3;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;
import org.firstinspires.ftc.teamcode.course.sim.SimTruth;

import java.util.List;

/**
 * PHẦN 3 — Ví dụ 3.1: Xem camera "nhìn thấy" gì.
 * Lái robot (cần trái: tiến/ngang, cần phải: quay) quanh sân DECODE và quan sát range / bearing / yaw thay đổi.
 *   - Quay robot sang TRÁI so với thẻ => bearing ...? ; tiến lại gần => range giảm; đi vòng sang bên => yaw đổi dấu.
 *   - B: bật/tắt nhiễu đo để thấy dữ liệu "thật" của camera dao động thế nào.
 */
@TeleOp(name = "P3_TagTelemetry", group = "P3-AprilTag")
public class P3_TagTelemetry extends OpMode {
    private final MecanumDrive drive = new MecanumDrive();
    private SimAprilTagCamera camera;

    @Override
    public void init() {
        drive.init(hardwareMap);
        camera = new SimAprilTagCamera();     // camera ở mép trước robot, cao 10 in, nhìn thẳng
    }

    @Override
    public void loop() {
        drive.drive(-gamepad1.left_stick_y * 0.6, gamepad1.left_stick_x * 0.6, gamepad1.right_stick_x * 0.4);
        if (gamepad1.bWasPressed()) camera.noiseEnabled = !camera.noiseEnabled;

        List<SimTagDetection> detections = camera.getDetections();
        telemetry.addData("Số thẻ thấy được", detections.size());
        for (SimTagDetection d : detections) {
            telemetry.addLine(String.format("== ID %d (%s) ==", d.id, d.metadata.name));
            telemetry.addLine(String.format("   range %.1f in | bearing %.1f° | yaw %.1f°",
                    d.ftcPose.range, d.ftcPose.bearing, d.ftcPose.yaw));
            telemetry.addLine(String.format("   x %.1f | y %.1f | z %.1f in | elevation %.1f°",
                    d.ftcPose.x, d.ftcPose.y, d.ftcPose.z, d.ftcPose.elevation));
        }
        double[] t = SimTruth.poseInchesDegrees();
        telemetry.addData("Vị trí thật", "(%.1f, %.1f, %.0f°)", t[0], t[1], t[2]);
        telemetry.addData("Nhiễu đo (B)", camera.noiseEnabled ? "BẬT" : "TẮT");
    }
}
