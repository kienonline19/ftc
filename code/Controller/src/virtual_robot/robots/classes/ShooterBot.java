package virtual_robot.robots.classes;

import com.qualcomm.robotcore.hardware.DcMotorControllerImpl;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorExImpl;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.PassiveColorSensorImpl;
import com.qualcomm.robotcore.hardware.PassiveDistanceSensorImpl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImpl;
import com.qualcomm.robotcore.hardware.VoltageSensorImpl;
import com.qualcomm.robotcore.hardware.configuration.MotorType;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import virtual_robot.controller.BotConfig;
import virtual_robot.controller.Game;
import virtual_robot.controller.VirtualField;
import virtual_robot.robots.ControlsElements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * SHOOTER BOT — cấu hình robot dùng cho tài liệu "Lập trình robot FTC khi chưa có robot thật".
 *
 * Kế thừa MecDynamicBot (khung gầm mecanum có mô hình vật lý, Pinpoint, IMU, 4 cảm biến khoảng cách,
 * cảm biến màu nhìn xuống sàn, tay gắp) nên các hằng số Pedro Pathing có sẵn trong virtual_robot
 * vẫn dùng được nguyên vẹn. Bot thêm các thiết bị sau:
 *
 *   "shooter_motor"  DcMotorEx  – động cơ flywheel 1:1, 6000 RPM, 28 tick/vòng, có mô hình vật lý
 *                                 (quán tính bánh đà, đường đặc tính động cơ, điện áp pin, tải khi bắn bóng)
 *   "feeder_servo"   Servo      – đẩy 1 quả bóng vào flywheel: 0 = nghỉ, 1 = đẩy. Phải giữ >= 100 ms ở mỗi vị trí.
 *   "intake_motor"   DcMotorEx  – hút bóng. Chỉ nạp được khi tâm robot nằm trong VÙNG NẠP (ô 24x24 in
 *                                 ở hai góc dưới của sân). Mỗi 0,4 s nạp 1 bóng, tối đa 3 bóng.
 *   "ball_sensor"    ColorSensor + DistanceSensor – nhìn quả bóng kế tiếp trong ổ (tím / xanh lá),
 *                                 khoảng cách ~2,5 cm nếu có bóng, ~15 cm nếu ổ trống.
 *   "voltage_sensor" VoltageSensor – điện áp pin mô phỏng (mặc định ngẫu nhiên 12,0–13,6 V mỗi lần chạy).
 *
 * Mỗi lần bắn, bot tính quỹ đạo bóng (góc nòng cố định) từ tốc độ flywheel THẬT tại thời điểm bắn,
 * rồi vẽ điểm bóng đi qua độ cao miệng goal và ghi kết quả TRÚNG / NGẮN / DÀI / LỆCH lên sân.
 * Goal được giả định nằm ở 2 góc trên của sân (đúng vị trí goal trên ảnh sân DECODE).
 *
 * CÀI ĐẶT: chép file này vào Controller/src/virtual_robot/robots/classes/
 *          và file shooter_bot.fxml vào Controller/src/virtual_robot/robots/fxml/
 */
@BotConfig(name = "Shooter Bot", filename = "shooter_bot")
public class ShooterBot extends MecDynamicBot implements ControlsElements {

    // ===================== THAM SỐ MÔ PHỎNG (giáo viên có thể chỉnh) =====================
    /** Điện áp pin (V). Double.NaN = ngẫu nhiên 12,0–13,6 V mỗi lần chạy OpMode. */
    public static double BATTERY_VOLTAGE = Double.NaN;
    /** Góc nòng (hood) cố định, độ. */
    public static double LAUNCH_ANGLE_DEG = 55;
    /** Độ cao điểm bóng rời flywheel (m). */
    public static double LAUNCH_HEIGHT_M = 0.30;
    /** Độ cao miệng goal (m) — giá trị giả định của mô phỏng. */
    public static double GOAL_HEIGHT_M = 1.0;
    /** Bán kính bánh flywheel (m) — bánh 96 mm. */
    public static double WHEEL_RADIUS_M = 0.048;
    /** Tốc độ bóng = EXIT_EFFICIENCY * tốc độ mặt bánh (bóng lăn giữa bánh và hood => ~0,5). */
    public static double EXIT_EFFICIENCY = 0.5;
    /** Khối lượng một quả bóng (kg). */
    public static double BALL_MASS_KG = 0.075;
    /** Dung sai TRÚNG theo chiều sâu (dọc hướng bắn) và theo chiều ngang (m). */
    public static double HIT_DEPTH_TOL_M = 0.15;
    public static double HIT_LATERAL_TOL_M = 0.18;
    /** Nhiễu ngẫu nhiên của tốc độ bóng (tỉ lệ, độ lệch chuẩn). */
    public static double SPEED_NOISE = 0.005;
    /** Điểm ngắm (tâm miệng goal) theo hệ tọa độ của tài liệu = hệ Pedro: gốc ở góc dưới-trái, đơn vị inch. */
    public static final double[] BLUE_GOAL = {10, 134};
    public static final double[] RED_GOAL = {134, 134};
    /** Khoảng cách từ tâm robot tới điểm bóng rời flywheel (inch, về phía trước). */
    public static double LAUNCH_OFFSET_IN = 8;

    // ===================== THIẾT BỊ =====================
    private FlywheelMotor shooterMotor;
    private DcMotorExImpl intakeMotor;
    private ServoImpl feederServo;
    private PassiveColorSensorImpl ballColor;
    private PassiveDistanceSensorImpl ballDistance;
    private BatterySensor batterySensor;

    @FXML private Circle flywheelCircle;
    @FXML private Circle magazine0;
    @FXML private Circle magazine1;
    @FXML private Circle magazine2;

    // ===================== TRẠNG THÁI =====================
    private final LinkedList<Character> magazine = new LinkedList<>();   // 'P' = tím, 'G' = xanh lá
    private final Random random = new Random();
    private double batteryVoltage = 12.6;

    private enum FeedState {READY, PUSHING, FIRED, RETRACTING}
    private FeedState feedState = FeedState.READY;
    private double feedTimerMs = 0;
    private double intakeTimerMs = 0;

    private int hits = 0, misses = 0;
    private volatile boolean resetPending = false;
    private final List<ShotResult> pendingShots = new ArrayList<>();
    private final LinkedList<Group> shotNodes = new LinkedList<>();
    private Group overlay;
    private Text statsText;
    private int shotCounter = 0;

    /** Kết quả một phát bắn (hệ tọa độ của tài liệu, inch). */
    public static class ShotResult {
        public double x0, y0, landX, landY, speed, distanceIn;
        public boolean hit;
        public char color;
        public String label = "";
        public String goal = "";
        public String labelAscii = "";
        public String goalAscii = "";
    }

    public ShooterBot() {
        super();
    }

    @Override
    public void initialize() {
        super.initialize();
        hardwareMap.setActive(true);
        shooterMotor = (FlywheelMotor) hardwareMap.get(DcMotorEx.class, "shooter_motor");
        intakeMotor = (DcMotorExImpl) hardwareMap.get(DcMotorEx.class, "intake_motor");
        feederServo = (ServoImpl) hardwareMap.get(Servo.class, "feeder_servo");
        hardwareMap.setActive(false);
        newBattery();
        refillMagazine();

        overlay = new Group();
        overlay.setMouseTransparent(true);
        statsText = new Text(8, 18, "");
        statsText.setFont(Font.font("DejaVu Sans", FontWeight.BOLD, 13));
        statsText.setFill(Color.WHITE);
        statsText.setStroke(Color.BLACK);
        statsText.setStrokeWidth(0.6);
        overlay.getChildren().add(statsText);
    }

    @Override
    protected void createHardwareMap() {
        super.createHardwareMap();
        hardwareMap.put("shooter_motor", new FlywheelMotor(motorController1, 1));
        hardwareMap.put("intake_motor", new DcMotorExImpl(MotorType.Gobilda137, motorController1, 2));
        hardwareMap.put("feeder_servo", new ServoImpl());
        ballColor = new PassiveColorSensorImpl();
        ballDistance = new PassiveDistanceSensorImpl(10, 200, 9, 201);
        hardwareMap.put("ball_sensor", ballColor);
        hardwareMap.put("ball_sensor", ballDistance);
        // Thay cảm biến điện áp mặc định (luôn 14 V) bằng pin mô phỏng.
        hardwareMap.voltageSensor.remove("voltage_sensor");
        batterySensor = new BatterySensor();
        hardwareMap.put("voltage_sensor", batterySensor);
    }

    @Override
    protected void setUpDisplayGroup(Group group) {
        super.setUpDisplayGroup(group);
        if (overlay != null && !fieldPane.getChildren().contains(overlay)) fieldPane.getChildren().add(overlay);
    }

    @Override
    public void removeFromDisplay(Pane fieldPane) {
        super.removeFromDisplay(fieldPane);
        fieldPane.getChildren().remove(overlay);
    }

    @Override
    public synchronized void updateStateAndSensors(double millis) {
        super.updateStateAndSensors(millis);
        if (resetPending) {
            resetPending = false;
            hits = 0;
            misses = 0;
            synchronized (pendingShots) { pendingShots.clear(); pendingShots.add(null); } // null = xóa hình cũ
        }
        shooterMotor.setVoltage(batteryVoltage);
        shooterMotor.update(millis);
        intakeMotor.update(millis);
        updateFeeder(millis);
        updateIntake(millis);
        updateBallSensor();
    }

    /** Máy trạng thái của servo đẩy bóng. */
    private void updateFeeder(double millis) {
        double pos = feederServo.getInternalPosition();
        switch (feedState) {
            case READY:
                if (pos > 0.8) { feedState = FeedState.PUSHING; feedTimerMs = 0; }
                break;
            case PUSHING:
                if (pos <= 0.2) feedState = FeedState.READY;
                else if (pos > 0.8) {
                    feedTimerMs += millis;
                    if (feedTimerMs >= 100) { fireBall(); feedState = FeedState.FIRED; }
                }
                break;
            case FIRED:
                if (pos <= 0.2) { feedState = FeedState.RETRACTING; feedTimerMs = 0; }
                break;
            case RETRACTING:
                if (pos <= 0.2) {
                    feedTimerMs += millis;
                    if (feedTimerMs >= 100) feedState = FeedState.READY;
                } else feedTimerMs = 0;
                break;
        }
    }

    private void updateIntake(double millis) {
        double[] p = pedroPose();
        boolean inLoadingZone = p[1] < 24 && (p[0] < 24 || p[0] > 120);
        if (intakeMotor.getPower() > 0.3 && inLoadingZone && magazine.size() < 3) {
            intakeTimerMs += millis;
            if (intakeTimerMs >= 400) {
                intakeTimerMs = 0;
                magazine.addLast(random.nextInt(3) == 0 ? 'G' : 'P');
            }
        } else intakeTimerMs = 0;
    }

    private void updateBallSensor() {
        if (magazine.isEmpty()) {
            ballColor.updateColor(15, 15, 15);
            ballDistance.update(150);
        } else if (magazine.getFirst() == 'G') {
            ballColor.updateColor(40, 210, 90);
            ballDistance.update(25);
        } else {
            ballColor.updateColor(150, 50, 220);
            ballDistance.update(25);
        }
    }

    /** Vị trí thật của robot theo hệ tọa độ của tài liệu: {x (in), y (in), heading (rad, 0 = hướng +x)}. */
    private double[] pedroPose() {
        double xIn = getX() / VirtualField.PIXELS_PER_INCH + 72;
        double yIn = getY() / VirtualField.PIXELS_PER_INCH + 72;
        double h = getHeadingRadians() + Math.PI / 2;
        return new double[]{xIn, yIn, h};
    }

    private void fireBall() {
        if (magazine.isEmpty()) {
            System.out.println("[ShooterBot] Servo da day nhung o chua KHONG con bong.");
            return;
        }
        char color = magazine.removeFirst();
        double v = shooterMotor.launchBall(BALL_MASS_KG, WHEEL_RADIUS_M, EXIT_EFFICIENCY);
        ShotResult r = evaluate(pedroPose(), v, color);
        if (r.hit) hits++; else misses++;
        synchronized (pendingShots) { pendingShots.add(r); }
        // In ra console (không dấu để tránh lỗi font trên một số máy)
        System.out.println(String.format(Locale.US,
                "[ShooterBot] Ban bong %s: v=%.2f m/s, khoang cach toi goal %s = %.1f in -> %s",
                color == 'G' ? "XANH" : "TIM", r.speed, r.goalAscii, r.distanceIn, r.labelAscii));
    }

    /** Tính quỹ đạo và đánh giá phát bắn. */
    private ShotResult evaluate(double[] pose, double v, char color) {
        ShotResult r = new ShotResult();
        r.color = color;
        double h = pose[2];
        double dx = Math.cos(h), dy = Math.sin(h);
        r.x0 = pose[0] + dx * LAUNCH_OFFSET_IN;
        r.y0 = pose[1] + dy * LAUNCH_OFFSET_IN;
        if (v <= 0) {
            r.label = "FLYWHEEL QUAY SAI CHIỀU / ĐỨNG YÊN";
            r.labelAscii = "FLYWHEEL QUAY SAI CHIEU / DUNG YEN";
            r.landX = r.x0;
            r.landY = r.y0;
            return r;
        }
        v *= 1 + SPEED_NOISE * random.nextGaussian();
        r.speed = v;
        double th = Math.toRadians(LAUNCH_ANGLE_DEG);
        double vh = v * Math.cos(th);
        double tan = Math.tan(th);
        double a = 9.81 / (2 * vh * vh);
        double dh = GOAL_HEIGHT_M - LAUNCH_HEIGHT_M;
        double disc = tan * tan - 4 * a * dh;
        boolean tooLow = disc < 0;
        double s = tooLow
                ? (tan + Math.sqrt(tan * tan + 4 * a * LAUNCH_HEIGHT_M)) / (2 * a)  // chạm sàn
                : (tan + Math.sqrt(disc)) / (2 * a);                               // đi xuống qua độ cao goal
        double sIn = s / 0.0254;
        r.landX = r.x0 + dx * sIn;
        r.landY = r.y0 + dy * sIn;

        // Chọn goal gần hướng bắn nhất (phải ở phía trước robot)
        double bestAngle = Double.MAX_VALUE, along = 0, lateral = 0;
        String[] names = {"XANH (BLUE)", "ĐỎ (RED)"};
        String[] namesAscii = {"XANH (BLUE)", "DO (RED)"};
        double[][] goals = {BLUE_GOAL, RED_GOAL};
        for (int i = 0; i < goals.length; i++) {
            double gx = goals[i][0] - r.x0, gy = goals[i][1] - r.y0;
            double al = gx * dx + gy * dy;          // dọc theo hướng bắn
            double lat = -gx * dy + gy * dx;        // > 0: goal nằm bên TRÁI đường bắn
            if (al <= 0) continue;
            double ang = Math.abs(Math.atan2(lat, al));
            if (ang < bestAngle) { bestAngle = ang; along = al; lateral = lat; r.goal = names[i]; r.goalAscii = namesAscii[i]; }
        }
        if (bestAngle == Double.MAX_VALUE) {
            r.label = "KHÔNG HƯỚNG VỀ GOAL";
            r.labelAscii = "KHONG HUONG VE GOAL";
            return r;
        }
        r.distanceIn = along;
        double depthErrIn = sIn - along;
        double latTolIn = HIT_LATERAL_TOL_M / 0.0254, depthTolIn = HIT_DEPTH_TOL_M / 0.0254;
        if (tooLow) {
            r.label = "QUÁ YẾU (không lên tới miệng goal)";
            r.labelAscii = "QUA YEU (khong len toi mieng goal)";
        } else if (Math.abs(lateral) > latTolIn) {
            r.label = String.format(Locale.US, "LỆCH %s %.0f in", lateral > 0 ? "PHẢI" : "TRÁI", Math.abs(lateral));
            r.labelAscii = String.format(Locale.US, "LECH %s %.0f in", lateral > 0 ? "PHAI" : "TRAI", Math.abs(lateral));
        } else if (Math.abs(depthErrIn) <= depthTolIn) {
            r.label = "TRÚNG!";
            r.labelAscii = "TRUNG!";
            r.hit = true;
        } else if (depthErrIn < 0) {
            r.label = String.format(Locale.US, "NGẮN %.0f in", -depthErrIn);
            r.labelAscii = String.format(Locale.US, "NGAN %.0f in", -depthErrIn);
        } else {
            r.label = String.format(Locale.US, "DÀI %.0f in", depthErrIn);
            r.labelAscii = String.format(Locale.US, "DAI %.0f in", depthErrIn);
        }
        return r;
    }

    @Override
    public synchronized void updateDisplay() {
        super.updateDisplay();
        double rpm = Math.abs(shooterMotor.getVelocity()) * 60.0 / FlywheelMotor.TICKS_PER_REV;
        if (flywheelCircle != null) {
            double frac = Math.min(1.0, rpm / 4000.0);
            flywheelCircle.setFill(Color.color(0.35 + 0.65 * frac, 0.35 + 0.25 * frac, 0.35 * (1 - frac)));
        }
        Circle[] dots = {magazine0, magazine1, magazine2};
        for (int i = 0; i < dots.length; i++) {
            if (dots[i] == null) continue;
            if (i < magazine.size()) dots[i].setFill(magazine.get(i) == 'G' ? Color.LIMEGREEN : Color.MEDIUMPURPLE);
            else dots[i].setFill(Color.gray(0.85));
        }
        List<ShotResult> toDraw;
        synchronized (pendingShots) {
            toDraw = new ArrayList<>(pendingShots);
            pendingShots.clear();
        }
        for (ShotResult r : toDraw) {
            if (r == null) {
                overlay.getChildren().removeAll(shotNodes);
                shotNodes.clear();
                shotCounter = 0;
            } else drawShot(r);
        }
        statsText.setText(String.format(Locale.US,
                "Trúng %d | Trượt %d | Bóng trong ổ: %d | Flywheel: %.0f RPM | Pin: %.1f V",
                hits, misses, magazine.size(), rpm, batteryVoltage));
        statsText.toFront();
    }

    private void drawShot(ShotResult r) {
        double ppi = VirtualField.PIXELS_PER_INCH;
        // Nếu điểm rơi nằm ngoài sân, chỉ vẽ tới mép sân (nhãn vẫn ghi đúng sai số).
        double ex = r.landX, ey = r.landY, t = 1.0;
        double ddx = r.landX - r.x0, ddy = r.landY - r.y0;
        if (ex < 3) t = Math.min(t, (3 - r.x0) / ddx);
        if (ex > 141) t = Math.min(t, (141 - r.x0) / ddx);
        if (ey < 3) t = Math.min(t, (3 - r.y0) / ddy);
        if (ey > 141) t = Math.min(t, (141 - r.y0) / ddy);
        t = Math.max(0, t);
        ex = r.x0 + ddx * t;
        ey = r.y0 + ddy * t;
        Color c = r.hit ? Color.LIMEGREEN : Color.ORANGERED;
        Line line = new Line(r.x0 * ppi, (144 - r.y0) * ppi, ex * ppi, (144 - ey) * ppi);
        line.setStroke(c);
        line.setStrokeWidth(2.5);
        line.getStrokeDashArray().addAll(8.0, 5.0);
        Circle dot = new Circle(ex * ppi, (144 - ey) * ppi, 6,
                r.color == 'G' ? Color.LIMEGREEN : Color.MEDIUMPURPLE);
        dot.setStroke(c);
        dot.setStrokeWidth(2.5);
        double labelX = Math.min(ex * ppi + 9, 144 * ppi - 150);
        double labelY = Math.max(32, (144 - ey) * ppi + 4) + 15 * (shotCounter++ % 4);   // tránh chữ đè lên nhau
        Text label = new Text(Math.max(4, labelX), labelY, r.label);
        label.setFont(Font.font("DejaVu Sans", FontWeight.BOLD, 12));
        label.setFill(c);
        label.setStroke(Color.BLACK);
        label.setStrokeWidth(0.4);
        Group g = new Group(line, dot, label);
        overlay.getChildren().add(g);
        shotNodes.add(g);
        while (shotNodes.size() > 6) overlay.getChildren().remove(shotNodes.removeFirst());
    }

    @Override
    public void powerDownAndReset() {
        super.powerDownAndReset();
        shooterMotor.stopAndReset();
        intakeMotor.stopAndReset();
        feedState = FeedState.READY;
        refillMagazine();
        newBattery();
        resetPending = true;
    }

    private void newBattery() {
        batteryVoltage = Double.isNaN(BATTERY_VOLTAGE) ? 12.0 + 1.6 * random.nextDouble() : BATTERY_VOLTAGE;
        if (shooterMotor != null) shooterMotor.setVoltage(batteryVoltage);
    }

    private void refillMagazine() {
        List<Character> balls = new ArrayList<>();
        balls.add('P');
        balls.add('P');
        balls.add('G');
        Collections.shuffle(balls, random);
        magazine.clear();
        magazine.addAll(balls);
    }

    @Override
    public void preloadElements(Game game) {
        refillMagazine();
    }

    @Override
    public void clearLoadedElements(Game game) {
        magazine.clear();
    }

    /** Cảm biến điện áp trả về điện áp pin mô phỏng. */
    public class BatterySensor extends VoltageSensorImpl {
        @Override
        public double getVoltage() {
            return batteryVoltage;
        }
    }

    // =====================================================================================
    /**
     * Động cơ flywheel có mô hình vật lý:
     *   J * dω/dt = τ_stall * (u * V / 12 - ω / ω_free) - b * ω - ma sát Coulomb
     * u = lệnh công suất [-1, 1] (RUN_WITHOUT_ENCODER) hoặc đầu ra của bộ PIDF vận tốc "kiểu Hub"
     * (RUN_USING_ENCODER). Khi một quả bóng đi qua, bánh đà mất năng lượng => tốc độ tụt xuống.
     */
    public static class FlywheelMotor extends DcMotorExImpl {
        public static final double J = 1.5e-4;                               // kg·m²
        public static final double STALL_TORQUE = 0.105;                     // N·m (tại 12 V)
        public static final double FREE_SPEED = 6000 * 2 * Math.PI / 60.0;   // rad/s (tại 12 V)
        public static final double V_NOMINAL = 12.0;
        public static final double VISCOUS = 0.07 * STALL_TORQUE / FREE_SPEED;
        public static final double COULOMB = 0.002;                          // N·m
        public static final double TICKS_PER_REV = 28;
        // Hệ số quy đổi để PIDF (đơn vị kiểu REV Hub) cho đáp ứng hợp lý. Chỉ là mô phỏng gần đúng!
        private static final double SCALE_P = 20, SCALE_I = 50, SCALE_D = 0.02;

        private double omega = 0;              // rad/s (chiều vật lý; > 0 = chiều bắn đúng)
        private double position = 0, basePosition = 0;
        private double voltage = 12.6;
        private boolean velocityMode = false;
        private double targetTps = 0;
        private double integral = 0, lastError = 0;
        private PIDFCoefficients velocityPidf = new PIDFCoefficients(10, 3, 0, 0);

        public FlywheelMotor(DcMotorControllerImpl controller, int port) {
            super(MotorType.RevUltraPlanetaryOneToOne, controller, port);
        }

        synchronized void setVoltage(double v) { voltage = v; }

        private boolean rev() {
            return (getDirection() == Direction.REVERSE) != MOTOR_TYPE.REVERSED;
        }

        private double logicalTps() {
            double v = omega * TICKS_PER_REV / (2 * Math.PI);
            return rev() ? -v : v;
        }

        @Override
        public synchronized void setMode(RunMode mode) {
            super.setMode(mode);
            velocityMode = false;
            targetTps = 0;
            integral = 0;
            lastError = 0;
            if (mode == RunMode.STOP_AND_RESET_ENCODER) basePosition = position;
        }

        @Override
        public synchronized void setPower(double power) {
            super.setPower(power);
            velocityMode = false;
        }

        @Override
        public synchronized void setVelocity(double angularRate) {
            super.setPower(Math.max(-1, Math.min(1, angularRate / MOTOR_TYPE.MAX_TICKS_PER_SECOND)));
            velocityMode = true;
            targetTps = angularRate;
        }

        @Override
        public synchronized double getVelocity() {
            return logicalTps();
        }

        @Override
        public synchronized int getCurrentPosition() {
            int result = (int) Math.floor(position - basePosition);
            return rev() ? -result : result;
        }

        @Override
        public synchronized double getActualPosition() {
            return position;
        }

        @Override
        public synchronized void setVelocityPIDFCoefficients(double p, double i, double d, double f) {
            velocityPidf = new PIDFCoefficients(p, i, d, f);
        }

        @Override
        public synchronized void setPIDFCoefficients(RunMode mode, PIDFCoefficients c) {
            if (mode == RunMode.RUN_USING_ENCODER) velocityPidf = new PIDFCoefficients(c.p, c.i, c.d, c.f);
        }

        @Override
        public synchronized PIDFCoefficients getPIDFCoefficients(RunMode mode) {
            if (mode == RunMode.RUN_USING_ENCODER)
                return new PIDFCoefficients(velocityPidf.p, velocityPidf.i, velocityPidf.d, velocityPidf.f);
            return super.getPIDFCoefficients(mode);
        }

        @Override
        public synchronized boolean isBusy() {
            return getMode() == RunMode.RUN_TO_POSITION && Math.abs(getPower()) > 1e-4
                    && Math.abs(getTargetPosition() - getCurrentPosition()) > 10;
        }

        @Override
        public synchronized void stopAndReset() {
            super.stopAndReset();
            omega = 0;
            velocityMode = false;
            targetTps = 0;
            integral = 0;
            lastError = 0;
        }

        /** Bộ PIDF vận tốc "kiểu Hub": u = (F*mục tiêu + P*sai số + I*tích phân + D*đạo hàm) / 32767. */
        private double hubVelocityControl(double target, double v, double h) {
            double err = target - v;
            integral += err * h;
            double deriv = (err - lastError) / h;
            lastError = err;
            double u = (velocityPidf.f * target + SCALE_P * velocityPidf.p * err
                    + SCALE_I * velocityPidf.i * integral + SCALE_D * velocityPidf.d * deriv) / 32767.0;
            if (u > 1 || u < -1) {          // chống bão hòa tích phân (anti-windup)
                integral -= err * h;
                u = Math.max(-1, Math.min(1, u));
            }
            if (target == 0 && Math.abs(v) < 5) { integral = 0; u = 0; }
            return u;
        }

        @Override
        public synchronized double update(double milliseconds) {
            double dt = milliseconds / 1000.0;
            int steps = 20;
            double h = dt / steps;
            double start = position;
            boolean r = rev();
            double maxTps = MOTOR_TYPE.MAX_TICKS_PER_SECOND;
            RunMode mode = getMode();
            for (int k = 0; k < steps; k++) {
                double v = logicalTps();
                double u;
                if (mode == RunMode.STOP_AND_RESET_ENCODER) {
                    u = 0;
                } else if (mode == RunMode.RUN_USING_ENCODER) {
                    double target = velocityMode ? targetTps : getPower() * maxTps;
                    u = hubVelocityControl(target, v, h);
                } else if (mode == RunMode.RUN_TO_POSITION) {
                    double posErr = getTargetPosition() - (r ? -(position - basePosition) : (position - basePosition));
                    double limit = Math.abs(getPower()) * maxTps;
                    double target = Math.max(-limit, Math.min(limit, 5.0 * posErr));
                    u = hubVelocityControl(target, v, h);
                } else {
                    u = velocityMode ? Math.max(-1, Math.min(1, targetTps / maxTps)) : getPower();
                }
                double uPhys = r ? -u : u;
                boolean coast = (u == 0 && getZeroPowerBehavior() == ZeroPowerBehavior.FLOAT);
                double torque = coast ? 0 : STALL_TORQUE * (uPhys * voltage / V_NOMINAL - omega / FREE_SPEED);
                torque -= VISCOUS * omega;
                if (omega == 0) {
                    if (Math.abs(torque) <= COULOMB) continue;
                    torque -= COULOMB * Math.signum(torque);
                    omega += torque / J * h;
                } else {
                    double newOmega = omega + (torque - COULOMB * Math.signum(omega)) / J * h;
                    if (Math.signum(newOmega) != Math.signum(omega) && Math.abs(torque) <= COULOMB) newOmega = 0;
                    omega = newOmega;
                }
                position += omega * TICKS_PER_REV / (2 * Math.PI) * h;
            }
            return position - start;
        }

        /**
         * Một quả bóng đi qua flywheel: bóng nhận tốc độ v = eta * ω * r, bánh đà mất năng lượng tương ứng
         * (kể cả hao hụt do trượt/nén bóng). Trả về tốc độ bóng (m/s), hoặc -1 nếu bánh quay sai chiều/quá chậm.
         */
        public synchronized double launchBall(double ballMass, double wheelRadius, double eta) {
            if (omega < 20) return -1;
            double v = eta * omega * wheelRadius;
            double lostEnergy = 0.5 * ballMass * v * v * 1.3;
            double energy = 0.5 * J * omega * omega;
            omega = Math.sqrt(Math.max(0, energy - lostEnergy) * 2 / J);
            return v;
        }
    }
}
