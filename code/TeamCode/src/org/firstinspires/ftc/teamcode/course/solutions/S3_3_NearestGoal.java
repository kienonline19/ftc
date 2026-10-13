package org.firstinspires.ftc.teamcode.course.solutions;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.course.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;

/**
 * LỜI GIẢI Bài tập 3.3 — Chọn thẻ GOAL gần nhất và bỏ qua thẻ ở quá xa.
 *   - chỉ xét thẻ goal (20 = xanh, 24 = đỏ), bỏ qua thẻ obelisk (21, 22, 23);
 *   - thẻ xa hơn MAX_TRUSTED_RANGE bị coi là không tin cậy (nhiễu lớn) => bỏ qua, nhưng vẫn đếm;
 *   - trong các thẻ còn lại, chọn thẻ có range NHỎ NHẤT.
 * Thử: đặt robot tại (66, 30) nhìn lên (90°) => thấy cả 2 goal; tại (66, 10) nhìn lên => cả 2 đều quá xa.
 */
@TeleOp(name = "S3_3_NearestGoal", group = "Z-LoiGiai")
public class S3_3_NearestGoal extends OpMode {
    static final double MAX_TRUSTED_RANGE = 120;     // inch
    private final MecanumDrive drive = new MecanumDrive();
    private SimAprilTagCamera camera;

    @Override
    public void init() {
        drive.init(hardwareMap);
        camera = new SimAprilTagCamera();
    }

    @Override
    public void loop() {
        drive.drive(-gamepad1.left_stick_y * 0.6, gamepad1.left_stick_x * 0.6, gamepad1.right_stick_x * 0.4);

        SimTagDetection best = null;
        int goalsSeen = 0, ignored = 0;
        for (SimTagDetection d : camera.getDetections()) {
            boolean isGoal = d.id == SimAprilTagCamera.BLUE_GOAL_ID || d.id == SimAprilTagCamera.RED_GOAL_ID;
            if (!isGoal) continue;                                    // bỏ qua obelisk
            goalsSeen++;
            if (d.ftcPose.range > MAX_TRUSTED_RANGE) {                // quá xa => không tin
                ignored++;
                continue;
            }
            if (best == null || d.ftcPose.range < best.ftcPose.range) best = d;
        }

        if (best == null) {
            telemetry.addData("Goal gần nhất", "KHÔNG CÓ (trong %.0f in)", MAX_TRUSTED_RANGE);
        } else {
            telemetry.addData("Goal gần nhất", "ID %d (%s) — %.1f in, bearing %.1f°",
                    best.id, best.metadata.name, best.ftcPose.range, best.ftcPose.bearing);
        }
        telemetry.addData("Số goal camera thấy", goalsSeen);
        telemetry.addData("Bỏ qua vì quá xa", ignored);
    }
}
