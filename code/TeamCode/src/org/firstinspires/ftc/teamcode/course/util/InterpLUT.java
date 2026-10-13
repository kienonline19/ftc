package org.firstinspires.ftc.teamcode.course.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Bảng tra có nội suy tuyến tính (Interpolated Look-Up Table).
 * Ví dụ: khoảng cách tới goal (inch) -> tốc độ flywheel cần thiết (RPM).
 *
 *   InterpLUT lut = new InterpLUT();
 *   lut.add(40, 2400); lut.add(60, 2100); lut.add(80, 2200);
 *   double rpm = lut.get(50);   // = 2250 (nằm giữa 2400 và 2100)
 *
 * Ngoài khoảng dữ liệu: trả về giá trị ở đầu mút gần nhất (kẹp - clamp), không ngoại suy.
 */
public class InterpLUT {
    private final List<double[]> points = new ArrayList<>();

    /** Thêm một điểm đo (x, y). Các điểm được tự sắp xếp theo x tăng dần. */
    public InterpLUT add(double x, double y) {
        int i = 0;
        while (i < points.size() && points.get(i)[0] < x) i++;
        if (i < points.size() && points.get(i)[0] == x) points.set(i, new double[]{x, y});
        else points.add(i, new double[]{x, y});
        return this;
    }

    public int size() {
        return points.size();
    }

    public double get(double x) {
        if (points.isEmpty()) throw new IllegalStateException("Bảng tra chưa có điểm nào");
        if (x <= points.get(0)[0]) return points.get(0)[1];
        double[] last = points.get(points.size() - 1);
        if (x >= last[0]) return last[1];
        for (int i = 1; i < points.size(); i++) {
            double[] a = points.get(i - 1), b = points.get(i);
            if (x <= b[0]) {
                double t = (x - a[0]) / (b[0] - a[0]);
                return a[1] + t * (b[1] - a[1]);
            }
        }
        return last[1];
    }
}
