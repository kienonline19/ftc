package org.firstinspires.ftc.teamcode.course.p3;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.course.sim.SimAprilTagCamera;
import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;

/**
 * PHẦN 3 — Ví dụ 3.2: Đọc MOTIF trên obelisk trong lúc INIT (init_loop), giống chương 16 của sách.
 *   ID 21 = GPP (xanh-tím-tím), 22 = PGP, 23 = PPG.
 * Đặt robot ở giữa sân, nhìn LÊN (về phía obelisk ngoài tường trên), ví dụ (72, 60, 90°).
 * Mẹo: đọc nhiều lần và chỉ "chốt" khi cùng một ID xuất hiện liên tiếp => chống đọc nhầm.
 */
@Autonomous(name = "P3_ReadMotif", group = "P3-AprilTag")
public class P3_ReadMotif extends OpMode {
    private SimAprilTagCamera camera;
    private String motif = "CHƯA BIẾT";
    private int lastId = -1, sameCount = 0;

    @Override
    public void init() {
        camera = new SimAprilTagCamera();
    }

    @Override
    public void init_loop() {
        for (SimTagDetection d : camera.getDetections()) {
            if (d.id < 21 || d.id > 23) continue;            // bỏ qua thẻ goal (20, 24)
            sameCount = (d.id == lastId) ? sameCount + 1 : 1;
            lastId = d.id;
            if (sameCount >= 5) motif = motifFromId(d.id);   // thấy cùng ID 5 lần liên tiếp
        }
        telemetry.addData("Motif", motif);
        telemetry.addData("ID gần nhất", lastId);
    }

    static String motifFromId(int id) {
        switch (id) {
            case 21: return "GPP";
            case 22: return "PGP";
            case 23: return "PPG";
            default: return "CHƯA BIẾT";
        }
    }

    @Override
    public void start() {
        telemetry.addData("Kế hoạch", "Bắn theo thứ tự " + motif);
    }

    @Override
    public void loop() {
        telemetry.addData("Motif đã chốt", motif);
        telemetry.addData("(Đáp án của mô phỏng)", motifFromId(camera.getObeliskIdForTesting()));
    }
}
