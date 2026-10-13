package org.firstinspires.ftc.teamcode.course.util;

import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Bộ điều khiển PIDF tự viết (giống chương 24 sách "Learn Java for FTC"), có thêm:
 *  - giới hạn tích phân (chống "integral windup"),
 *  - tự xóa tích phân khi mục tiêu thay đổi,
 *  - giới hạn đầu ra.
 *
 *  output = kP*error + kI*∫error dt + kD*d(error)/dt + kF*target
 */
public class Pidf {
    public double kP, kI, kD, kF;
    public double maxIntegral = 1.0;     // giới hạn |kI * tích phân|
    public double minOutput = -1, maxOutput = 1;

    private double integral = 0, lastError = 0, lastTarget = Double.NaN;
    private boolean first = true;
    private final ElapsedTime timer = new ElapsedTime();

    public Pidf(double kP, double kI, double kD, double kF) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kF = kF;
    }

    public void reset() {
        integral = 0;
        lastError = 0;
        first = true;
        timer.reset();
    }

    /** Tính đầu ra từ mục tiêu (target) và giá trị đo được (measured). */
    public double calculate(double target, double measured) {
        double dt = timer.seconds();
        timer.reset();
        if (target != lastTarget) {          // mục tiêu đổi => xóa tích phân
            integral = 0;
            lastTarget = target;
        }
        double error = target - measured;
        double derivative = 0;
        if (!first && dt > 1e-6) {
            integral += error * dt;
            derivative = (error - lastError) / dt;
        }
        first = false;
        if (kI != 0) {                       // chống windup: |kI*integral| <= maxIntegral
            double limit = maxIntegral / Math.abs(kI);
            integral = Math.max(-limit, Math.min(limit, integral));
        }
        lastError = error;
        double out = kP * error + kI * integral + kD * derivative + kF * target;
        return Math.max(minOutput, Math.min(maxOutput, out));
    }
}
