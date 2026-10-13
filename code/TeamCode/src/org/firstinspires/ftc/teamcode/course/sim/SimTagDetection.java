package org.firstinspires.ftc.teamcode.course.sim;

import java.util.Locale;

/**
 * Một lần "nhìn thấy" thẻ AprilTag — mô phỏng lớp AprilTagDetection của FTC SDK.
 * Tên các trường giống hệt SDK để code luyện tập trong mô phỏng chuyển sang robot thật dễ dàng:
 *   detection.id, detection.metadata.name, detection.ftcPose.range, .bearing, .yaw ...
 */
public class SimTagDetection {
    public final int id;
    public final Metadata metadata;
    public final FtcPose ftcPose;

    public SimTagDetection(int id, Metadata metadata, FtcPose ftcPose) {
        this.id = id;
        this.metadata = metadata;
        this.ftcPose = ftcPose;
    }

    /**
     * Vị trí của thẻ so với ỐNG KÍNH camera (đơn vị: inch và độ), theo quy ước của FTC SDK:
     *  x  : lệch ngang, dương = thẻ nằm bên PHẢI tâm ảnh
     *  y  : khoảng cách thẳng phía trước camera
     *  z  : chênh lệch độ cao, dương = thẻ cao hơn camera
     *  yaw: thẻ xoay quanh trục đứng; 0 = mặt thẻ song song với mặt camera; ngược chiều kim đồng hồ là dương
     *  range    = sqrt(x² + y²)  (đo trên mặt phẳng ngang)
     *  bearing  = góc camera phải quay để nhìn thẳng vào thẻ; dương = quay sang TRÁI (ngược chiều kim đồng hồ)
     *  elevation= góc camera phải ngẩng lên để nhìn vào tâm thẻ
     */
    public static class FtcPose {
        public double x, y, z, yaw, pitch, roll, range, bearing, elevation;

        @Override
        public String toString() {
            return String.format(Locale.US, "range=%.1f in, bearing=%.1f°, yaw=%.1f°, x=%.1f, y=%.1f, z=%.1f",
                    range, bearing, yaw, x, y, z);
        }
    }

    /** Thông tin cố định của thẻ trên sân (tương tự AprilTagMetadata của SDK). */
    public static class Metadata {
        public final String name;
        public final double fieldX, fieldY, fieldZ, facingDeg, sizeIn;

        public Metadata(String name, double fieldX, double fieldY, double fieldZ, double facingDeg, double sizeIn) {
            this.name = name;
            this.fieldX = fieldX;
            this.fieldY = fieldY;
            this.fieldZ = fieldZ;
            this.facingDeg = facingDeg;
            this.sizeIn = sizeIn;
        }
    }
}
