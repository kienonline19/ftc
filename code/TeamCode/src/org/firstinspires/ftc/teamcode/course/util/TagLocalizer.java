package org.firstinspires.ftc.teamcode.course.util;

import org.firstinspires.ftc.teamcode.course.sim.SimTagDetection;

/**
 * Tính vị trí robot trên sân từ MỘT lần nhìn thấy thẻ AprilTag có vị trí đã biết.
 *
 *  Bước 1: hướng camera  = hướng mặt thẻ − yaw + 180°      (đảo ngược định nghĩa của yaw)
 *  Bước 2: hướng từ camera tới thẻ = hướng camera + bearing
 *  Bước 3: vị trí camera = vị trí thẻ − range × (cos, sin)(hướng ở bước 2)
 *  Bước 4: vị trí tâm robot = vị trí camera − (độ lệch lắp camera, xoay theo hướng robot)
 *
 * Mọi giá trị theo hệ tọa độ của tài liệu (inch, độ, 0° = +x, ngược chiều kim đồng hồ dương).
 */
public final class TagLocalizer {
    private TagLocalizer() {}

    /** Dùng yaw của thẻ để suy ra hướng robot (không cần IMU, nhưng nhiễu hơn khi ở xa). */
    public static double[] robotPose(SimTagDetection d, double camForward, double camLeft, double camYawDeg) {
        double camHeading = d.metadata.facingDeg - d.ftcPose.yaw + 180;
        return fromCameraHeading(d, camHeading, camForward, camLeft, camYawDeg);
    }

    /** Dùng hướng robot đã biết (IMU/odometry) — chính xác hơn, chỉ lấy range + bearing từ camera. */
    public static double[] robotPoseWithHeading(SimTagDetection d, double robotHeadingDeg,
                                                double camForward, double camLeft, double camYawDeg) {
        return fromCameraHeading(d, robotHeadingDeg + camYawDeg, camForward, camLeft, camYawDeg);
    }

    private static double[] fromCameraHeading(SimTagDetection d, double camHeadingDeg,
                                              double camForward, double camLeft, double camYawDeg) {
        double toTag = Math.toRadians(camHeadingDeg + d.ftcPose.bearing);
        double camX = d.metadata.fieldX - d.ftcPose.range * Math.cos(toTag);
        double camY = d.metadata.fieldY - d.ftcPose.range * Math.sin(toTag);
        double h = Math.toRadians(camHeadingDeg - camYawDeg);
        double x = camX - (Math.cos(h) * camForward - Math.sin(h) * camLeft);
        double y = camY - (Math.sin(h) * camForward + Math.cos(h) * camLeft);
        return new double[]{x, y, normalizeDeg(Math.toDegrees(h))};
    }

    public static double normalizeDeg(double deg) {
        while (deg > 180) deg -= 360;
        while (deg <= -180) deg += 360;
        return deg;
    }
}
