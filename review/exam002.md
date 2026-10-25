# Bộ bài tập Java — 5 bài

| # | Bài | Chủ đề | Mức độ |
|---|-----|--------|--------|
| 1 | Lồng đèn lễ hội | ƯCLN / BCNN | Dễ |
| 2 | Đội robot | Kế thừa & đa hình | Dễ |
| 3 | Quét tự động | Đạo hàm & tích phân (FTC) | Trung bình |
| 4 | Lộ trình sạc pin | Tham lam | Trung bình |
| 5 | Khu pit yên tĩnh | Sắp xếp + tìm kiếm | Khá |

**Cách kiểm thử**
- **Bài 1–3:** viết hàm hoặc các lớp theo mô tả, sau đó dán các câu lệnh `assert` trong phần test vào hàm `main` của em. Chạy bằng lệnh `java -ea TenLop`, vì Java bỏ qua `assert` nếu không có `-ea`. Nếu chương trình không in ra gì và không xuất hiện `AssertionError` thì mọi test đều đã đạt.
- **Bài 4–5:** chương trình đọc dữ liệu từ đầu vào chuẩn và ghi kết quả ra đầu ra chuẩn.

---

## Bài 1 — Lồng đèn lễ hội

Một lễ hội đường phố có nhiều chiếc lồng đèn. Lồng đèn thứ *i* nhấp nháy mỗi **pᵢ** giây. Tại giây 0, tất cả lồng đèn cùng nhấp nháy và đám đông reo hò.

Viết hai hàm:

```java
static long firstSync(long... periods)        // giây đầu tiên > 0 mà mọi lồng đèn cùng nhấp nháy
static long cheers(long t, long... periods)   // số lần reo hò trong các giây 1..t
```

### Ràng buộc
- 1 ≤ số lồng đèn ≤ 10
- 1 ≤ pᵢ ≤ 10⁹, 1 ≤ t ≤ 10¹⁸
- Đảm bảo giá trị của `firstSync` ≤ 10¹⁸

### Test
```java
assert firstSync(4, 6, 10) == 60 && cheers(200, 4, 6, 10) == 3;   // reo hò ở giây 60, 120, 180
assert firstSync(4, 15) == 60 && cheers(59, 4, 15) == 0;
assert firstSync(6, 6, 6, 6) == 6 && cheers(100, 6, 6, 6, 6) == 16;
assert firstSync(999999937, 1000000000, 500000000) == 999999937000000000L;
```

### Gợi ý
- Hãy giải với hai lồng đèn trước. Đáp án cho ba lồng đèn có thể dựng từ đáp án cho hai không?
- Đáp án của test cuối vừa với kiểu `long`, nhưng điều đó không có nghĩa mọi giá trị trung gian cũng vậy.

---

## Bài 2 — Đội robot

Một câu lạc bộ robot có ba loại robot. Robot nào cũng có `battery` (pin) và `distance` (quãng đường đã đi), và robot nào cũng có thể **di chuyển** và **làm việc**. Mỗi loại làm những việc này theo cách khác nhau:

| Lớp | Pin tốn cho mỗi đơn vị di chuyển | Pin tốn khi làm việc | Thông điệp khi làm việc |
|-----|----------------------------------|----------------------|-------------------------|
| `WheelBot` | 1 | 1 | `"honks"` |
| `ArmBot` | 2 | 5 | `"lifts a cone"` |
| `Drone` | 3 | 4 | `"scans the field"` |

Viết một lớp cơ sở trừu tượng `Robot` và ba lớp con. Mỗi robot cần có:
- Một hàm khởi tạo nhận lượng pin ban đầu, ví dụ `new Drone(30)`.
- `int move(int d)`: đi được càng nhiều đơn vị nguyên càng tốt, tối đa `d`, sao cho pin không xuống dưới 0. Trả về quãng đường robot thực sự đã đi.
- `String work()`: nếu pin đủ cho việc làm, trừ pin và trả về thông điệp làm việc. Ngược lại, trả về `"low battery"` và không trừ pin.

### Test
```java
Robot zip = new WheelBot(10), hook = new ArmBot(20), eye = new Drone(9);
assert zip.move(4) == 4 && zip.battery == 6;
assert hook.move(5) == 5 && hook.work().equals("lifts a cone");
assert eye.move(5) == 3 && eye.battery == 0;          // 9 pin / 3 pin mỗi đơn vị
assert eye.work().equals("low battery");
assert hook.work().equals("lifts a cone") && hook.work().equals("low battery");
assert zip.work().equals("honks") && zip.battery == 5;

Robot sky = new Drone(30), rover = new WheelBot(3);
assert sky.work().equals("scans the field") && sky.move(100) == 8;
assert sky.battery == 2 && sky.work().equals("low battery");
assert rover.move(2) == 2 && rover.move(7) == 1 && rover.distance == 3;
```

### Gợi ý
- Đọc lại bảng. Điều gì thực sự khác nhau giữa ba loại robot: các *bước thực hiện*, hay chỉ là *các con số và câu chữ*?
- Nếu em thấy mình viết `if (this instanceof ...)` bên trong `move`, thì phần logic đó nên nằm trong lớp con.

---

## Bài 3 — Quét tự động (FTC)

Trong giai đoạn tự động (autonomous) của một trận đấu FTC, một robot trượt dọc theo một thanh ray thẳng. Bộ đo hành trình (odometry) báo **vận tốc** của robot (inch mỗi giây) là

> v(t) = a·t² + b·t + c, với 0 ≤ t ≤ T

Vận tốc dương nghĩa là "tiến", vận tốc âm nghĩa là "lùi".

Viết hàm:

```java
static double[] sweep(double a, double b, double c, double T)
```

Hàm trả về `{displacement, totalDistance, peakSpeed}`:
1. **Độ dời:** robot kết thúc cách vị trí xuất phát bao xa (có dấu).
2. **Tổng quãng đường:** bánh xe thực sự đã lăn được bao nhiêu inch.
3. **Tốc độ cực đại:** giá trị lớn nhất của |v(t)| trong suốt lượt chạy.

### Ràng buộc
- |a|, |b|, |c| ≤ 100, 0 < T ≤ 30
- Đáp án được chấp nhận nếu sai số không quá 0.001

### Test
```java
// hàm phụ: trả về true nếu mọi giá trị trong got lệch không quá 0.001 so với want
static boolean close(double[] got, double... want)

assert close(sweep(0, 0, 12, 2.5), 30, 30, 12);    // tốc độ không đổi, không bao giờ quay lại
assert close(sweep(0, -4, 8, 3), 6, 10, 8);        // tiến 8 inch, rồi lùi 2 inch
assert close(sweep(-3, 12, 0, 5), 25, 39, 15);
assert close(sweep(1, -6, 8, 5), 6.667, 9.333, 8);
```

### Gợi ý
- Độ dời và quãng đường chỉ khác nhau khi robot quay đầu. Khi nào điều đó xảy ra?
- Test thứ ba là cái bẫy cho những ai chỉ kiểm tra một ứng viên duy nhất cho tốc độ cực đại.

---

## Bài 4 — Lộ trình sạc pin

Một robot giao hàng phải đi trên một con đường thẳng từ vị trí **0** đến vị trí **D**. Robot xuất phát với pin đầy, và một lần sạc đầy cho phép đi được **R** đơn vị. Dọc đường có **N** trạm sạc; sạc ở bất kỳ trạm nào cũng làm pin đầy lại hoàn toàn.

Tìm **số lần dừng sạc ít nhất** để đến được D, và liệt kê các trạm đã dùng. Nếu có nhiều lộ trình cùng dùng số lần dừng ít nhất, in ra lộ trình có dãy vị trí **lớn nhất theo thứ tự từ điển**. Nếu không thể đến D, in ra `-1`.

### Đầu vào
- Dòng 1: ba số nguyên `D R N`
- Dòng 2: `N` số nguyên — vị trí các trạm sạc (không nhất thiết đã sắp xếp, có thể trùng nhau)

### Đầu ra
- Nếu không đến được: một dòng duy nhất `-1`
- Ngược lại: dòng 1 là số lần dừng `k`, và nếu `k > 0`, dòng 2 là `k` vị trí trạm theo thứ tự đi qua

### Ràng buộc
- 1 ≤ D, R ≤ 10⁹
- 1 ≤ N ≤ 2·10⁵
- 0 < vị tríᵢ < D

### Test

**Test 1**
```
Đầu vào
25 10 5
4 9 13 18 22

Đầu ra
2
9 18
```
*Dừng ở trạm 4 hoặc 13 vẫn có thể đi tiếp, nhưng không bao giờ giúp bớt được một lần dừng.*

**Test 2**
```
Đầu vào
100 30 6
90 25 60 30 55 30

Đầu ra
3
30 60 90
```

**Test 3**
```
Đầu vào
50 10 3
5 10 25

Đầu ra
-1
```

**Test 4**
```
Đầu vào
8 10 2
3 5

Đầu ra
0
```

### Gợi ý
- Ở mỗi lần quyết định, lựa chọn nào giữ lại cho em nhiều phương án nhất về sau?
- Hãy đảm bảo mỗi trạm chỉ được xét một số lần cố định — N có thể rất lớn.

---

## Bài 5 — Khu pit yên tĩnh

Tại một cuộc thi robot, khu pit là một dãy dài gồm **N** chiếc bàn đặt ở các vị trí cho trước dọc hành lang. Năm nay có **K** đội có robot đặc biệt ồn ào, và ban tổ chức muốn xếp cho mỗi đội một bàn sao cho các đội ồn ào ở càng xa nhau càng tốt.

Chọn K bàn (mỗi bàn một đội) sao cho **khoảng cách nhỏ nhất giữa hai bàn bất kỳ được chọn là lớn nhất có thể**. In ra khoảng cách nhỏ nhất lớn nhất đó.

### Đầu vào
- Dòng 1: hai số nguyên `N K`
- Dòng 2: `N` số nguyên — vị trí các bàn (chưa sắp xếp, có thể trùng nhau)

### Đầu ra
Một số nguyên: khoảng cách nhỏ nhất lớn nhất có thể đạt được.

### Ràng buộc
- 2 ≤ K ≤ N ≤ 10⁵
- 0 ≤ vị tríᵢ ≤ 10⁹

### Test

**Test 1**
```
Đầu vào
5 3
1 2 8 4 9

Đầu ra
3
```
*Chọn các bàn 1, 4 và 8 (hoặc 1, 4, 9) cho khoảng cách ít nhất là 3. Không cách chọn nào đạt được 4.*

**Test 2**
```
Đầu vào
6 4
0 0 0 10 10 20

Đầu ra
0
```

**Test 3**
```
Đầu vào
4 2
1000000000 0 500 7

Đầu ra
1000000000
```

**Test 4**
```
Đầu vào
7 3
10 1 17 3 25 30 12

Đầu ra
13
```

### Gợi ý
- Trả lời câu hỏi "khoảng cách tốt nhất là bao nhiêu?" thì khó. Trả lời câu hỏi "có đạt được khoảng cách *g* không?" thì dễ hơn nhiều.
- Nếu một khoảng cách nào đó đạt được, em có thể nói gì về mọi khoảng cách nhỏ hơn nó?
