package org.firstinspires.ftc.teamcode.course.sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * SimAprilTagCamera — camera AprilTag GIẢ LẬP cho virtual_robot.
 *
 * virtual_robot không mô phỏng camera (AprilTagProcessor.getDetections() của nó luôn trả về null),
 * nên lớp này tự tính xem một webcam gắn trên robot "nhìn thấy" những thẻ nào, dựa trên vị trí thật
 * của robot (SimTruth) và vị trí các thẻ trên sân DECODE. Kết quả có cùng ý nghĩa với FTC SDK.
 *
 * Mô hình camera:
 *   - gắn ở vị trí (forward, left, height) so với tâm robot, quay một góc yaw so với hướng robot;
 *   - góc nhìn ngang H_FOV, góc nhìn dọc V_FOV; chỉ thấy thẻ trong khoảng [MIN_RANGE, MAX_RANGE];
 *   - chỉ đọc được thẻ khi nhìn vào mặt thẻ không quá nghiêng (<= MAX_VIEW_ANGLE so với pháp tuyến);
 *   - có nhiễu đo (tăng theo khoảng cách) và thỉnh thoảng "rớt khung" (không thấy thẻ).
 *
 * Hệ tọa độ sân: hệ tọa độ của tài liệu (gốc góc dưới-trái, x phải, y lên, inch).
 */
public class SimAprilTagCamera {

    // ---------- Các thẻ trên sân DECODE (đổi sang hệ tọa độ của tài liệu) ----------
    public static final int BLUE_GOAL_ID = 20;
    public static final int RED_GOAL_ID = 24;
    public static final int OBELISK_GPP = 21;
    public static final int OBELISK_PGP = 22;
    public static final int OBELISK_PPG = 23;

    /** Một thẻ gắn cố định trên sân. facingDeg = hướng mà MẶT thẻ nhìn ra (độ, hệ của tài liệu). */
    public static class FieldTag {
        public final int id;
        public final SimTagDetection.Metadata metadata;

        public FieldTag(int id, String name, double x, double y, double z, double facingDeg, double sizeIn) {
            this.id = id;
            this.metadata = new SimTagDetection.Metadata(name, x, y, z, facingDeg, sizeIn);
        }
    }

    /**
     * Sân DECODE: thẻ 20 trên goal xanh (góc trên-trái), thẻ 24 trên goal đỏ (góc trên-phải),
     * và MỘT trong ba thẻ 21/22/23 trên obelisk (ngoài tường phía trên, chính giữa) — chọn ngẫu nhiên
     * mỗi trận, giống như motif được bốc thăm.
     * Vị trí goal lấy từ thư viện thẻ của SDK (đổi hệ trục); độ cao thẻ obelisk là giá trị gần đúng.
     */
    public static List<FieldTag> decodeField(int obeliskId) {
        List<FieldTag> tags = new ArrayList<>();
        tags.add(new FieldTag(BLUE_GOAL_ID, "BlueTarget", 16.4, 130.4, 29.5, -35.4, 6.5));
        tags.add(new FieldTag(RED_GOAL_ID, "RedTarget", 127.6, 130.4, 29.5, -144.6, 6.5));
        String name = obeliskId == OBELISK_GPP ? "Obelisk_GPP" : obeliskId == OBELISK_PGP ? "Obelisk_PGP" : "Obelisk_PPG";
        tags.add(new FieldTag(obeliskId, name, 72, 152, 20, -90, 6.5));
        return tags;
    }

    // ---------- Thông số camera ----------
    public double H_FOV_DEG = 70;
    public double V_FOV_DEG = 50;
    public double MIN_RANGE_IN = 6;
    public double MAX_RANGE_IN = 160;
    public double MAX_VIEW_ANGLE_DEG = 70;
    public double DROPOUT_PROBABILITY = 0.03;
    public boolean noiseEnabled = true;

    private final double camForward, camLeft, camHeight, camYawDeg;
    private final List<FieldTag> tags;
    private final int obeliskId;
    private final Random random = new Random();

    /** Camera mặc định: ở mép trước robot (8 in phía trước tâm), cao 10 in, nhìn thẳng về phía trước. */
    public SimAprilTagCamera() {
        this(8, 0, 10, 0);
    }

    /**
     * @param forwardIn khoảng cách camera ở phía trước tâm robot (inch)
     * @param leftIn    khoảng cách camera lệch sang trái tâm robot (inch)
     * @param heightIn  độ cao ống kính so với mặt sân (inch)
     * @param yawDeg    góc quay của camera so với hướng robot (độ, dương = quay trái)
     */
    public SimAprilTagCamera(double forwardIn, double leftIn, double heightIn, double yawDeg) {
        this.camForward = forwardIn;
        this.camLeft = leftIn;
        this.camHeight = heightIn;
        this.camYawDeg = yawDeg;
        this.obeliskId = OBELISK_GPP + random.nextInt(3);
        this.tags = decodeField(obeliskId);
    }

    /** ID của thẻ đang gắn trên obelisk trong "trận" này (chỉ để giáo viên kiểm tra đáp án). */
    public int getObeliskIdForTesting() {
        return obeliskId;
    }

    public List<FieldTag> getFieldTags() {
        return Collections.unmodifiableList(tags);
    }

    /** Danh sách thẻ camera đang thấy, tính từ vị trí THẬT của robot trong mô phỏng. */
    public List<SimTagDetection> getDetections() {
        double[] p = SimTruth.poseInchesDegrees();
        return detectFrom(p[0], p[1], p[2]);
    }

    /** Thẻ có id cho trước nếu đang thấy, ngược lại trả về null. */
    public SimTagDetection getDetection(int id) {
        for (SimTagDetection d : getDetections()) if (d.id == id) return d;
        return null;
    }

    /** Tính các thẻ nhìn thấy từ một vị trí robot cho trước (x, y inch; heading độ). */
    public List<SimTagDetection> detectFrom(double robotX, double robotY, double robotHeadingDeg) {
        List<SimTagDetection> result = new ArrayList<>();
        double h = Math.toRadians(robotHeadingDeg);
        double camX = robotX + Math.cos(h) * camForward - Math.sin(h) * camLeft;
        double camY = robotY + Math.sin(h) * camForward + Math.cos(h) * camLeft;
        double camHeading = robotHeadingDeg + camYawDeg;
        double ch = Math.toRadians(camHeading);

        for (FieldTag tag : tags) {
            SimTagDetection.Metadata m = tag.metadata;
            double dx = m.fieldX - camX, dy = m.fieldY - camY, dz = m.fieldZ - camHeight;
            double forward = dx * Math.cos(ch) + dy * Math.sin(ch);
            double right = dx * Math.sin(ch) - dy * Math.cos(ch);
            if (forward <= 0) continue;                                   // thẻ ở sau lưng camera
            // Công thức giống hệt FTC SDK (AprilTagProcessorImpl): range = hypot(x, y),
            // bearing = atan2(-x, y), elevation = atan2(z, y) — với x = phải, y = phía trước, z = lên trên.
            double range = Math.hypot(forward, right);
            double bearing = Math.toDegrees(Math.atan2(-right, forward));
            double elevation = Math.toDegrees(Math.atan2(dz, forward));
            if (range < MIN_RANGE_IN || range > MAX_RANGE_IN) continue;   // quá gần / quá xa
            if (Math.abs(bearing) > H_FOV_DEG / 2) continue;              // ngoài khung hình ngang
            if (Math.abs(elevation) > V_FOV_DEG / 2) continue;            // ngoài khung hình dọc
            double toCamera = Math.toDegrees(Math.atan2(camY - m.fieldY, camX - m.fieldX));
            if (Math.abs(normalize(toCamera - m.facingDeg)) > MAX_VIEW_ANGLE_DEG) continue; // nhìn quá xiên
            if (noiseEnabled && random.nextDouble() < DROPOUT_PROBABILITY) continue;        // rớt khung

            double yaw = normalize(m.facingDeg - camHeading + 180);
            if (noiseEnabled) {
                range += random.nextGaussian() * (0.2 + 0.006 * range);
                bearing += random.nextGaussian() * 0.3;
                elevation += random.nextGaussian() * 0.3;
                yaw += random.nextGaussian() * (1.0 + 0.015 * range);
            }
            SimTagDetection.FtcPose pose = new SimTagDetection.FtcPose();
            pose.range = range;
            pose.bearing = bearing;
            pose.elevation = elevation;
            pose.yaw = yaw;
            pose.x = -range * Math.sin(Math.toRadians(bearing));
            pose.y = range * Math.cos(Math.toRadians(bearing));
            pose.z = pose.y * Math.tan(Math.toRadians(elevation));      // => elevation = atan2(z, y) như SDK
            pose.pitch = 0;
            pose.roll = 0;
            result.add(new SimTagDetection(tag.id, m, pose));
        }
        return result;
    }

    private static double normalize(double deg) {
        while (deg > 180) deg -= 360;
        while (deg <= -180) deg += 360;
        return deg;
    }
}
