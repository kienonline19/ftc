package org.firstinspires.ftc.teamcode.course.p4;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.course.mechanisms.Shooter;

/**
 * PHẦN 4 — Ví dụ 4.3: Bộ điều khiển vận tốc TỰ VIẾT trong lớp Shooter (feedforward + P + bù điện áp).
 * Cấu hình: Shooter Bot.
 *   dpad lên/xuống : RPM mục tiêu ±100     A : bắn 1 quả     B : bắn 3 quả liên tiếp (máy trạng thái)
 *   X : bật/tắt phần P (chỉ còn feedforward)          Y : bật/tắt bù điện áp pin (hệ số 12/V)
 * Mỗi 50 ms in một dòng CSV ra cửa sổ Run (console): thời_gian,mục_tiêu,rpm,công_suất
 * -> chép vào Excel / Google Sheets để vẽ đồ thị đáp ứng.
 */
@TeleOp(name = "P4_FlywheelCustom", group = "P4-Flywheel")
public class P4_FlywheelCustom extends OpMode {
    private final Shooter shooter = new Shooter();
    private double savedKP;
    private boolean useP = true;
    private final ElapsedTime clock = new ElapsedTime();
    private double lastLog = 0;

    @Override
    public void init() {
        shooter.init(hardwareMap);
        shooter.setTargetRpm(2200);
        savedKP = Shooter.kP;
        System.out.println("t,target,rpm,power");
    }

    @Override
    public void start() {
        clock.reset();
    }

    @Override
    public void loop() {
        if (gamepad1.dpadUpWasPressed()) shooter.setTargetRpm(shooter.getTargetRpm() + 100);
        if (gamepad1.dpadDownWasPressed()) shooter.setTargetRpm(shooter.getTargetRpm() - 100);
        if (gamepad1.aWasPressed()) shooter.shoot(1);
        if (gamepad1.bWasPressed()) shooter.shoot(3);
        if (gamepad1.xWasPressed()) { useP = !useP; Shooter.kP = useP ? savedKP : 0; }
        if (gamepad1.yWasPressed()) Shooter.VOLTAGE_COMPENSATION = !Shooter.VOLTAGE_COMPENSATION;
        shooter.update();

        if (clock.seconds() - lastLog >= 0.05) {
            lastLog = clock.seconds();
            System.out.printf("%.2f,%.0f,%.0f,%.3f%n", lastLog, shooter.getTargetRpm(), shooter.getRpm(), shooter.getLastPower());
        }
        telemetry.addData("Mục tiêu / đo được", "%.0f / %.0f RPM", shooter.getTargetRpm(), shooter.getRpm());
        telemetry.addData("Đủ tốc?", shooter.atSpeed() ? "CÓ" : "chưa");
        telemetry.addData("Công suất", "%.3f", shooter.getLastPower());
        telemetry.addData("Phản hồi P", useP ? "BẬT" : "TẮT (chỉ feedforward)");
        telemetry.addData("Pin", "%.2f V | bù điện áp: %s", shooter.getBatteryVoltage(), Shooter.VOLTAGE_COMPENSATION ? "BẬT" : "TẮT");
        telemetry.addData("Trạng thái bắn", shooter.getState() + " | đã bắn " + shooter.getShotsFired());
    }

    @Override
    public void stop() {
        Shooter.kP = savedKP;      // trả lại giá trị mặc định cho các OpMode khác
        Shooter.VOLTAGE_COMPENSATION = true;
    }
}
