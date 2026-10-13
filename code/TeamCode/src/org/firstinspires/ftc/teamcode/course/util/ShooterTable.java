package org.firstinspires.ftc.teamcode.course.util;

/**
 * Bảng tra "khoảng cách camera tới thẻ goal (inch) -> RPM flywheel" cho Shooter Bot (góc nòng 55°).
 * Các giá trị này được đo bằng cách bắn thử ở nhiều khoảng cách (xem Bài tập 4.4) — bạn hãy tự đo lại!
 * Lưu ý: dưới ~35 in robot đứng quá sát goal, camera không còn thấy thẻ và bóng phải bay rất cong.
 */
public final class ShooterTable {
    private ShooterTable() {}

    public static InterpLUT create() {
        return new InterpLUT()
                .add(35, 1805)
                .add(45, 1865)
                .add(55, 1945)
                .add(65, 2030)
                .add(75, 2120)
                .add(85, 2210)
                .add(95, 2300)
                .add(105, 2385)
                .add(115, 2465)
                .add(125, 2550)
                .add(135, 2625);
    }
}
