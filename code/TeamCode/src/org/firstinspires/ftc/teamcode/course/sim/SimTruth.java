package org.firstinspires.ftc.teamcode.course.sim;

import com.pedropathing.math.Pose;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * SimTruth — "vị trí THẬT" của robot trong virtual_robot (ground truth).
 *
 * CHỈ DÙNG TRONG MÔ PHỎNG! Robot thật không có hàm nào biết chính xác vị trí của nó:
 * vị trí phải được ước lượng bằng odometry (Pinpoint, encoder...) và camera (AprilTag).
 * Trong tài liệu, SimTruth được dùng để:
 *   1) giả lập camera AprilTag (camera cần biết robot đang thật sự ở đâu để "nhìn thấy" thẻ);
 *   2) khởi tạo vị trí cho Pedro Pathing đúng chỗ bạn đã đặt robot bằng chuột.
 *
 * Hệ tọa độ trả về = HỆ TỌA ĐỘ KHÓA HỌC (giống Pedro Pathing):
 *   gốc (0, 0) ở góc DƯỚI-TRÁI của sân như trên màn hình virtual_robot, x sang PHẢI, y lên TRÊN,
 *   đơn vị inch (sân 144 x 144 in), heading 0 = robot nhìn sang phải (+x), tăng NGƯỢC chiều kim đồng hồ.
 *
 * Cách hoạt động: đọc (bằng reflection) vị trí "thô" mà simulator lưu trong CommonOdometry
 * (gốc ở tâm sân, đơn vị mét, heading 0 = robot nhìn lên trên) rồi đổi sang hệ tọa độ của tài liệu.
 * Chỉ hoạt động với các cấu hình có vật lý: Mecanum Bot, MecDynamic Bot, XDrive Bot, Shooter Bot.
 */
public final class SimTruth {
    private SimTruth() {}

    private static Pose2D rawPose() {
        try {
            Class<?> c = Class.forName("com.qualcomm.hardware.CommonOdometry");
            Method getInstance = c.getMethod("getInstance");
            Object odo = getInstance.invoke(null);
            Field f = c.getDeclaredField("rawPoseMR");
            f.setAccessible(true);
            return (Pose2D) f.get(odo);
        } catch (Exception e) {
            throw new IllegalStateException("SimTruth chỉ chạy được trong virtual_robot (cấu hình có odometry).", e);
        }
    }

    /** {x (in), y (in), heading (độ)} theo hệ tọa độ của tài liệu. */
    public static double[] poseInchesDegrees() {
        Pose2D raw = rawPose();
        double x = raw.getX(DistanceUnit.INCH) + 72.0;
        double y = raw.getY(DistanceUnit.INCH) + 72.0;
        double h = AngleUnit.normalizeDegrees(raw.getHeading(AngleUnit.DEGREES) + 90.0);
        return new double[]{x, y, h};
    }

    /** Vị trí thật dưới dạng Pose của Pedro Pathing (x, y inch; heading radian). */
    public static Pose pedroPose() {
        double[] p = poseInchesDegrees();
        return new Pose(p[0], p[1], Math.toRadians(p[2]));
    }
}
