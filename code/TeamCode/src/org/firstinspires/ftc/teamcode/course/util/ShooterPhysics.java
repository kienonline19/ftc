package org.firstinspires.ftc.teamcode.course.util;

/**
 * LỜI GIẢI Bài tập 4.6 — Tính RPM cần thiết bằng công thức ném xiên (thay cho bảng tra).
 *
 *  Bóng rời nòng với tốc độ v, góc θ, ở độ cao h0. Muốn bóng đi qua điểm (d, h) — cách d mét, cao h mét:
 *      h − h0 = d·tanθ − g·d² / (2·v²·cos²θ)
 *  =>  v = sqrt( g·d² / (2·cos²θ·(d·tanθ − (h − h0))) )      (cần d·tanθ > h − h0)
 *  Flywheel bán kính r, bóng nhận tốc độ v = η·ω·r  =>  ω = v / (η·r)  =>  RPM = ω·60 / (2π)
 *
 * Với Shooter Bot: θ = 55°, h0 = 0,30 m, h = 1,0 m, r = 0,048 m, η = 0,5; bóng rời nòng ở 8 in phía trước tâm robot.
 */
public final class ShooterPhysics {
    private ShooterPhysics() {}

    public static double THETA_DEG = 55, H0 = 0.30, H_GOAL = 1.0, WHEEL_R = 0.048, ETA = 0.5;

    /** @param distanceIn khoảng cách NGANG từ điểm bóng rời nòng tới tâm miệng goal (inch). Trả về NaN nếu không thể. */
    public static double requiredRpm(double distanceIn) {
        double d = distanceIn * 0.0254;
        double th = Math.toRadians(THETA_DEG);
        double denom = 2 * Math.cos(th) * Math.cos(th) * (d * Math.tan(th) - (H_GOAL - H0));
        if (denom <= 0) return Double.NaN;
        double v = Math.sqrt(9.81 * d * d / denom);
        double omega = v / (ETA * WHEEL_R);
        return omega * 60 / (2 * Math.PI);
    }
}
