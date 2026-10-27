# Bài 1, 2, 3 — 15 biến thể mỗi bài

Mỗi biến thể dùng cùng kiến thức và cùng mức độ với bài gốc, nhưng là một tình huống khác.

**Cách kiểm thử:** viết hàm hoặc các lớp theo mô tả, dán các câu lệnh `assert` vào hàm `main`, rồi chạy bằng `java -ea Main`. Nếu không có `AssertionError` thì mọi test đều đạt.

---

# BÀI 1 — Ước, ƯCLN, BCNN

Một số test dùng `Arrays.equals`, nên cần thêm `import java.util.Arrays;`.

## 1.1 — Lát gạch

Một sàn phòng hình chữ nhật kích thước **m × n**. Cần lát bằng các viên gạch vuông giống nhau, càng to càng tốt, và không được cắt viên nào.

```java
static long[] tiles(long m, long n)   // trả về {cạnh viên gạch, số viên gạch}
```
Ràng buộc: 1 ≤ m, n ≤ 10⁹

```java
assert Arrays.equals(tiles(6, 4), new long[]{2, 6});
assert Arrays.equals(tiles(12, 18), new long[]{6, 6});
assert Arrays.equals(tiles(7, 5), new long[]{1, 35});
assert Arrays.equals(tiles(1000000000, 1000000000), new long[]{1000000000, 1});
```
**Gợi ý:** Cạnh viên gạch phải "vừa khít" theo cả hai chiều cùng lúc.

## 1.2 — Túi quà

Có **a** cái kẹo, **b** cái bánh và **c** hộp sữa. Hãy chia hết vào nhiều túi nhất có thể sao cho các túi giống hệt nhau.

```java
static int[] bags(int a, int b, int c)   // trả về {số túi, kẹo mỗi túi, bánh mỗi túi, sữa mỗi túi}
```
Ràng buộc: 1 ≤ a, b, c ≤ 10⁹

```java
assert Arrays.equals(bags(12, 18, 24), new int[]{6, 2, 3, 4});
assert Arrays.equals(bags(5, 9, 11), new int[]{1, 5, 9, 11});
assert Arrays.equals(bags(40, 60, 100), new int[]{20, 2, 3, 5});
assert Arrays.equals(bags(7, 7, 7), new int[]{7, 1, 1, 1});
```
**Gợi ý:** Bài toán cho hai loại quà em đã biết giải. Loại thứ ba thay đổi điều gì?

## 1.3 — Bánh răng

Hai bánh răng ăn khớp nhau: bánh A có **a** răng, bánh B có **b** răng. Ban đầu, hai răng được đánh dấu chạm nhau. Mỗi bánh phải quay bao nhiêu vòng trọn thì hai răng đánh dấu chạm nhau lại lần đầu tiên?

```java
static long[] turns(long a, long b)   // trả về {số vòng của A, số vòng của B}
```
Ràng buộc: 1 ≤ a, b ≤ 2·10⁹

```java
assert Arrays.equals(turns(12, 18), new long[]{3, 2});
assert Arrays.equals(turns(10, 15), new long[]{3, 2});
assert Arrays.equals(turns(7, 7), new long[]{1, 1});
assert Arrays.equals(turns(8, 9), new long[]{9, 8});
assert Arrays.equals(turns(1000000007, 1000000009), new long[]{1000000009, 1000000007});
```
**Gợi ý:** Hãy đếm số răng đã đi qua điểm tiếp xúc. Con số đó giống nhau với cả hai bánh.

## 1.4 — Số hoàn hảo

Một số được gọi là **hoàn hảo** nếu nó bằng tổng các ước dương nhỏ hơn chính nó (ví dụ 6 = 1 + 2 + 3).

```java
static boolean isPerfect(long n)
```
Ràng buộc: 1 ≤ n ≤ 10¹²

```java
assert isPerfect(6) && isPerfect(28) && isPerfect(496) && isPerfect(8128);
assert !isPerfect(1) && !isPerfect(12) && !isPerfect(999999999999L);
assert isPerfect(33550336) && isPerfect(137438691328L);
```
**Gợi ý:** Duyệt đến n là quá chậm. Các ước luôn đi theo cặp.

## 1.5 — Xếp hàng thể dục

Có **n** học sinh cần xếp thành một hình chữ nhật r hàng × c cột, với **r ≥ 2** và **c ≥ 2**. Hai cách xếp 3×4 và 4×3 được tính là khác nhau. Có bao nhiêu cách xếp?

```java
static int arrangements(int n)
```
Ràng buộc: 1 ≤ n ≤ 10⁹

```java
assert arrangements(12) == 4;
assert arrangements(7) == 0;
assert arrangements(36) == 7;
assert arrangements(16) == 3;
assert arrangements(1) == 0;
```
**Gợi ý:** Mỗi cách xếp ứng với một thứ rất quen thuộc của n. Thứ đó là gì, và cần loại đi những trường hợp nào?

## 1.6 — Rút gọn phân số

Rút gọn phân số p/q về dạng tối giản. Mẫu số luôn dương, và dấu (nếu có) nằm ở tử số. Nếu mẫu số bằng 1 thì chỉ in tử số.

```java
static String simplify(long p, long q)
```
Ràng buộc: |p|, |q| ≤ 10¹⁸, q ≠ 0

```java
assert simplify(6, 8).equals("3/4");
assert simplify(10, -4).equals("-5/2");
assert simplify(-3, -9).equals("1/3");
assert simplify(0, 5).equals("0");
assert simplify(14, 7).equals("2");
```
**Gợi ý:** Phần tính toán chỉ có một dòng. Phần khó là các dấu âm.

## 1.7 — Hai robot tuần tra

Robot A quay về trạm sạc mỗi **a** giây, robot B mỗi **b** giây (cả hai rời trạm tại giây 0). Trong các giây 1..t, có bao nhiêu thời điểm **đúng một** robot ở trạm?

```java
static long exactlyOne(long t, long a, long b)
```
Ràng buộc: 1 ≤ a, b ≤ 10⁹, 1 ≤ t ≤ 10¹⁸

```java
assert exactlyOne(24, 4, 6) == 6;
assert exactlyOne(10, 3, 3) == 0;
assert exactlyOne(20, 4, 5) == 7;
assert exactlyOne(1000000000000000000L, 2, 3) == 500000000000000001L;
```
**Gợi ý:** Vẽ hai tập hợp giao nhau. Phần "đúng một" là phần nào?

## 1.8 — Bánh xe mật mã

Một vòng tròn có **n** ô đánh số 0..n−1. Con trỏ bắt đầu ở ô 0 và mỗi lần nhảy **k** ô. Với bao nhiêu giá trị k trong 1..n thì con trỏ ghé qua được **tất cả** các ô?

```java
static long goodSteps(long n)
```
Ràng buộc: 1 ≤ n ≤ 10¹²

```java
assert goodSteps(1) == 1;
assert goodSteps(9) == 6;
assert goodSteps(12) == 4;
assert goodSteps(13) == 12;
assert goodSteps(1000000000000L) == 400000000000L;
```
**Gợi ý:** Thử với n = 12 và từng k để tìm ra điều kiện của k. Sau đó, đếm từng k sẽ quá chậm; hãy nghĩ về các ước nguyên tố của n.

## 1.9 — Cặp số bạn bè

Hai số **khác nhau** a và b là "bạn bè" nếu tổng các ước nhỏ hơn a bằng b, và tổng các ước nhỏ hơn b bằng a.

```java
static boolean amicable(long a, long b)
```
Ràng buộc: 1 ≤ a, b ≤ 10¹²

```java
assert amicable(220, 284) && amicable(284, 220);
assert amicable(1184, 1210) && amicable(17296, 18416);
assert !amicable(6, 6) && !amicable(220, 221) && !amicable(1, 1);
```
**Gợi ý:** Hãy viết một hàm phụ trước. Cẩn thận với trường hợp a = b.

## 1.10 — Chuyền bóng vòng tròn

**n** học sinh đứng thành vòng tròn, đánh số 0..n−1. Bạn số 0 cầm bóng, và mỗi lượt bóng được chuyền sang bạn đứng cách đó **k** vị trí theo cùng một chiều. Trò chơi kéo dài mãi. Có bao nhiêu bạn từng được cầm bóng?

```java
static long receivers(long n, long k)
```
Ràng buộc: 1 ≤ k ≤ n ≤ 10¹⁸

```java
assert receivers(12, 4) == 3;
assert receivers(12, 5) == 12;
assert receivers(10, 4) == 5;
assert receivers(7, 7) == 1;
assert receivers(1000000000, 600000000) == 5;
```
**Gợi ý:** Mô phỏng với vài cặp (n, k) nhỏ trên giấy và tìm quy luật. Mô phỏng không qua được test cuối.

## 1.11 — Cắt thanh gỗ

Có nhiều thanh gỗ với độ dài cho trước. Cần cắt tất cả thành các đoạn **bằng nhau, dài nhất có thể, không thừa**. Tổng cộng thu được bao nhiêu đoạn?

```java
static long pieces(long... lengths)
```
Ràng buộc: 1 ≤ số thanh ≤ 10⁵, 1 ≤ độ dài ≤ 10⁹

```java
assert pieces(12, 18, 30) == 10;
assert pieces(7, 11) == 18;
assert pieces(100) == 1;
assert pieces(1000000000, 999999999) == 1999999999L;
```
**Gợi ý:** Độ dài mỗi đoạn phải có quan hệ gì với độ dài **mọi** thanh?

## 1.12 — Số nhiều ước nhất

Tìm số **nhỏ nhất** trong 1..n có nhiều ước nhất.

```java
static int mostDivisors(int n)
```
Ràng buộc: 1 ≤ n ≤ 10⁵

```java
assert mostDivisors(1) == 1;
assert mostDivisors(5) == 4;
assert mostDivisors(10) == 6;
assert mostDivisors(100) == 60;
assert mostDivisors(1000) == 840;
assert mostDivisors(100000) == 83160;
```
**Gợi ý:** Thay vì hỏi "số này có những ước nào?", hãy hỏi "số d là ước của những số nào?".

## 1.13 — Phân tích thừa số nguyên tố

Viết n dưới dạng tích các thừa số nguyên tố theo thứ tự tăng dần, ví dụ `"2^3 * 3^2 * 5"`. Số mũ 1 thì không ghi.

```java
static String factorize(long n)
```
Ràng buộc: 2 ≤ n ≤ 10¹²

```java
assert factorize(360).equals("2^3 * 3^2 * 5");
assert factorize(13).equals("13");
assert factorize(2).equals("2");
assert factorize(1024).equals("2^10");
assert factorize(600851475143L).equals("71 * 839 * 1471 * 6857");
assert factorize(1000000000000L).equals("2^12 * 5^12");
```
**Gợi ý:** Sau khi chia hết các thừa số nhỏ, phần còn lại của n có thể là gì?

## 1.14 — Số chia hết cho tất cả

Tìm số dương nhỏ nhất chia hết cho mọi số 1, 2, …, n.

```java
static long smallestMultiple(int n)
```
Ràng buộc: 1 ≤ n ≤ 40

```java
assert smallestMultiple(1) == 1;
assert smallestMultiple(10) == 2520;
assert smallestMultiple(20) == 232792560L;
assert smallestMultiple(40) == 5342931457063200L;
```
**Gợi ý:** Nhân tất cả lại thì quá lớn. Mỗi số mới thực sự cần "góp thêm" bao nhiêu?

## 1.15 — Ước chung

Đếm số ước dương chung của a và b.

```java
static int commonDivisors(long a, long b)
```
Ràng buộc: 1 ≤ a, b ≤ 10¹²

```java
assert commonDivisors(12, 18) == 4;
assert commonDivisors(7, 13) == 1;
assert commonDivisors(100, 75) == 3;
assert commonDivisors(720720, 1081080) == 192;
assert commonDivisors(1000000000000L, 999999999996L) == 3;
```
**Gợi ý:** Mọi ước chung của a và b đều là ước của **một** số duy nhất.

---

# BÀI 2 — Kế thừa với robot

Mỗi biến thể yêu cầu một lớp cơ sở (thường là trừu tượng) và các lớp con. Hãy để logic dùng chung nằm ở lớp cơ sở, và chỉ ghi đè (override) phần khác nhau.

## 2.1 — Lời chào

| Lớp | `greet()` trả về |
|-----|------------------|
| `Robot` | `"Hi, I am <name>"` |
| `TeaBot extends Robot` | lời chào của `Robot` + `" and I serve tea"` |
| `GuardBot extends Robot` | lời chào của `Robot` viết HOA + `"!"` |
| `PoliteGuard extends GuardBot` | `"Excuse me. "` + lời chào của `GuardBot` |

```java
assert new Robot("R1").greet().equals("Hi, I am R1");
assert new TeaBot("Tee").greet().equals("Hi, I am Tee and I serve tea");
assert new GuardBot("Max").greet().equals("HI, I AM MAX!");
Robot r = new PoliteGuard("Bo");
assert r.greet().equals("Excuse me. HI, I AM BO!");
assert r instanceof GuardBot;
```
**Gợi ý:** Chuỗi `"Hi, I am "` chỉ nên xuất hiện đúng một lần trong code.

## 2.2 — Thời gian di chuyển

Mỗi robot có `speed()` (đơn vị/giây): `Tank` = 2, `Walker` = 1, `Racer` = 5.
- `int timeTo(int d)`: số giây nguyên ít nhất để đi hết quãng đường d.
- `boolean canReach(int d, int seconds)`: robot có đến kịp không.
- `Racer` cần thêm **2 giây khởi động** cho mỗi chuyến.

```java
Robot t = new Tank(), w = new Walker(), r = new Racer();
assert t.timeTo(10) == 5 && t.timeTo(11) == 6;
assert w.timeTo(7) == 7;
assert r.timeTo(10) == 4 && r.timeTo(1) == 3;
assert r.canReach(10, 4) && !r.canReach(10, 3);
assert !t.canReach(11, 5) && w.canReach(3, 3);
```
**Gợi ý:** `canReach` chỉ cần viết một lần. Nó vẫn đúng với `Racer` nếu nó gọi đúng phương thức.

## 2.3 — Robot chở hàng

Lớp `Carrier` có `capacity()` (kg mỗi chuyến): `DroneCarrier` = 5, `Truck` = 40, `Cart` = 12.
- `int trips(int kg)`: số chuyến ít nhất để chở hết kg.
- `int lastLoad(int kg)`: số kg ở chuyến cuối (bằng 0 nếu không có hàng).
- Hàm tĩnh `fleetTrips(List<Carrier> fleet, int kg)`: tổng số chuyến khi **mỗi** robot trong đội tự chở kg hàng.

```java
Carrier d = new DroneCarrier(), t = new Truck(), c = new Cart();
assert d.trips(12) == 3 && d.lastLoad(12) == 2;
assert d.trips(10) == 2 && d.lastLoad(10) == 5;
assert t.trips(100) == 3 && t.lastLoad(100) == 20;
assert c.trips(0) == 0 && c.lastLoad(0) == 0 && c.trips(12) == 1 && c.lastLoad(12) == 12;
assert fleetTrips(List.of(d, t, c), 60) == 19;
```
**Gợi ý:** Khi số kg chia hết cho sức chở, chuyến cuối chở bao nhiêu?

## 2.4 — Thuê robot

`cost(int hours)` = phí cố định + số giờ × giá mỗi giờ.

| Lớp | Phí cố định | Giá/giờ | Ghi chú |
|-----|-------------|---------|---------|
| `Helper` | 10 | 5 | |
| `Painter extends Helper` | 10 | 7 | |
| `Welder` | 50 | 20 | các giờ **sau giờ thứ 8** tính nửa giá |

```java
assert new Helper().cost(0) == 10 && new Helper().cost(4) == 30;
assert new Painter().cost(4) == 38;
assert new Welder().cost(8) == 210 && new Welder().cost(10) == 230;
Robot r = new Welder();
assert r.cost(12) == 250;
```
**Gợi ý:** `Painter` chỉ cần ghi đè một phương thức. `Welder` có thể tái sử dụng công thức của lớp cha thay vì viết lại.

## 2.5 — Robot cảm biến

Lớp `SensorBot` có `respond(int reading)`: tăng biến đếm `count` rồi trả về phản ứng của robot.
- `LineFollower`: reading < 50 → `"turn left"`, > 50 → `"turn right"`, = 50 → `"straight"`
- `ObstacleBot`: reading < 20 → `"stop"`, ngược lại → `"go"`
- `CautiousBot extends ObstacleBot`: giống `ObstacleBot` nhưng ngưỡng là 40

```java
SensorBot lf = new LineFollower();
assert lf.respond(30).equals("turn left") && lf.respond(50).equals("straight") && lf.respond(80).equals("turn right");
assert lf.count == 3;
SensorBot ob = new ObstacleBot();
assert ob.respond(10).equals("stop") && ob.respond(20).equals("go");
SensorBot cb = new CautiousBot();
assert cb.respond(30).equals("stop") && cb.respond(40).equals("go") && cb.count == 2;
```
**Gợi ý:** Việc đếm là chung cho mọi robot. `CautiousBot` không cần viết lại phần so sánh.

## 2.6 — Nâng cấp

Mỗi robot bắt đầu ở `level` = 1. `upgrade()` tăng level thêm 1 nếu chưa đạt `maxLevel()`, trả về `true`/`false`. `power()` = `basePower()` × level.

| Lớp | basePower | maxLevel | Ghi chú |
|-----|-----------|----------|---------|
| `Scout` | 3 | 5 | |
| `Tank` | 10 | 3 | |
| `MegaTank extends Tank` | 10 | 3 | sức mạnh gấp đôi `Tank` |

```java
Robot s = new Scout();
assert s.power() == 3;
for (int i = 0; i < 4; i++) assert s.upgrade();
assert s.level == 5 && s.power() == 15 && !s.upgrade();
Robot t = new Tank();
assert t.upgrade() && t.upgrade() && t.power() == 30 && !t.upgrade();
Robot m = new MegaTank();
assert m.power() == 20 && m.upgrade() && m.power() == 40 && m.maxLevel() == 3;
```
**Gợi ý:** "Gấp đôi `Tank`" thì nên gọi đến ai?

## 2.7 — Đấu robot

Mỗi robot có `hp` và `damage()`. `attack(Robot other)` gây sát thương bằng `damage()` lên robot kia. Máu không xuống dưới 0, và robot đã hết máu (`hp` = 0) thì không tấn công được.

| Lớp | hp ban đầu | damage | Ghi chú |
|-----|------------|--------|---------|
| `Brawler` | 30 | 8 | |
| `ShieldBot` | 20 | 4 | chỉ nhận **một nửa** sát thương (làm tròn xuống) |
| `Sniper` | 15 | 12 | |

```java
Robot b = new Brawler(), s = new ShieldBot(), n = new Sniper();
b.attack(s); assert s.hp == 16;
n.attack(s); assert s.hp == 10;
s.attack(b); assert b.hp == 26;
n.attack(b); n.attack(b); assert b.hp == 2 && b.isAlive();
b.attack(n); b.attack(n); assert n.hp == 0 && !n.isAlive();
n.attack(b); assert b.hp == 2;
```
**Gợi ý:** Việc giảm sát thương là chuyện của **robot bị đánh**, không phải của robot đi đánh.

## 2.8 — Robot dọn dẹp

`minutes(int area)`: số phút để dọn một phòng có diện tích area (m²).
- `Vacuum`: 2 m²/phút, làm tròn lên
- `Mop`: 1 m²/phút, cộng thêm 3 phút nạp nước cho mỗi 10 m² trọn vẹn
- `ComboBot extends Vacuum`: hút bụi xong rồi lau với tốc độ 1 m²/phút (không cần nạp nước)

`totalMinutes(int... areas)`: tổng thời gian để dọn từng phòng một.

```java
Cleaner v = new Vacuum(), m = new Mop(), c = new ComboBot();
assert v.minutes(25) == 13 && v.minutes(4) == 2;
assert m.minutes(25) == 31 && m.minutes(10) == 13 && m.minutes(9) == 9;
assert c.minutes(25) == 38;
assert v.totalMinutes(3, 4, 5) == 7;
assert c.totalMinutes(3, 4, 5) == 19;
```
**Gợi ý:** Vì sao `v.totalMinutes(3, 4, 5)` không bằng `v.minutes(12)`?

## 2.9 — Mã số robot

Mỗi robot được tạo ra sẽ nhận một mã số tăng dần chung cho **mọi** loại (1, 2, 3, …). `label()` trả về tiền tố + `"-"` + mã số. `Robot.total()` trả về số robot đã tạo.

| Lớp | Tiền tố |
|-----|---------|
| `Welder` | `W` |
| `Painter` | `P` |
| `Inspector extends Painter` | `I` |

```java
// Chạy ngay đầu main, trước khi tạo robot nào khác
Robot a = new Welder(), b = new Painter(), c = new Welder(), d = new Inspector();
assert a.label().equals("W-1") && b.label().equals("P-2") && c.label().equals("W-3") && d.label().equals("I-4");
assert Robot.total() == 4 && d instanceof Painter;
```
**Gợi ý:** Biến đếm phải thuộc về **lớp**, không thuộc về từng đối tượng.

## 2.10 — Sạc pin

Hàm khởi tạo nhận lượng pin ban đầu. `charge(int minutes)` sạc thêm `minutes × chargeRate()` nhưng không vượt quá `maxBattery()`, và trả về lượng pin thực sự được sạc thêm.

| Lớp | maxBattery | chargeRate |
|-----|------------|------------|
| `MiniBot` | 50 | 2 |
| `HeavyBot` | 200 | 5 |
| `FastMini extends MiniBot` | 50 | gấp 3 lần `MiniBot` |

```java
Robot m = new MiniBot(10);
assert m.charge(5) == 10 && m.battery == 20 && m.charge(100) == 30 && m.battery == 50 && m.charge(1) == 0;
Robot h = new HeavyBot(0);
assert h.charge(30) == 150 && h.charge(30) == 50 && h.battery == 200;
Robot f = new FastMini(0);
assert f.charge(5) == 30 && f.charge(5) == 20 && f.battery == 50;
```
**Gợi ý:** Nếu sau này `MiniBot` đổi tốc độ sạc, `FastMini` có tự động đổi theo không?

## 2.11 — Sức mạnh đội

Mỗi robot có `name` và `power()`:
- `Lifter(name, kg)`: power = kg / 10 (chia nguyên)
- `Runner(name, speed)`: power = speed × 2
- `Captain(name)`: power = 25

Viết hàm tĩnh `teamPower(List<Robot>)` trả về tổng sức mạnh, và `strongest(List<Robot>)` trả về tên robot mạnh nhất (nếu bằng nhau thì lấy robot đứng trước; danh sách rỗng thì trả về `"none"`).

```java
List<Robot> team = List.of(new Lifter("Ox", 120), new Runner("Fox", 7),
                           new Captain("Cap"), new Lifter("Bull", 250));
assert teamPower(team) == 76 && strongest(team).equals("Cap");
assert teamPower(List.of()) == 0 && strongest(List.of()).equals("none");
```
**Gợi ý:** Hai hàm tĩnh không cần biết trong danh sách có những loại robot nào.

## 2.12 — Robot trên lưới

Robot bắt đầu ở (0, 0), hướng về Bắc (`N`). `turnLeft()` và `turnRight()` xoay 90°. `forward()` đi `stepSize()` ô theo hướng hiện tại. `pos()` trả về dạng `"(x,y) D"`, với D là một trong `N`, `E`, `S`, `W`. Bắc là y tăng, Đông là x tăng.
- `Rover`: bước 1
- `Hopper`: bước 2
- `Crab extends Rover`: khi `forward()`, cua đi **ngang sang bên phải** so với hướng đang nhìn (hướng nhìn không đổi)

```java
Robot r = new Rover();  r.forward(); r.forward(); r.turnRight(); r.forward();
assert r.pos().equals("(1,2) E");
Robot h = new Hopper(); h.forward(); h.turnLeft(); h.forward();
assert h.pos().equals("(-2,2) W");
Robot c = new Crab();   c.forward();
assert c.pos().equals("(1,0) N");
c.turnRight(); c.forward();
assert c.pos().equals("(1,-1) E");
```
**Gợi ý:** Nếu đánh số các hướng 0..3 theo chiều kim đồng hồ, "bên phải" của một hướng là hướng nào?

## 2.13 — Bảng xếp hạng

`Robot implements Comparable<Robot>`: robot điểm cao hơn đứng trước; nếu bằng điểm thì xếp theo tên A→Z.
- `Sprinter(name, laps)`: điểm = laps × 10
- `Collector(name, items)`: điểm = items × 3
- `Allrounder(name, laps, items) extends Sprinter`: điểm của `Sprinter` + items × 3

```java
List<Robot> list = new ArrayList<>(List.of(new Sprinter("Zed", 4), new Collector("Amy", 15),
                                           new Allrounder("Kai", 2, 7), new Sprinter("Ann", 4)));
Collections.sort(list);
assert list.get(0).name.equals("Amy") && list.get(1).name.equals("Kai");
assert list.get(2).name.equals("Ann") && list.get(3).name.equals("Zed");
assert list.get(1).score() == 41;
```
**Gợi ý:** `compareTo` chỉ cần viết một lần, trong lớp cơ sở.

## 2.14 — Nhật ký lệnh

`Robot` (lớp thường, không trừu tượng) có `execute(String cmd)`: ghi lệnh vào nhật ký và trả về `"OK " + cmd`. `history()` trả về các lệnh đã ghi, nối bằng dấu phẩy.
- `SafeBot`: từ chối lệnh `"jump"`, trả về `"DENIED"` và **không** ghi vào nhật ký
- `LoudBot`: chuyển lệnh thành chữ HOA rồi xử lý như bình thường

```java
Robot r = new Robot();
assert r.execute("go").equals("OK go") && r.execute("jump").equals("OK jump") && r.history().equals("go,jump");
Robot s = new SafeBot();
assert s.execute("jump").equals("DENIED") && s.execute("spin").equals("OK spin") && s.history().equals("spin");
Robot l = new LoudBot();
assert l.execute("beep").equals("OK BEEP") && l.history().equals("BEEP");
```
**Gợi ý:** Cả hai lớp con đều nên để `super` làm phần việc ghi nhật ký.

## 2.15 — Tính điểm trận FTC

Lớp `MatchBot`: `addSamples(int n)` cộng thêm mẫu vật, `park()` đánh dấu đã đỗ xe. `points()` = số mẫu × `pointsPerSample()` + (`parkBonus()` nếu đã đỗ). Mặc định `parkBonus()` = 3.
- `IntakeBot`: 2 điểm/mẫu
- `BasketBot`: 5 điểm/mẫu
- `ClimberBot extends BasketBot`: thưởng đỗ xe là 15

```java
MatchBot i = new IntakeBot(); i.addSamples(4); assert i.points() == 8;  i.park(); assert i.points() == 11;
MatchBot b = new BasketBot(); b.addSamples(3); assert b.points() == 15; b.addSamples(2); assert b.points() == 25;
MatchBot c = new ClimberBot(); c.addSamples(1); assert c.points() == 5; c.park(); assert c.points() == 20;
int total = 0;
for (MatchBot m : List.of(i, b, c)) total += m.points();
assert total == 56;
```
**Gợi ý:** Phương thức nào có giá trị mặc định, và phương thức nào bắt buộc lớp con phải tự định nghĩa?

---

# BÀI 3 — Đạo hàm và tích phân trong FTC

Mọi test so sánh số thực với sai số 0.001. Em tự viết hai hàm phụ sau:

```java
static boolean close(double got, double want)               // |got − want| ≤ 0.001
static boolean close(double[] got, double... want)          // đúng với từng phần tử
```

## 3.1 — Vị trí, vận tốc, gia tốc

Bộ đo hành trình cho biết vị trí robot là x(t) = a·t³ + b·t² + c·t + d (inch). Tính vị trí, vận tốc và gia tốc tại thời điểm t.

```java
static double[] motion(double a, double b, double c, double d, double t)   // {x, v, acc}
```
```java
assert close(motion(1, 0, 0, 0, 2), 8, 12, 12);
assert close(motion(0, -2, 10, 5, 3), 17, -2, -4);
assert close(motion(0.5, -3, 4, 0, 1), 1.5, -0.5, -3);
```
**Gợi ý:** Mỗi lần lấy đạo hàm, số mũ và hệ số thay đổi thế nào?

## 3.2 — Tăng tốc rồi chạy đều

Robot xuất phát đứng yên, tăng tốc đều với gia tốc `accel` cho đến khi đạt `vMax`, rồi giữ nguyên tốc độ. Robot đã đi được bao xa sau t giây?

```java
static double distanceAt(double vMax, double accel, double t)
```
```java
assert close(distanceAt(30, 60, 0.25), 1.875);
assert close(distanceAt(30, 60, 2), 52.5);
assert close(distanceAt(20, 40, 0.5), 5);
assert close(distanceAt(24, 48, 3), 66);
```
**Gợi ý:** Vẽ đồ thị vận tốc theo thời gian. Quãng đường là diện tích của hình gì?

## 3.3 — Hồ sơ chuyển động hình thang

Robot đi một quãng `dist`: tăng tốc với gia tốc `accel`, chạy đều tối đa `vMax`, rồi giảm tốc với cùng độ lớn gia tốc để dừng đúng đích. Tính tổng thời gian.

```java
static double travelTime(double dist, double vMax, double accel)
```
```java
assert close(travelTime(48, 24, 48), 2.5);
assert close(travelTime(6, 24, 48), 0.707107);
assert close(travelTime(12, 24, 48), 1.0);
assert close(travelTime(100, 30, 60), 3.833333);
```
**Gợi ý:** Liệu robot có kịp đạt tốc độ tối đa không? Nếu không thì đồ thị vận tốc có hình gì?

## 3.4 — Góc tay máy lớn nhất

Góc của tay máy (độ) là θ(t) = a·t³ + b·t² + c·t, với 0 ≤ t ≤ T. Tìm góc lớn nhất mà tay máy đạt được.

```java
static double maxAngle(double a, double b, double c, double T)
```
```java
assert close(maxAngle(0, -10, 40, 5), 40);
assert close(maxAngle(1, -6, 9, 5), 20);
assert close(maxAngle(0, 0, 15, 4), 60);
assert close(maxAngle(-1, 3, 0, 4), 4);
```
**Gợi ý:** Giá trị lớn nhất chỉ có thể nằm ở một vài loại điểm. Test thứ hai cho thấy em không được bỏ sót loại nào.

## 3.5 — Quãng đường từ dữ liệu encoder

Robot chỉ đi tiến. Cứ mỗi `dt` giây, encoder ghi lại vận tốc một lần, được mảng `v[0..n]`. Hãy ước lượng tổng quãng đường bằng cách nối các điểm đo liên tiếp bằng đoạn thẳng.

```java
static double distance(double[] v, double dt)
```
```java
assert close(distance(new double[]{0, 10, 20, 30}, 0.5), 22.5);
assert close(distance(new double[]{12, 12, 12}, 1), 24);
assert close(distance(new double[]{0, 4, 8, 4, 0}, 0.25), 4);
assert close(distance(new double[]{5}, 0.1), 0);
```
**Gợi ý:** Phần diện tích dưới mỗi đoạn thẳng là hình gì?

## 3.6 — Vận tốc và gia tốc từ vị trí

Cứ mỗi `dt` giây ghi một vị trí, được mảng `p[0..n]`. Ước lượng **tốc độ lớn nhất** từ hiệu hai vị trí liên tiếp, và **độ lớn gia tốc lớn nhất** từ ba vị trí liên tiếp.

```java
static double[] estimate(double[] p, double dt)   // {tốc độ lớn nhất, |gia tốc| lớn nhất}
```
```java
assert close(estimate(new double[]{0, 1, 4, 9, 16}, 1), 7, 2);
assert close(estimate(new double[]{0, 2, 4, 6}, 0.5), 4, 0);
assert close(estimate(new double[]{0, 5, 5, 0}, 0.1), 50, 500);
```
**Gợi ý:** Gia tốc là "hiệu của các hiệu". Nó chia cho dt mấy lần?

## 3.7 — Độ dài đường cong

Robot chạy theo đường cong y = a·x² + b·x + c, từ x0 đến x1. Tính độ dài quãng đường robot đã đi.

```java
static double arcLength(double a, double b, double c, double x0, double x1)
```
```java
assert close(arcLength(0, 0, 5, 0, 10), 10);
assert close(arcLength(0, 0.75, 0, 0, 4), 5);
assert close(arcLength(1, 0, 0, 0, 1), 1.478943);
assert close(arcLength(0.1, -1, 0, 0, 10), 11.477936);
assert close(arcLength(-0.5, 2, 1, -1, 3), 6.800433);
```
**Gợi ý:** Chia đường cong thành rất nhiều đoạn gần như thẳng. Độ dài mỗi đoạn phụ thuộc vào độ dốc ở đó.

## 3.8 — Góc hướng trên đường cong

Robot chạy theo đường cong y = a·x² + b·x + c. Tính góc hướng (độ, so với trục x) tại vị trí x, và robot phải xoay bao nhiêu độ khi đi từ x0 đến x1.

```java
static double heading(double a, double b, double x)
static double turn(double a, double b, double x0, double x1)
```
```java
assert close(heading(0, 1, 5), 45);
assert close(heading(0.5, 0, 1), 45);
assert close(heading(1, 0, 0), 0);
assert close(heading(0, -1, 0), -45);
assert close(heading(1, 0, -0.5), -45);
assert close(turn(0.25, -1, 0, 4), 90);
assert close(turn(0, 2, 0, 10), 0);
```
**Gợi ý:** Hệ số góc của tiếp tuyến cho biết điều gì về hướng đi?

## 3.9 — Pin có đủ không?

Dòng điện mà robot tiêu thụ là I(t) = p·t² + q·t + r (ampe), với t tính bằng giây trong [0, T]. Tính dung lượng đã dùng theo **mAh**, và kiểm tra pin dung lượng `cap` (mAh) có đủ không.

```java
static double usedMah(double p, double q, double r, double T)
static boolean canFinish(double p, double q, double r, double T, double cap)
```
```java
assert close(usedMah(0, 0, 3.6, 10), 10);
assert close(usedMah(0, 0.72, 0, 10), 10);
assert close(usedMah(0.036, 0, 0, 10), 3.333333);
assert close(usedMah(0.01, -0.2, 5, 30), 41.666667);
assert canFinish(0.01, -0.2, 5, 30, 42) && !canFinish(0.01, -0.2, 5, 30, 41);
```
**Gợi ý:** Tích phân cho kết quả theo A·s. 1 mAh bằng bao nhiêu A·s?

## 3.10 — Thiết kế cú xoay

Vận tốc góc của robot là ω(t) = k·t·(T − t) (độ/giây), với 0 ≤ t ≤ T. Tìm k để robot xoay đúng `theta` độ, và tính vận tốc góc lớn nhất khi đó.

```java
static double[] turnProfile(double theta, double T)   // {k, ω lớn nhất}
```
```java
assert close(turnProfile(90, 3), 20, 45);
assert close(turnProfile(180, 2), 135, 135);
assert close(turnProfile(45, 1.5), 80, 45);
assert close(turnProfile(360, 4), 33.75, 135);
```
**Gợi ý:** Viết tổng góc xoay dưới dạng tích phân có chứa k, rồi giải ngược.

## 3.11 — Phanh êm

Robot đang chạy với vận tốc v0 thì bắt đầu phanh. Độ lớn gia tốc hãm tăng dần theo thời gian: a(t) = j·t. Tính thời gian đến khi dừng hẳn và quãng đường phanh.

```java
static double[] brake(double v0, double j)   // {thời gian dừng, quãng đường phanh}
```
```java
assert close(brake(8, 4), 2, 10.666667);
assert close(brake(18, 9), 2, 24);
assert close(brake(50, 100), 1, 33.333333);
assert close(brake(10, 5), 2, 13.333333);
```
**Gợi ý:** Đi từ gia tốc đến vận tốc, rồi đến vị trí: hai lần tích phân.

## 3.12 — Hai robot gần nhau nhất

Hai robot chuyển động thẳng đều trên sân. Robot A bắt đầu ở điểm `a` với vận tốc `va`, robot B bắt đầu ở điểm `b` với vận tốc `vb` (mỗi giá trị là mảng {x, y}). Tìm khoảng cách nhỏ nhất giữa chúng trong khoảng thời gian [0, T].

```java
static double closest(double[] a, double[] va, double[] b, double[] vb, double T)
```
```java
assert close(closest(new double[]{0,0}, new double[]{1,0}, new double[]{10,0}, new double[]{-1,0}, 10), 0);
assert close(closest(new double[]{0,0}, new double[]{0,1}, new double[]{10,0}, new double[]{-1,0}, 10), 7.071068);
assert close(closest(new double[]{0,0}, new double[]{0,1}, new double[]{10,0}, new double[]{-1,0}, 3), 7.615773);
assert close(closest(new double[]{0,0}, new double[]{2,2}, new double[]{3,4}, new double[]{2,2}, 5), 5);
```
**Gợi ý:** Bình phương khoảng cách là hàm gì theo t? Và đừng quên rằng t chỉ được nằm trong [0, T].

## 3.13 — Định lý giá trị trung bình

Vị trí robot là x(t) = a·t³ + b·t² + c·t. Tìm thời điểm t trong khoảng (0, T) mà vận tốc tức thời **bằng** vận tốc trung bình trên cả đoạn [0, T]. Nếu có hai thời điểm thì trả về thời điểm sớm hơn.

```java
static double meanValueTime(double a, double b, double c, double T)
```
Ràng buộc: a ≠ 0 hoặc b ≠ 0

```java
assert close(meanValueTime(0, 1, 0, 4), 2);
assert close(meanValueTime(1, 0, 0, 3), 1.732051);
assert close(meanValueTime(1, -3, 0, 3), 2);
assert close(meanValueTime(1, -1, 0, 2), 1.215250);
```
**Gợi ý:** Vận tốc trung bình là độ dời chia cho thời gian. Phương trình thu được có bậc mấy?

## 3.14 — Odometry khi chạy theo cung tròn

Robot xuất phát tại (0, 0), hướng theo trục +x. Nó chạy với tốc độ không đổi v, trong khi góc hướng tăng đều với tốc độ ω (rad/s). Tìm vị trí của robot sau T giây.

```java
static double[] arcOdometry(double v, double w, double T)   // {x, y}
```
```java
assert close(arcOdometry(10, 0, 2), 20, 0);
assert close(arcOdometry(10, Math.PI / 2, 1), 6.366198, 6.366198);
assert close(arcOdometry(5, Math.PI, 1), 0, 3.183099);
assert close(arcOdometry(4, 2 * Math.PI, 1), 0, 0);
```
**Gợi ý:** Mỗi thành phần vận tốc là v·cos θ(t) hoặc v·sin θ(t). Cẩn thận khi ω = 0.

## 3.15 — Sai số điều khiển giảm dần

Khi bộ điều khiển hoạt động, sai số vị trí giảm theo hàm e(t) = e0·e^(−k·t).
- `settleTime`: thời điểm sớm nhất mà sai số ≤ `tol` (bằng 0 nếu ngay từ đầu đã đạt).
- `accumulated`: tổng tích lũy của sai số theo thời gian, ∫₀ᵀ e(t) dt. Bộ điều khiển PID dùng đại lượng này cho thành phần I.

```java
static double settleTime(double e0, double k, double tol)
static double accumulated(double e0, double k, double T)
```
```java
assert close(settleTime(10, 2, 0.1), 2.302585);
assert close(settleTime(0.05, 2, 0.1), 0);
assert close(settleTime(24, 0.5, 3), 4.158883);
assert close(accumulated(10, 2, 1), 4.323324);
assert close(accumulated(6, 0.5, 100), 12);
```
**Gợi ý:** Phép toán nào "gỡ" được hàm mũ? Với test cuối, khi T rất lớn thì tích phân tiến tới giá trị nào?
