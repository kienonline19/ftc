package org.firstinspires.ftc.teamcode.course.p2;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.math.Pose;

/**
 * Các vị trí quan trọng trên sân DECODE (hệ tọa độ của tài liệu = Pedro, inch) — viết cho liên minh XANH (BLUE).
 * Liên minh ĐỎ (RED) = đối xứng qua đường thẳng x = 72: dùng PoseFactory.degrees().mirrorX(72).
 *
 *   Goal XANH: góc trên-trái, điểm ngắm ≈ (10, 134).   Goal ĐỎ: góc trên-phải ≈ (134, 134).
 *   Vùng bắn lớn: phía trên chữ V trắng  (y >= 72 + |x − 72|).
 *   Vạch đặt bóng (spike mark) bên trái ở y = 84, 60, 36 (x ≈ 20…30).
 *   Base XANH: ô vuông xanh dưới-phải (tâm ≈ (105, 34)).
 */
public class DecodePoses {
    public static final double GOAL_X = 10, GOAL_Y = 134;      // goal XANH

    public final Pose START, SHOOT, INTAKE_START, INTAKE_END, BASE, GOAL;

    public DecodePoses(boolean red) {
        PoseFactory pf = red ? PoseFactory.degrees().mirrorX(72) : PoseFactory.degrees();
        START = pf.of(56, 9, 90);
        SHOOT = pf.of(60, 86, aimHeadingDeg(60, 86));          // hướng thẳng vào goal
        INTAKE_START = pf.of(46, 84, 180);
        INTAKE_END = pf.of(20, 84, 180);
        BASE = pf.of(105, 34, 90);
        GOAL = pf.of(GOAL_X, GOAL_Y, 0);
    }

    /** Góc (độ) để robot đứng ở (x, y) nhìn thẳng vào goal XANH. */
    public static double aimHeadingDeg(double x, double y) {
        return Math.toDegrees(Math.atan2(GOAL_Y - y, GOAL_X - x));
    }
}
