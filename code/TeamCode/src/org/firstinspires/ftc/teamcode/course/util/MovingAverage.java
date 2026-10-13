package org.firstinspires.ftc.teamcode.course.util;

import java.util.ArrayDeque;

/** Trung bình trượt N mẫu gần nhất — lọc nhiễu đơn giản cho dữ liệu cảm biến (range, bearing...). */
public class MovingAverage {
    private final int size;
    private final ArrayDeque<Double> window = new ArrayDeque<>();
    private double sum = 0;

    public MovingAverage(int size) {
        this.size = size;
    }

    public double add(double value) {
        window.addLast(value);
        sum += value;
        if (window.size() > size) sum -= window.removeFirst();
        return get();
    }

    public double get() {
        return window.isEmpty() ? 0 : sum / window.size();
    }

    public void clear() {
        window.clear();
        sum = 0;
    }
}
