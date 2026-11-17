# 40 bài tập tính toán Motor & Cảm biến — có bẫy, có lời giải Java

*Chuẩn bị cho mùa giải FTC 2026–2027 (BIOBUZZ) · luyện tập với virtual_robot, không cần robot thật*

> Bộ bài này đi tiếp sau **Phần 6** (`MOTOR_CAM_BIEN_BIOBUZZ_2026_2027.md`, lý thuyết và 47 bài) và **10 ví dụ code**
> (`VI_DU_MOTOR_CAM_BIEN_BIOBUZZ.md`). Ở đây chỉ có **tính toán**: 4 bộ, mỗi bộ 10 bài. Trong một bộ, bài sau chỉ đổi
> **một chi tiết** của bài trước — và chi tiết nhỏ ấy nhiều khi chính là cái bẫy. Mọi lời giải đều có code Java chạy
> được và kết quả in ra thật. Thông số phần cứng lấy từ trang của nhà sản xuất (xem [Nguồn](#nguon)).

## Cách học với bộ bài này

1. **Làm theo thứ tự** trong mỗi bộ. Dòng *Thay đổi so với bài …* ở đầu đề cho biết bài này khác bài trước ở đâu;
   phần còn lại giữ nguyên.
2. **Đọc đề thật kỹ.** Bài nào cũng có ít nhất một chỗ dễ sai: đơn vị, chiều của tỉ số truyền, góc mốc, dấu, giới
   hạn của phép đo, hoặc một chi tiết của Java. Tự giải ra con số (giấy, máy tính bỏ túi) rồi mới mở **Lời giải**.
3. Mỗi lời giải gồm: các bước tính, code Java, kết quả chạy, mục **Bẫy** (cách sai hay gặp và vì sao sai) và
   **Đáp số**. Cuối tài liệu có [bảng đáp số](#dap-so) và [danh sách 20 cái bẫy](#bay) để ôn trước giải.
4. Độ khó: ★ dễ · ★★ trung bình · ★★★ khá khó.

Trong lời văn dùng dấu phẩy thập phân (41,80); trong code và kết quả chạy dùng dấu chấm (41.80).

## Chạy code lời giải

Bốn file nằm trong `code/TeamCode/src/org/firstinspires/ftc/teamcode/course/tinhtoan/`:

| File | Bộ bài |
|---|---|
| `Bo1_Encoder.java` | Bộ 1 — encoder: tick, quãng đường, góc |
| `Bo2_VanToc.java` | Bộ 2 — vận tốc: RPM, tick/giây, bánh đà, giới hạn của phép đo |
| `Bo3_MomenDongDien.java` | Bộ 3 — mô-men, dòng điện, pin |
| `Bo4_CamBien.java` | Bộ 4 — cảm biến khoảng cách, IMU, màu |

Mỗi file có các **hằng số**, các **hàm dùng chung** (mỗi hàm là một công thức), một hàm `baiX_Y()` cho mỗi bài và hàm
`main` chạy cả 10 bài. Code chỉ dùng Java chuẩn, không cần robot hay SDK FTC.

- **Trong IntelliJ IDEA / Android Studio:** chép thư mục `code/` vào virtual_robot như các phần trước, mở file rồi bấm
  ▶ ở lề trái cạnh hàm `main`.
- **Không cần IDE:** mở cửa sổ dòng lệnh ở thư mục chứa file và gõ `java Bo1_Encoder.java` (JDK 11 trở lên chạy thẳng
  file nguồn, không cần biên dịch trước).

Kết quả in bằng tiếng Việt **không dấu** để cửa sổ console nào cũng hiển thị đúng. Muốn tự luyện thêm: sửa một con số
trong hàm (ví dụ đổi 24 in thành 30 in), tự tính trước, rồi chạy lại để so.

## Thông số dùng chung

**Motor goBILDA 5203 Yellow Jacket** (thông số ở 12 V). Encoder đếm 28 tick mỗi vòng **trục motor**, nên số tick mỗi
vòng **trục ra** = 28 × tỉ số hộp số. Tỉ số "tròn" trên tên sản phẩm chỉ là gần đúng; goBILDA công bố công thức chính
xác:

| Motor | Tỉ số chính xác | Không tải | tick/vòng trục ra | τ kẹt | I kẹt | I không tải |
|---|---|---|---|---|---|---|
| 1:1 | 1 | 6000 RPM | 28 | 1,47 kg·cm | 9,2 A | 0,25 A |
| 5,2:1 | 1 + 46/11 = 5,1818 | 1150 RPM | 145,1 | 7,9 kg·cm | 9,2 A | 0,25 A |
| 13,7:1 | (1 + 46/17)² = 13,7336 | 435 RPM | 384,5 | 18,7 kg·cm | 9,2 A | 0,25 A |
| 19,2:1 | (1 + 46/17)(1 + 46/11) = 19,2032 | 312 RPM | 537,7 | 24,3 kg·cm | 9,2 A | 0,25 A |
| 50,9:1 | (1 + 46/17)³ = 50,8950 | 117 RPM | 1425,1 | 68,4 kg·cm | 9,2 A | 0,25 A |

> **Một cái bẫy nhỏ ngay từ bảng thông số.** Một số trang bán lại (và bảng 6.2.6 của Phần 6) ghi motor 435 RPM có
> 383,6 tick/vòng, tức 28 × 13,7. Trang của goBILDA ghi **384,5** = 28 × 13,7336. Chênh 0,24 %: chạy 10 m lệch khoảng
> 2,4 cm. Tài liệu này dùng số của goBILDA.

**Encoder, cảm biến và các số liệu khác**

| Thiết bị / đại lượng | Số liệu | Bài |
|---|---|---|
| REV HD Hex Motor | 28 tick/vòng trục motor, 6000 RPM không tải | 1.3 |
| Cartridge REV UltraPlanetary | ghi "3:1" thật ra 84:29 = 2,8966 · "4:1" = 76:21 = 3,6190 · "5:1" = 68:13 = 5,2308 | 1.3 |
| REV Through Bore Encoder | 2048 chu kỳ = **8192 tick/vòng** | 2.7 |
| Pod odometry goBILDA 4-Bar | bánh 32 mm, 2000 tick/vòng ⇒ 19,894 tick/mm (hằng số `goBILDA_4_BAR_POD` của driver Pinpoint) | 1.4–1.6, 2.8 |
| Pod odometry goBILDA Swingarm | bánh 48 mm, 2000 tick/vòng ⇒ 13,263 tick/mm (`goBILDA_SWINGARM_POD`) | 1.5, 2.8 |
| Vận tốc encoder Hub gửi về | số nguyên **16 bit có dấu**: −32 768 … 32 767 tick/s. Theo chú thích trong mã RoadRunner, Hub đo trong cửa sổ 50 ms nên số đọc là bội số của 20 tick/s | 2.7–2.9 |
| REV 2m Distance Sensor (chip VL53L0X) | đo tới 2 m; góc nhìn **toàn phần** 25°; mỗi lần đo mặc định ≈ 33 ms | 4.1–4.8 |
| `AngleUnit.normalizeDegrees(x)` | đưa góc về khoảng [−180°, 180°) | 4.5 |
| POLLEN / NECTAR | bóng Ø ≈ 2,8 in / Ø ≈ 3,6 in | 2.10, 4.8 |
| Cầu chì của pin (R601) | 20 A | 3.10 |

Hằng số đổi đơn vị: 1 in = 25,4 mm · g = 9,81 m/s² · 1 kg·cm = 0,0980665 N·m · 1 vòng = 360° = 2π rad.

## Mục lục

| Bộ | Chủ đề | Bài |
|---|---|---|
| [1](#bo-1) | Encoder: từ tick tới quãng đường và góc | [1.1](#bai-1-1) · [1.2](#bai-1-2) · [1.3](#bai-1-3) · [1.4](#bai-1-4) · [1.5](#bai-1-5) · [1.6](#bai-1-6) · [1.7](#bai-1-7) · [1.8](#bai-1-8) · [1.9](#bai-1-9) · [1.10](#bai-1-10) |
| [2](#bo-2) | Vận tốc: RPM, tick/giây và giới hạn của phép đo | [2.1](#bai-2-1) · [2.2](#bai-2-2) · [2.3](#bai-2-3) · [2.4](#bai-2-4) · [2.5](#bai-2-5) · [2.6](#bai-2-6) · [2.7](#bai-2-7) · [2.8](#bai-2-8) · [2.9](#bai-2-9) · [2.10](#bai-2-10) |
| [3](#bo-3) | Mô-men, dòng điện và pin: tay quay đưa NECTAR lên FLOWER | [3.1](#bai-3-1) · [3.2](#bai-3-2) · [3.3](#bai-3-3) · [3.4](#bai-3-4) · [3.5](#bai-3-5) · [3.6](#bai-3-6) · [3.7](#bai-3-7) · [3.8](#bai-3-8) · [3.9](#bai-3-9) · [3.10](#bai-3-10) |
| [4](#bo-4) | Cảm biến: khoảng cách, IMU và màu | [4.1](#bai-4-1) · [4.2](#bai-4-2) · [4.3](#bai-4-3) · [4.4](#bai-4-4) · [4.5](#bai-4-5) · [4.6](#bai-4-6) · [4.7](#bai-4-7) · [4.8](#bai-4-8) · [4.9](#bai-4-9) · [4.10](#bai-4-10) |
| | [Bảng đáp số](#dap-so) · [20 cái bẫy cần nhớ](#bay) · [Nguồn](#nguon) | |

---

<a id="bo-1"></a>

## Bộ 1 — Encoder: từ tick tới quãng đường và góc

**Tình huống chung.** Robot mẫu có khung mecanum với 4 motor goBILDA 5203 **312 RPM** (537,7 tick/vòng), bánh
**104 mm** gắn thẳng vào trục motor. Các bài sau thêm dần pod odometry, một tay quay và một lift. Mọi mục tiêu chạy
bằng `RUN_TO_POSITION`; encoder được reset (`STOP_AND_RESET_ENCODER`) một lần ở đầu trận.

**Công thức cần nhớ**

```
tick/vòng trục ra     = 28 × tỉ số hộp số            (goBILDA 5203 và REV HD Hex: 28 tick/vòng trục motor)
tỉ số truyền ngoài G  = số răng bánh BỊ DẪN / số răng bánh DẪN       (G > 1: giảm tốc)
quãng đường mỗi vòng  = π × ĐƯỜNG KÍNH
tick                  = quãng đường / (π × D) × tick/vòng × G
tick (cơ cấu quay)    = (góc đích − góc lúc reset) / 360 × tick/vòng × G
góc từ tick           = góc lúc reset + tick / (tick/vòng × G / 360)
```

**Code dùng chung** (đầu file `Bo1_Encoder.java`):

```java
// ===== Hằng số =====
static final double MM_PER_IN = 25.4;          // 1 in = 25,4 mm (chính xác theo định nghĩa)

// goBILDA 5203: encoder đếm 28 tick mỗi vòng TRỤC MOTOR, nên tick/vòng ở TRỤC RA = 28 × tỉ số hộp số.
static final double TPR_312 = 537.7;           // 19,2:1 = (1+46/17)(1+46/11)  ->  28 × 19,2032 = 537,69
static final double TPR_117 = 1425.1;          // 50,9:1 = (1+46/17)^3          ->  28 × 50,8950 = 1425,06

// REV HD Hex: 28 tick mỗi vòng trục motor. Tỉ số THẬT của cartridge UltraPlanetary (theo REV):
static final double REV_HDHEX_CPR = 28;
static final double UP_4_1 = 76.0 / 21.0;      // ghi "4:1", thật 3,619:1
static final double UP_5_1 = 68.0 / 13.0;      // ghi "5:1", thật 5,231:1

// Pod odometry goBILDA: 2000 tick mỗi vòng bánh pod, không có hộp số.
static final double POD_CPR = 2000;
static final double POD_4BAR_MM = 32;          // 4-Bar Odometry Pod
static final double POD_SWINGARM_MM = 48;      // Swingarm Odometry Pod

static final double WHEEL_MM = 104;            // bánh mecanum của robot mẫu

// ===== Hàm dùng chung =====

/** Tick encoder cho mỗi inch. ratio = tỉ số truyền ngoài = số răng bánh BỊ DẪN / số răng bánh DẪN. */
static double ticksPerInch(double ticksPerRev, double wheelDiameterMm, double ratio) {
    double circumferenceIn = Math.PI * wheelDiameterMm / MM_PER_IN;   // chu vi = π × ĐƯỜNG KÍNH
    return ticksPerRev * ratio / circumferenceIn;
}

/** Tick encoder cho mỗi độ quay của cơ cấu (tay quay, khớp...). */
static double ticksPerDegree(double ticksPerRev, double ratio) {
    return ticksPerRev * ratio / 360.0;
}
```

---

<a id="bai-1-1"></a>

### Bài 1.1 ★ — Từ inch sang tick

**Đề.** Robot mẫu cần chạy thẳng **24 in**. Phải gọi `setTargetPosition(...)` với số nào cho cả bốn bánh?

<details>
<summary><b>Lời giải 1.1</b></summary>

1. **Đổi đường kính sang inch:** D = 104 / 25,4 = 4,0945 in.
2. **Chu vi:** C = π × D = 12,8632 in — mỗi vòng bánh, robot tiến 12,86 in.
3. **Số vòng bánh:** 24 / 12,8632 = 1,8658 vòng.
4. **Số tick:** 1,8658 × 537,7 = 1003,23.
5. **Làm tròn:** `setTargetPosition` nhận số nguyên ⇒ **1003**.

Kiểm tra nhanh bằng ước lượng: mỗi vòng bánh ≈ 13 in ≈ 540 tick, tức khoảng 42 tick/in ⇒ 24 in ≈ 1000 tick. Khớp.

```java
/** Bài 1.1 — Từ inch sang tick. */
static void bai1_1() {
    tieuDe("1.1");
    double wheelIn = WHEEL_MM / MM_PER_IN;            // Bước 1: đường kính sang inch
    double circumference = Math.PI * wheelIn;         // Bước 2: chu vi = π × D
    double revs = 24.0 / circumference;               // Bước 3: số vòng bánh
    double ticks = revs * TPR_312;                    // Bước 4: số tick (bánh gắn thẳng vào motor)
    int target = (int) Math.round(ticks);             // Bước 5: setTargetPosition() nhận số nguyên
    inRa("D = %.4f in, chu vi = %.4f in", wheelIn, circumference);
    inRa("So vong banh = %.4f", revs);
    inRa("So tick = %.2f -> lam tron %d", ticks, target);
    inRa("Kiem tra bang ham chung: 24 x %.4f = %.2f tick", ticksPerInch(TPR_312, WHEEL_MM, 1),
            24 * ticksPerInch(TPR_312, WHEEL_MM, 1));
    inRa("=> setTargetPosition(%d)", target);
}
```

Kết quả chạy:

```
D = 4.0945 in, chu vi = 12.8632 in
So vong banh = 1.8658
So tick = 1003.23 -> lam tron 1003
Kiem tra bang ham chung: 24 x 41.8014 = 1003.23 tick
=> setTargetPosition(1003)
```

> **Bẫy.** Đường kính cho bằng **mm** còn quãng đường bằng **inch**: phải đổi về cùng một đơn vị trước khi chia. Chu
> vi là π × **đường kính** (không phải π × bán kính, cũng không phải 2π × đường kính). Dùng `Math.round` thay cho ép
> kiểu `(int)`: ở đây 1003,23 thì như nhau, nhưng 1003,9 ép `(int)` sẽ thành 1003 thay vì 1004.

**Đáp số:** `setTargetPosition(1003)` — hệ số **41,80 tick/in** sẽ dùng lại ở các bài sau.

</details>

<a id="bai-1-2"></a>

### Bài 1.2 ★ — Đi tiếp 18 in

*Thay đổi so với bài 1.1: robot vừa tới mốc 24 in, encoder **không** reset. Giờ robot đi tiếp **18 in** cùng hướng.*

**Đề.** Gọi `setTargetPosition(...)` với số nào?

<details>
<summary><b>Lời giải 1.2</b></summary>

1. Mục tiêu của `RUN_TO_POSITION` là **vị trí tuyệt đối** tính từ lần reset encoder, không phải "đi thêm bao nhiêu".
2. Tổng quãng đường kể từ lúc reset: 24 + 18 = 42 in.
3. Target = 42 × 41,8014 = 1755,66 ⇒ **1756**.

```java
/** Bài 1.2 — Đi tiếp 18 in: mục tiêu là vị trí TUYỆT ĐỐI tính từ lần reset encoder. */
static void bai1_2() {
    tieuDe("1.2");
    double tpi = ticksPerInch(TPR_312, WHEEL_MM, 1);
    int target1 = (int) Math.round(24 * tpi);              // mục tiêu bài 1.1
    int relative = (int) Math.round(18 * tpi);             // SAI nếu dùng thẳng: đây là số tương đối
    int correct = (int) Math.round((24 + 18) * tpi);       // ĐÚNG: tính từ TỔNG quãng đường
    int stoppedAt = 997;                                   // motor dừng trong dung sai, lệch 6 tick
    inRa("Tick moi inch = %.4f", tpi);
    inRa("SAI setTargetPosition(%d): robot quay LUI ve moc 18 in", relative);
    inRa("Cong hai so da lam tron: %d + %d = %d", target1, relative, target1 + relative);
    inRa("Lay vi tri dang dung %d + %d = %d (sai so dung sai bi cong don)", stoppedAt, relative,
            stoppedAt + relative);
    inRa("=> DUNG: 42 x %.4f = %.2f -> setTargetPosition(%d)", tpi, 42 * tpi, correct);
}
```

Kết quả chạy:

```
Tick moi inch = 41.8014
SAI setTargetPosition(752): robot quay LUI ve moc 18 in
Cong hai so da lam tron: 1003 + 752 = 1755
Lay vi tri dang dung 997 + 752 = 1749 (sai so dung sai bi cong don)
=> DUNG: 42 x 41.8014 = 1755.66 -> setTargetPosition(1756)
```

> **Bẫy.** Ba cách sai hay gặp:
> - Gọi `setTargetPosition(752)` (số tick của riêng 18 in): robot đang ở 1003 sẽ **chạy lùi** về mốc 18 in.
> - Cộng hai số đã làm tròn: 1003 + 752 = 1755 — chỉ lệch 1 tick, nhưng sau nhiều chặng sai số làm tròn cộng dồn.
> - Lấy `getCurrentPosition()` cộng thêm 752: motor thường dừng **trong dung sai** chứ không đúng 1003 (ví dụ 997),
>   nên được 1749; sau mỗi chặng lại hụt thêm vài tick.
>
> Cách đúng: tính mục tiêu mỗi chặng từ **tổng quãng đường** (hoặc từ mục tiêu cũ), không từ chỗ robot đang dừng.

**Đáp số:** `setTargetPosition(1756)`.

</details>

<a id="bai-1-3"></a>

### Bài 1.3 ★★ — REV HD Hex với hộp "20:1"

*Thay đổi so với bài 1.1: motor bánh là REV HD Hex (28 tick/vòng trục motor) gắn hộp UltraPlanetary hai tầng — một
cartridge **"4:1"** và một cartridge **"5:1"**, ngoài hộp ghi "20:1". Bánh vẫn 104 mm, vẫn chạy 24 in.*

**Đề.** a) Target là bao nhiêu? b) Nếu tính theo tỉ số 20:1 thì robot thật sự đi bao xa?

<details>
<summary><b>Lời giải 1.3</b></summary>

1. REV công bố tỉ số **thật** của từng cartridge: "4:1" là 76:21 ≈ 3,6190; "5:1" là 68:13 ≈ 5,2308.
2. Tỉ số cả hộp = 3,6190 × 5,2308 = **18,9304** (không phải 20).
3. tick/vòng trục ra = 28 × 18,9304 = 530,05.
4. a) tick mỗi inch = 530,05 / 12,8632 = 41,2068 ⇒ target = 24 × 41,2068 = 988,96 ⇒ **989**.
5. b) Theo số ghi: 28 × 20 = 560 tick/vòng ⇒ target 1045. Robot thật cần 41,2068 tick mỗi inch, nên đi
   1045 / 41,2068 = **25,36 in** — lố 1,36 in (5,7 %).

```java
/** Bài 1.3 — REV HD Hex + UltraPlanetary "4:1" và "5:1": số ghi trên hộp không phải tỉ số thật. */
static void bai1_3() {
    tieuDe("1.3");
    double ratioLabel = 4 * 5;                             // 20, con số "tròn" in trên hộp
    double ratioActual = UP_4_1 * UP_5_1;                  // 3,619 × 5,231
    double tprLabel = REV_HDHEX_CPR * ratioLabel;          // 560
    double tprActual = REV_HDHEX_CPR * ratioActual;        // ≈ 530
    double tpiActual = ticksPerInch(tprActual, WHEEL_MM, 1);
    int targetLabel = (int) Math.round(24 * ticksPerInch(tprLabel, WHEEL_MM, 1));
    int targetActual = (int) Math.round(24 * tpiActual);
    double travel = targetLabel / tpiActual;               // robot THẬT đi bao xa với target sai
    inRa("Ti so that = %.4f (ghi 20); tick/vong: that %.2f, theo so ghi %.0f", ratioActual, tprActual, tprLabel);
    inRa("Dung so ghi: target %d -> robot di that %d / %.4f = %.2f in (lo %.2f in)", targetLabel, targetLabel,
            tpiActual, travel, travel - 24);
    inRa("=> DUNG: 24 x %.4f = %.2f -> setTargetPosition(%d)", tpiActual, 24 * tpiActual, targetActual);
}
```

Kết quả chạy:

```
Ti so that = 18.9304 (ghi 20); tick/vong: that 530.05, theo so ghi 560
Dung so ghi: target 1045 -> robot di that 1045 / 41.2068 = 25.36 in (lo 1.36 in)
=> DUNG: 24 x 41.2068 = 988.96 -> setTargetPosition(989)
```

> **Bẫy.** Con số trên nhãn là **tên gọi**, không phải tỉ số. Với UltraPlanetary, sai lệch lên tới 5,7 % — đủ để
> robot lố hơn một inch sau 24 in. Luôn tra bảng tỉ số thật của nhà sản xuất (với goBILDA: công thức trong bảng thông
> số ở đầu tài liệu).

**Đáp số:** a) **989**; b) robot đi **25,36 in** (lố 1,36 in).

</details>

<a id="bai-1-4"></a>

### Bài 1.4 ★ — Pod odometry nói thật

*Thay đổi so với bài 1.1: robot có thêm một pod odometry goBILDA **4-Bar** (bánh 32 mm, 2000 tick/vòng) đặt song song
với hướng đi. Sau khi chạy xong bài 1.1, encoder motor đọc 1003 tick, còn pod đọc **11 950** tick.*

**Đề.** Robot thật sự đã đi bao xa? Vì sao khác với con số của encoder motor?

<details>
<summary><b>Lời giải 1.4</b></summary>

1. Pod **không có hộp số**: 2000 tick ứng với một vòng bánh 32 mm.
2. tick/mm = 2000 / (π × 32) = 19,8944 — đúng bằng hằng số `goBILDA_4_BAR_POD = 19.89436789` trong driver Pinpoint.
   Đổi sang inch: × 25,4 = 505,32 tick/in.
3. Robot đi: 11 950 / 505,32 = **23,65 in**.
4. Encoder motor: 1003 / 41,8014 = 23,99 in. Chênh **0,35 in (1,4 %)**: bánh mecanum trượt một chút khi tăng và giảm
   tốc. Pod không truyền lực kéo nên gần như không trượt.

```java
/** Bài 1.4 — Pod odometry 4-bar (32 mm, 2000 tick/vòng): tick -> quãng đường thật. */
static void bai1_4() {
    tieuDe("1.4");
    double podTpmm = POD_CPR / (Math.PI * POD_4BAR_MM);    // so với hằng số của Pinpoint: 19,894
    double podTpi = ticksPerInch(POD_CPR, POD_4BAR_MM, 1); // pod không có hộp số: ratio = 1
    double actualIn = 11950 / podTpi;                      // pod đọc 11 950 tick
    double motorIn = 1003 / ticksPerInch(TPR_312, WHEEL_MM, 1);  // encoder motor đọc 1003 tick
    inRa("Pod: %.4f tick/mm = %.2f tick/in", podTpmm, podTpi);
    inRa("Encoder motor noi %.2f in, pod noi %.2f in", motorIn, actualIn);
    inRa("=> Robot di that %.2f in; banh truot %.2f in (%.1f %%)", actualIn, motorIn - actualIn,
            100 * (motorIn - actualIn) / motorIn);
}
```

Kết quả chạy:

```
Pod: 19.8944 tick/mm = 505.32 tick/in
Encoder motor noi 23.99 in, pod noi 23.65 in
=> Robot di that 23.65 in; banh truot 0.35 in (1.4 %)
```

> **Bẫy.** Đừng nhân tỉ số hộp số của motor (19,2) cho pod, cũng đừng dùng đường kính bánh xe 104 mm. Encoder motor
> đo bánh **quay** bao nhiêu; pod đo robot **đi** bao nhiêu — đó là lý do các đội định vị bằng pod chứ không bằng encoder
> motor.

**Đáp số:** robot đi **23,65 in**; bánh trượt khoảng **0,35 in**.

</details>

<a id="bai-1-5"></a>

### Bài 1.5 ★★ — Đổi pod nhưng quên đổi hằng số

*Thay đổi so với bài 1.4: đội thay pod 4-Bar bằng pod **Swingarm** (bánh 48 mm, vẫn 2000 tick/vòng), nhưng trong code
vẫn để hằng số của pod 4-Bar. Robot đi đúng quãng đường như bài 1.4.*

**Đề.** a) Pod mới đếm được bao nhiêu tick? b) Code báo robot đi bao xa? c) Sửa thế nào?

<details>
<summary><b>Lời giải 1.5</b></summary>

1. Pod 48 mm: 2000 / (π × 48 / 25,4) = 336,88 tick/in.
2. a) Quãng đường vẫn 23,65 in ⇒ 23,65 × 336,88 = **7967 tick**.
3. b) Code chia cho 505,32 (hằng số của pod 32 mm) ⇒ **15,77 in**, thiếu 33,3 %. Robot tưởng mới đi được 2/3 quãng
   đường nên Autonomous sẽ chạy lố.
4. c) Dùng 336,88 tick/in. Với Pinpoint:
   `pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD)` ⇒ 23,65 in.

```java
/** Bài 1.5 — Đổi sang pod swingarm (48 mm) nhưng quên đổi hằng số trong code. */
static void bai1_5() {
    tieuDe("1.5");
    double distanceIn = 11950 / ticksPerInch(POD_CPR, POD_4BAR_MM, 1);   // cùng quãng đường bài 1.4
    double swingTpi = ticksPerInch(POD_CPR, POD_SWINGARM_MM, 1);
    long ticks = Math.round(distanceIn * swingTpi);                      // pod 48 mm đếm được
    double wrongIn = ticks / ticksPerInch(POD_CPR, POD_4BAR_MM, 1);       // code vẫn dùng hằng số 32 mm
    inRa("Pod 48 mm: %.2f tick/in -> dem duoc %d tick", swingTpi, ticks);
    inRa("Code van de hang so 4-bar: %.2f in (sai %.1f %%)", wrongIn, 100 * (wrongIn - distanceIn) / distanceIn);
    inRa("=> Dung hang so swingarm: %d / %.2f = %.2f in", ticks, swingTpi, ticks / swingTpi);
}
```

Kết quả chạy:

```
Pod 48 mm: 336.88 tick/in -> dem duoc 7967 tick
Code van de hang so 4-bar: 15.77 in (sai -33.3 %)
=> Dung hang so swingarm: 7967 / 336.88 = 23.65 in
```

> **Bẫy.** Sai số đúng bằng tỉ số hai đường kính: 32 / 48 = 2/3. Khi đổi bất kỳ phần cứng nào (pod, bánh, motor, hộp
> số), rà lại **mọi** hằng số liên quan trong code.

**Đáp số:** a) **7967 tick**; b) **15,77 in** (thiếu 33,3 %); c) dùng hằng số của pod Swingarm: 336,88 tick/in.

</details>

<a id="bai-1-6"></a>

### Bài 1.6 ★★ — Hai pod song song: robot đã quay bao nhiêu?

*Thay đổi so với bài 1.4: robot có **hai** pod 4-Bar song song với hướng tiến, một bên trái và một bên phải, cách nhau
**300 mm**. Robot quay tại chỗ: pod trái đọc **−2400** tick, pod phải **+2400** tick.*

**Đề.** Robot quay bao nhiêu độ, theo chiều nào?

<details>
<summary><b>Lời giải 1.6</b></summary>

1. Đổi tick sang mm (19,8944 tick/mm): pod trái −120,64 mm, pod phải +120,64 mm.
2. Khi robot quay, hai pod chạy trên hai cung tròn quanh tâm quay. Hiệu hai cung chia cho khoảng cách giữa hai pod là
   góc quay tính bằng **radian**: Δθ = (Δphải − Δtrái) / W = 241,27 / 300 = 0,80425 rad.
3. Đổi sang độ: 0,80425 × 180 / π = **46,08°**.
4. Pod phải tiến, pod trái lùi ⇒ robot quay **trái** (ngược chiều kim đồng hồ) — góc dương theo quy ước của IMU.

```java
/** Bài 1.6 — Hai pod song song cách nhau 300 mm: tính góc robot đã quay. */
static void bai1_6() {
    tieuDe("1.6");
    double tpmm = POD_CPR / (Math.PI * POD_4BAR_MM);   // tick mỗi mm của pod 4-bar
    double leftMm = -2400 / tpmm;                       // pod trái lùi
    double rightMm = 2400 / tpmm;                       // pod phải tiến
    double trackMm = 300;                               // khoảng cách GIỮA HAI pod
    double dThetaRad = (rightMm - leftMm) / trackMm;    // radian; dương = quay trái (ngược kim đồng hồ)
    inRa("Pod trai %.2f mm, pod phai %.2f mm", leftMm, rightMm);
    inRa("Goc quay = %.2f / %.0f = %.5f rad = %.2f do", rightMm - leftMm, trackMm, dThetaRad,
            Math.toDegrees(dThetaRad));
    inRa("SAI chia cho nua khoang cach (150 mm): %.2f do", Math.toDegrees((rightMm - leftMm) / 150));
}
```

Kết quả chạy:

```
Pod trai -120.64 mm, pod phai 120.64 mm
Goc quay = 241.27 / 300 = 0.80425 rad = 46.08 do
SAI chia cho nua khoang cach (150 mm): 92.16 do
```

> **Bẫy.** W là khoảng cách **giữa hai pod**, không phải từ tâm robot tới một pod — chia cho 150 mm ra 92,16°, gấp
> đôi. Phép chia "cung / bán kính" cho ra **radian**, phải đổi sang độ. Và hai số đọc phải đổi về mm trước khi chia
> cho W (đang tính bằng mm).

**Đáp số:** **46,08°**, quay trái.

</details>

<a id="bai-1-7"></a>

### Bài 1.7 ★★ — Tay quay: mốc không phải 0°

*Thay đổi so với bài 1.1: cơ cấu khác — một tay quay dùng motor **117 RPM** (1425,1 tick/vòng) và xích: bánh **15
răng** trên trục motor, **45 răng** trên trục tay. Lúc reset encoder, tay đang tựa vào chốt chặn ở **−30°** (dưới
phương nằm ngang). Cần đưa tay lên **+45°**.*

**Đề.** Target là bao nhiêu?

<details>
<summary><b>Lời giải 1.7</b></summary>

1. Tỉ số xích G = 45 / 15 = 3: motor quay 3 vòng thì tay quay 1 vòng.
2. tick mỗi độ của tay = 1425,1 × 3 / 360 = 11,8758.
3. Góc phải quay = 45 − (−30) = 75°.
4. Target = 75 × 11,8758 = 890,69 ⇒ **891**.

```java
/** Bài 1.7 — Tay quay: 117 RPM + xích 15→45 răng; encoder reset khi tay tựa ở −30°. */
static void bai1_7() {
    tieuDe("1.7");
    double ratio = 45.0 / 15.0;                        // bánh bị dẫn / bánh dẫn = 3
    double tpd = ticksPerDegree(TPR_117, ratio);       // tick mỗi độ của TAY
    double startDeg = -30, targetDeg = 45;
    int target = (int) Math.round((targetDeg - startDeg) * tpd);
    int noOffset = (int) Math.round(targetDeg * tpd);
    int ratioFlipped = (int) Math.round((targetDeg - startDeg) * ticksPerDegree(TPR_117, 15.0 / 45.0));
    inRa("Tick moi do cua tay = %.4f", tpd);
    inRa("SAI quen moc -30 do: target %d -> tay dung o %.1f do", noOffset, startDeg + noOffset / tpd);
    inRa("SAI dao nguoc ti so: target %d -> tay dung o %.1f do", ratioFlipped, startDeg + ratioFlipped / tpd);
    inRa("=> DUNG: (45 - (-30)) x %.4f = %.2f -> setTargetPosition(%d)", tpd, (targetDeg - startDeg) * tpd, target);
}
```

Kết quả chạy:

```
Tick moi do cua tay = 11.8758
SAI quen moc -30 do: target 534 -> tay dung o 15.0 do
SAI dao nguoc ti so: target 99 -> tay dung o -21.7 do
=> DUNG: (45 - (-30)) x 11.8758 = 890.69 -> setTargetPosition(891)
```

> **Bẫy.** Quên góc mốc −30° ⇒ target 534 ⇒ tay chỉ lên tới **15°**. Đảo ngược tỉ số (nhân 15/45) ⇒ target 99 ⇒ tay
> chỉ nhích lên **−21,7°**. Encoder chỉ biết "đã quay bao nhiêu kể từ lúc reset"; muốn nói chuyện bằng góc thật thì
> code phải nhớ góc lúc reset.

**Đáp số:** `setTargetPosition(891)` (11,876 tick mỗi độ của tay).

</details>

<a id="bai-1-8"></a>

### Bài 1.8 ★★ — Đọc ngược: tick thành góc

*Thay đổi so với bài 1.7: lần này đi theo chiều ngược lại. Giữa trận, `getCurrentPosition()` của tay trả về **1300**.*

**Đề.** Tay đang ở góc bao nhiêu? cos của góc đó dương hay âm? (Bộ 3 dùng cos θ để bù trọng lực, nên dấu này quyết
định motor đẩy về phía nào.)

<details>
<summary><b>Lời giải 1.8</b></summary>

1. Góc = góc mốc + tick / (tick mỗi độ) = −30 + 1300 / 11,8758 = **79,47°** — tay sắp tới phương thẳng đứng nhưng chưa
   qua.
2. cos 79,47° = **+0,1828** > 0: trọng lực vẫn kéo tay ngã về phía trước, lệnh bù trọng lực phải dương.
3. Nếu quên cộng mốc: 1300 / 11,8758 = 109,47° ⇒ cos = −0,3332 — **đổi dấu**. Code tưởng tay đã ngả ra sau nên đẩy
   ngược chiều, và tay rơi về phía trước.

```java
/** Bài 1.8 — Đổi ngược: tick đang đọc -> góc của tay (để tính bù trọng lực). */
static void bai1_8() {
    tieuDe("1.8");
    double tpd = ticksPerDegree(TPR_117, 3.0);
    int pos = 1300;                                    // getCurrentPosition()
    double angle = -30 + pos / tpd;                    // NHỚ cộng góc mốc lúc reset
    double noOffset = pos / tpd;
    inRa("Goc = -30 + %d / %.4f = %.2f do, cos = %+.4f", pos, tpd, angle, Math.cos(Math.toRadians(angle)));
    inRa("Quen moc: %.2f do, cos = %+.4f (doi dau!)", noOffset, Math.cos(Math.toRadians(noOffset)));
}
```

Kết quả chạy:

```
Goc = -30 + 1300 / 11.8758 = 79.47 do, cos = +0.1828
Quen moc: 109.47 do, cos = -0.3332 (doi dau!)
```

> **Bẫy.** Góc mốc phải có mặt ở **cả hai chiều** đổi: góc → tick (bài 1.7) và tick → góc (bài này). Nên viết hai hàm
> `angleToTicks` và `ticksToAngle` dùng chung một hằng số mốc, đừng viết rải rác.

**Đáp số:** **79,47°**, cos dương (+0,1828).

</details>

<a id="bai-1-9"></a>

### Bài 1.9 ★★★ — Lift nối cascade

*Thay đổi so với bài 1.1: chuyển động thẳng nhưng bằng dây thay cho bánh xe. Lift dùng motor 312 RPM, ống cuộn
**Ø 1,5 in**, nối kiểu **cascade** với **2 tầng di động**: tầng 1 đi theo dây, tầng 2 (mang khay) đi **nhanh gấp đôi**
tầng 1 so với khung đế. Cần nâng khay lên thêm **14,0 in**.*

**Đề.** Target là bao nhiêu?

<details>
<summary><b>Lời giải 1.9</b></summary>

1. Mỗi vòng ống cuộn kéo được π × 1,5 = 4,7124 in dây.
2. Cascade 2 tầng di động: khay đi 2 × 4,7124 = 9,4248 in mỗi vòng.
3. tick mỗi inch của khay = 537,7 / 9,4248 = **57,05** — bằng một nửa con số 114,10 của lift nối liền (continuous),
   lift ở ví dụ 4 và Phần 6.
4. Target = 14 × 57,05 = 798,7 ⇒ **799**.

```java
/** Bài 1.9 — Lift nối kiểu cascade, 2 tầng di động: khay đi gấp đôi chiều dài dây được cuộn. */
static void bai1_9() {
    tieuDe("1.9");
    double spoolIn = 1.5;
    double stringPerRev = Math.PI * spoolIn;           // dây cuộn được mỗi vòng ống cuộn
    double carriagePerRev = 2 * stringPerRev;          // cascade 2 tầng di động: khay đi gấp 2
    double tpiString = TPR_312 / stringPerRev;
    double tpiCarriage = TPR_312 / carriagePerRev;
    int target = (int) Math.round(14.0 * tpiCarriage);
    int wrong = (int) Math.round(14.0 * tpiString);
    inRa("Day/vong = %.4f in, khay/vong = %.4f in", stringPerRev, carriagePerRev);
    inRa("Tick moi inch: theo day %.2f, theo khay %.2f", tpiString, tpiCarriage);
    inRa("SAI dung %.2f tick/in: target %d -> khay muon len %.1f in", tpiString, wrong, wrong / tpiCarriage);
    inRa("=> DUNG: 14 x %.2f = %.1f -> setTargetPosition(%d)", tpiCarriage, 14 * tpiCarriage, target);
}
```

Kết quả chạy:

```
Day/vong = 4.7124 in, khay/vong = 9.4248 in
Tick moi inch: theo day 114.10, theo khay 57.05
SAI dung 114.10 tick/in: target 1597 -> khay muon len 28.0 in
=> DUNG: 14 x 57.05 = 798.7 -> setTargetPosition(799)
```

> **Bẫy.** Chép hằng số 114,10 tick/in từ lift cũ ⇒ target 1597 ⇒ lift cố đi lên **28 in**: đâm vào đỉnh hành trình,
> motor kẹt, có thể làm robot vượt chiều cao cho phép. Nhớ luôn hệ quả đi kèm: với cascade 2 tầng, khay nhanh gấp đôi
> thì dây phải kéo **mạnh gấp đôi** trọng lượng khay (cùng một công suất).

**Đáp số:** `setTargetPosition(799)` (57,05 tick/in).

</details>

<a id="bai-1-10"></a>

### Bài 1.10 ★★★ — Đọc code: vì sao robot lố nửa inch?

*Thay đổi so với bài 1.1: vẫn chạy 24 in, nhưng lần này đề bài là đoạn code của bạn A:*

```java
static final double TICKS_PER_REV = 537;           // motor 312 RPM
static final double WHEEL_DIAMETER_IN = 104 / 25;  // bánh 104 mm
static final double TICKS_PER_IN = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_IN);
// ...
motor.setTargetPosition((int) (24 * TICKS_PER_IN));
```

**Đề.** a) Target thực sự là bao nhiêu? b) Robot thật đi bao xa? c) Tìm và sửa các lỗi.

<details>
<summary><b>Lời giải 1.10</b></summary>

1. `104 / 25`: cả hai đều là số nguyên nên Java **chia lấy phần nguyên** ⇒ 4, rồi mới đổi sang `double` 4.0. (Thêm một
   lỗi nữa: 25 chứ không phải 25,4.) Đường kính đúng là 4,0945 in.
2. TICKS_PER_IN = 537 / (π × 4) = 42,7331, trong khi số đúng là 41,8014.
3. 24 × 42,7331 = 1025,59 ⇒ `(int)` **cắt** phần lẻ ⇒ a) **1025**.
4. b) Robot thật: 1025 / 41,8014 = **24,52 in** — lố 0,52 in.
5. Phân tích: dùng đường kính 4 thay cho 4,0945 in làm số tick **tăng** 2,4 %; số 537 thay cho 537,7 làm số tick
   **giảm** 0,13 %; `(int)` bớt thêm 0,59 tick. Hai lỗi đầu ngược chiều nhau nhưng lỗi chia số nguyên lớn hơn hẳn ⇒
   lố 2,2 %.
6. c) Sửa: `104 / 25.4` (có một số thực là đủ để Java chia số thực), `537.7`, và `(int) Math.round(...)` ⇒ 1003.

```java
/** Bài 1.10 — Đọc code: hai lỗi nhỏ của Java làm robot đi lố. */
static void bai1_10() {
    tieuDe("1.10");
    // ---- Code của bạn A (có lỗi) ----
    final double TICKS_PER_REV_A = 537;              // lỗi 1: bỏ mất 0,7
    final double WHEEL_DIAMETER_IN_A = 104 / 25;     // lỗi 2: 104/25 là chia SỐ NGUYÊN = 4
    double ticksPerInA = TICKS_PER_REV_A / (Math.PI * WHEEL_DIAMETER_IN_A);
    int targetA = (int) (24 * ticksPerInA);          // lỗi 3 (nhỏ): (int) cắt phần lẻ, không làm tròn
    // ---- Code đã sửa ----
    final double TICKS_PER_REV = 537.7;
    final double WHEEL_DIAMETER_IN = 104 / 25.4;     // 25.4 là số thực => phép chia số thực
    double ticksPerIn = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_IN);
    int target = (int) Math.round(24 * ticksPerIn);
    inRa("Ban A: D = %.4f in, %.4f tick/in, 24 x %.4f = %.2f -> (int) %d", WHEEL_DIAMETER_IN_A, ticksPerInA,
            ticksPerInA, 24 * ticksPerInA, targetA);
    inRa("Robot that di: %d / %.4f = %.2f in (lo %.2f in)", targetA, ticksPerIn, targetA / ticksPerIn,
            targetA / ticksPerIn - 24);
    inRa("=> Da sua: D = %.4f in, %.4f tick/in, target = %d", WHEEL_DIAMETER_IN, ticksPerIn, target);
}
```

Kết quả chạy:

```
Ban A: D = 4.0000 in, 42.7331 tick/in, 24 x 42.7331 = 1025.59 -> (int) 1025
Robot that di: 1025 / 41.8014 = 24.52 in (lo 0.52 in)
=> Da sua: D = 4.0945 in, 41.8014 tick/in, target = 1003
```

> **Bẫy.** Phép chia số nguyên của Java không báo lỗi, không cảnh báo — nó chỉ âm thầm làm sai. Mỗi khi chia, hỏi:
> "hai số này có phải đều là `int` không?". Cách an toàn: viết hằng số thực có dấu chấm (`104.0`, `25.4`).

**Đáp số:** a) **1025**; b) **24,52 in** (lố 0,52 in); c) sửa `25` → `25.4`, `537` → `537.7`, `(int)` →
`(int) Math.round(...)`.

</details>

---

<a id="bo-2"></a>

## Bộ 2 — Vận tốc: RPM, tick/giây và giới hạn của phép đo

**Tình huống chung.** Launcher của robot mẫu: motor goBILDA 5203 **6000 RPM** (1:1, 28 tick/vòng) quay một bánh đà
**Ø 96 mm**, chạy `RUN_USING_ENCODER` và điều khiển bằng `setVelocity`. Như Phần 6, vận tốc phần tử bay ra là
v = η × ω × r với η = 0,5, ω tính bằng rad/s và r là **bán kính** bánh đà. Bánh đà quay 2400 RPM thì POLLEN bay
khoảng 6 m/s.

**Công thức cần nhớ**

```
tick/s   = RPM / 60 × tick/vòng                 RPM = tick/s × 60 / tick/vòng
rad/s    = RPM × 2π / 60
v (m/s)  = η × ω (rad/s) × r (m)
dây đai  : RPM_bánh đà = RPM_motor × răng puli motor / răng puli bánh đà
F (PIDF) = 32767 / (tick/s lớn nhất ĐO ĐƯỢC ở công suất 1)
vận tốc Hub gửi về: số nguyên 16 bit có dấu, −32 768 … 32 767 tick/s
xung ngắn hơn chu kỳ đọc T:  xác suất thấy = thời gian xung / T
```

**Code dùng chung** (đầu file `Bo2_VanToc.java`):

```java
// ===== Hằng số =====
static final double MM_PER_IN = 25.4;
static final double TPR_6000 = 28;              // goBILDA 5203 1:1 (6000 RPM): 28 tick/vòng
static final double FLYWHEEL_R_M = 0.048;       // bánh đà Ø 96 mm => BÁN KÍNH 0,048 m
static final double ETA = 0.5;                  // v_phần tử = ETA × ω × r (mô hình của Phần 6)
static final double PULLEY_MOTOR = 20;          // puli 20 răng trên trục motor (bánh DẪN)
static final double PULLEY_FLYWHEEL = 30;       // puli 30 răng trên trục bánh đà (bánh BỊ DẪN)
static final double THROUGH_BORE_CPR = 8192;    // REV Through Bore Encoder: 8192 tick/vòng
static final double POD_CPR = 2000;             // pod odometry goBILDA: 2000 tick/vòng

// ===== Hàm dùng chung =====

/** RPM -> tick/giây: con số mà setVelocity(double) cần. */
static double rpmToTps(double rpm, double ticksPerRev) {
    return rpm / 60.0 * ticksPerRev;
}

/** tick/giây (getVelocity()) -> RPM. */
static double tpsToRpm(double tps, double ticksPerRev) {
    return tps * 60.0 / ticksPerRev;
}

/** RPM -> rad/s. */
static double rpmToRadPerSec(double rpm) {
    return rpm * 2 * Math.PI / 60.0;
}

/** Giá trị Hub gửi về khi vận tốc thật vượt quá số 16 bit có dấu: phép ép kiểu (short) của Java cắt y hệt. */
static int asHubVelocity(long trueTicksPerSecond) {
    return (short) trueTicksPerSecond;
}
```

---

<a id="bai-2-1"></a>

### Bài 2.1 ★ — RPM hay tick/giây?

**Đề.** Bánh đà gắn thẳng vào trục motor. Cần bánh đà quay **2400 RPM**. Viết lệnh `launcher.setVelocity(...)`.

<details>
<summary><b>Lời giải 2.1</b></summary>

1. 2400 RPM = 2400 / 60 = 40 vòng mỗi giây.
2. Mỗi vòng 28 tick ⇒ 40 × 28 = **1120 tick/s**.
3. `launcher.setVelocity(1120)`. Phần tử bay 0,5 × (2400 × 2π/60) × 0,048 = 6,03 m/s.

```java
/** Bài 2.1 — Bánh đà gắn thẳng: setVelocity() nhận tick/giây, không nhận RPM. */
static void bai2_1() {
    tieuDe("2.1");
    double tps = rpmToTps(2400, TPR_6000);            // 2400/60 vòng/s × 28 tick/vòng
    double wrongRpm = tpsToRpm(2400, TPR_6000);       // nếu lỡ gọi setVelocity(2400)
    inRa("2400 RPM = %.0f vong/s x 28 = %.0f tick/s", 2400 / 60.0, tps);
    inRa("SAI setVelocity(2400): 2400 tick/s = %.0f RPM, nhanh gap %.2f lan", wrongRpm, wrongRpm / 2400);
    inRa("Van toc phan tu: dung %.2f m/s, sai %.2f m/s", ETA * rpmToRadPerSec(2400) * FLYWHEEL_R_M,
            ETA * rpmToRadPerSec(wrongRpm) * FLYWHEEL_R_M);
    inRa("=> launcher.setVelocity(%.0f)", tps);
}
```

Kết quả chạy:

```
2400 RPM = 40 vong/s x 28 = 1120 tick/s
SAI setVelocity(2400): 2400 tick/s = 5143 RPM, nhanh gap 2.14 lan
Van toc phan tu: dung 6.03 m/s, sai 12.93 m/s
=> launcher.setVelocity(1120)
```

> **Bẫy.** `setVelocity(double)` nhận **tick/giây**, không nhận RPM. Gọi `setVelocity(2400)` thì motor quay
> 2400 tick/s = 5143 RPM — nhanh gấp 2,14 lần, phần tử bay 12,9 m/s và văng khỏi mục tiêu. Code vẫn biên dịch, không có
> lỗi nào báo ra.

**Đáp số:** `launcher.setVelocity(1120)`.

</details>

<a id="bai-2-2"></a>

### Bài 2.2 ★ — Thêm dây đai giảm tốc

*Thay đổi so với bài 2.1: motor nối với bánh đà bằng dây đai — puli **20 răng** trên trục motor, puli **30 răng** trên
trục bánh đà (giảm tốc để bánh đà có thêm mô-men, hồi tốc nhanh hơn sau mỗi phát). Bánh đà vẫn cần 2400 RPM.*

**Đề.** `launcher.setVelocity(...)` với số nào?

<details>
<summary><b>Lời giải 2.2</b></summary>

1. Encoder gắn ở **trục motor**, nên `setVelocity` nói về tốc độ của motor, không phải của bánh đà.
2. Bánh đà quay chậm hơn motor 30 / 20 = 1,5 lần ⇒ motor phải quay 2400 × 1,5 = **3600 RPM**.
3. 3600 / 60 × 28 = **1680 tick/s**.

```java
/** Bài 2.2 — Thêm dây đai 20 → 30 răng: encoder vẫn đo trục MOTOR. */
static void bai2_2() {
    tieuDe("2.2");
    double ratio = PULLEY_FLYWHEEL / PULLEY_MOTOR;    // 1,5: bánh đà quay CHẬM hơn motor 1,5 lần
    double motorRpm = 2400 * ratio;                   // motor phải quay nhanh hơn bánh đà
    double tps = rpmToTps(motorRpm, TPR_6000);
    double wrongMotorRpm = 2400 / ratio;              // SAI: chia thay vì nhân
    inRa("Motor = 2400 x %.1f = %.0f RPM -> %.0f tick/s", ratio, motorRpm, tps);
    inRa("SAI chia cho %.1f: motor %.0f RPM -> banh da chi %.0f RPM", ratio, wrongMotorRpm, wrongMotorRpm / ratio);
    inRa("=> launcher.setVelocity(%.0f)", tps);
}
```

Kết quả chạy:

```
Motor = 2400 x 1.5 = 3600 RPM -> 1680 tick/s
SAI chia cho 1.5: motor 1600 RPM -> banh da chi 1067 RPM
=> launcher.setVelocity(1680)
```

> **Bẫy.** Chiều của tỉ số: puli **nhỏ** dẫn puli **lớn** thì bên bị dẫn quay **chậm** hơn. Chia nhầm cho 1,5 ⇒ motor
> 1600 RPM ⇒ bánh đà chỉ 1067 RPM, phần tử rơi trước mục tiêu. Mẹo kiểm tra: motor 6000 RPM không tải; 3600 RPM là
> 60 % — hợp lý cho một launcher còn dư tốc độ để hồi.

**Đáp số:** `launcher.setVelocity(1680)`.

</details>

<a id="bai-2-3"></a>

### Bài 2.3 ★ — Đọc ngược ra vận tốc phần tử

*Thay đổi so với bài 2.2: pin đã yếu, `launcher.getVelocity()` chỉ đọc được **1540** tick/s.*

**Đề.** Lúc này phần tử bay ra nhanh bao nhiêu m/s?

<details>
<summary><b>Lời giải 2.3</b></summary>

1. Motor: 1540 × 60 / 28 = 3300 RPM.
2. Bánh đà (qua dây đai): 3300 × 20 / 30 = 2200 RPM.
3. Đổi sang rad/s: ω = 2200 × 2π / 60 = 230,38 rad/s.
4. v = 0,5 × 230,38 × 0,048 = **5,53 m/s** (thiếu khoảng 8 % so với 6,03 m/s).

```java
/** Bài 2.3 — Đọc ngược: getVelocity() = 1540 tick/s thì phần tử bay ra nhanh bao nhiêu? */
static void bai2_3() {
    tieuDe("2.3");
    double motorRpm = tpsToRpm(1540, TPR_6000);
    double flyRpm = motorRpm * PULLEY_MOTOR / PULLEY_FLYWHEEL;
    double omega = rpmToRadPerSec(flyRpm);
    double v = ETA * omega * FLYWHEEL_R_M;
    inRa("Motor = 1540 x 60 / 28 = %.0f RPM; banh da = %.0f x 20/30 = %.0f RPM", motorRpm, motorRpm, flyRpm);
    inRa("omega = %.0f x 2pi/60 = %.2f rad/s", flyRpm, omega);
    inRa("v = %.1f x %.2f x %.3f = %.2f m/s", ETA, omega, FLYWHEEL_R_M, v);
    inRa("SAI dung duong kinh 0.096 m: %.2f m/s | SAI dung RPM thay rad/s: %.1f m/s",
            ETA * omega * 0.096, ETA * flyRpm * FLYWHEEL_R_M);
}
```

Kết quả chạy:

```
Motor = 1540 x 60 / 28 = 3300 RPM; banh da = 3300 x 20/30 = 2200 RPM
omega = 2200 x 2pi/60 = 230.38 rad/s
v = 0.5 x 230.38 x 0.048 = 5.53 m/s
SAI dung duong kinh 0.096 m: 11.06 m/s | SAI dung RPM thay rad/s: 52.8 m/s
```

> **Bẫy.** Ba chỗ dễ sai: dùng **đường kính** 0,096 m ⇒ 11,06 m/s (gấp đôi); nhân thẳng RPM thay cho rad/s ⇒ 52,8 m/s
> — con số vô lý, phải nghi ngay; quên dây đai ⇒ lấy 3300 RPM của motor làm tốc độ bánh đà.

**Đáp số:** **5,53 m/s**.

</details>

<a id="bai-2-4"></a>

### Bài 2.4 ★★ — `setVelocity` với `AngleUnit`

*Thay đổi so với bài 2.2: bạn B thích viết bằng đơn vị góc: `launcher.setVelocity(x, AngleUnit.RADIANS)`.*

**Đề.** a) x bằng bao nhiêu để bánh đà quay 2400 RPM? b) Trong Robot Configuration, cổng motor này bị khai báo nhầm
thành loại motor 537,7 tick/vòng (loại 312 RPM). Lệnh của bạn B thành bao nhiêu tick/s, launcher chạy ra sao?

<details>
<summary><b>Lời giải 2.4</b></summary>

SDK đổi lệnh này sang tick/s bằng công thức: tick/s = x / 2π × (tick/vòng của **loại motor được khai báo**). Trong
virtual_robot có thể xem hàm `setVelocity(double angularRate, AngleUnit unit)` của `DcMotorExImpl`.

1. a) Góc ở đây là góc của **trục motor** (nơi có encoder). Motor cần 3600 RPM (bài 2.2) ⇒ x = 3600 × 2π / 60 =
   **376,99 rad/s**.
2. Nếu đưa 251,33 rad/s (tốc độ của **bánh đà**) ⇒ motor chỉ 2400 RPM ⇒ bánh đà 1600 RPM.
3. b) SDK nhân với 537,7 thay vì 28: 376,99 / 2π × 537,7 = **32 262 tick/s**. Motor thật tối đa khoảng 2800 tick/s nên
   vòng vận tốc không bao giờ đạt mục tiêu, cứ đẩy công suất lên 1: launcher chạy hết ga.

```java
/** Bài 2.4 — setVelocity(x, AngleUnit.RADIANS): góc của TRỤC MOTOR, đổi bằng tick/vòng của LOẠI MOTOR khai báo. */
static void bai2_4() {
    tieuDe("2.4");
    double motorRadS = rpmToRadPerSec(3600);          // bánh đà 2400 RPM <=> motor 3600 RPM (bài 2.2)
    double flyRadS = rpmToRadPerSec(2400);
    inRa("=> launcher.setVelocity(%.2f, AngleUnit.RADIANS)", motorRadS);
    inRa("SAI dua %.2f rad/s cua banh da: motor 2400 RPM -> banh da %.0f RPM", flyRadS, 2400 * PULLEY_MOTOR / PULLEY_FLYWHEEL);
    double tpsWrongType = motorRadS / (2 * Math.PI) * 537.7;   // SDK nhân với tick/vòng của loại motor khai báo
    inRa("Khai bao nham loai 537.7 tick/vong: lenh %.0f tick/s (motor that toi da ~2800) -> chay het ga", tpsWrongType);
}
```

Kết quả chạy:

```
=> launcher.setVelocity(376.99, AngleUnit.RADIANS)
SAI dua 251.33 rad/s cua banh da: motor 2400 RPM -> banh da 1600 RPM
Khai bao nham loai 537.7 tick/vong: lenh 32262 tick/s (motor that toi da ~2800) -> chay het ga
```

> **Bẫy.** Hai cái bẫy chồng lên nhau: `AngleUnit` là góc của trục **motor** chứ không phải cơ cấu sau dây đai; và
> phiên bản có `AngleUnit` phụ thuộc **loại motor khai báo trong cấu hình** — một chỗ không nằm trong code nên rất khó
> tìm. Sau khi đổi motor hay cấu hình, in thử `getMotorType().getTicksPerRev()` ra telemetry để kiểm tra.

**Đáp số:** a) `setVelocity(376.99, AngleUnit.RADIANS)`; b) lệnh thành 32 262 tick/s, launcher chạy hết ga.

</details>

<a id="bai-2-5"></a>

### Bài 2.5 ★★ — Hệ số F của vòng vận tốc

*Thay đổi so với bài 2.2: bắt đầu chỉnh PIDF. Cho launcher chạy `setPower(1.0)` (có bánh đà, dây đai, pin thật),
`getVelocity()` lớn nhất đo được là **2380** tick/s.*

**Đề.** a) Tính F. b) Bạn C không đo mà tính F theo thông số 6000 RPM. Với target 1680 tick/s, riêng phần F của bạn C
đưa motor tới khoảng bao nhiêu tick/s?

<details>
<summary><b>Lời giải 2.5</b></summary>

1. a) F = 32767 / 2380 = **13,768**. (Trong vòng vận tốc của Hub, 32767 ứng với công suất tối đa, nên phần F cho ra
   công suất ≈ F × target / 32767.)
2. b) Theo thông số: 6000 / 60 × 28 = 2800 tick/s ⇒ F = 32767 / 2800 = 11,703.
3. Phần F của bạn C cho công suất 11,703 × 1680 / 32767 = 0,600. Motor ở công suất 0,6 chỉ chạy khoảng
   0,6 × 2380 = **1428 tick/s** — thiếu 15 %. Phần I phải gánh nốt, nên bánh đà hồi tốc chậm sau mỗi phát.
4. Với F đo được: công suất 0,706 ⇒ đúng 1680 tick/s ngay từ phần F.

```java
/** Bài 2.5 — Hệ số F của vòng vận tốc: F = 32767 / (tick/s lớn nhất ĐO ĐƯỢC). */
static void bai2_5() {
    tieuDe("2.5");
    double measuredMax = 2380;                        // getVelocity() khi setPower(1.0), có bánh đà + dây đai
    double paperMax = rpmToTps(6000, TPR_6000);       // 2800 tick/s: thông số không tải
    double fMeasured = 32767 / measuredMax;
    double fPaper = 32767 / paperMax;
    double target = 1680;                             // tick/s (bài 2.2)
    // Phần F cho ra công suất ≈ F × target / 32767; motor đạt ≈ công suất × measuredMax
    double speedPaper = fPaper * target / 32767 * measuredMax;
    inRa("F (so do) = 32767 / %.0f = %.3f", measuredMax, fMeasured);
    inRa("F (tren giay) = 32767 / %.0f = %.3f", paperMax, fPaper);
    inRa("Voi F tren giay: phan F dua cong suat %.3f -> chi dat %.0f tick/s (thieu %.0f %%), phan I phai bu",
            fPaper * target / 32767, speedPaper, 100 * (1 - speedPaper / target));
    inRa("Voi F do duoc: cong suat %.3f -> %.0f tick/s", fMeasured * target / 32767, fMeasured * target / 32767 * measuredMax);
}
```

Kết quả chạy:

```
F (so do) = 32767 / 2380 = 13.768
F (tren giay) = 32767 / 2800 = 11.703
Voi F tren giay: phan F dua cong suat 0.600 -> chi dat 1428 tick/s (thieu 15 %), phan I phai bu
Voi F do duoc: cong suat 0.706 -> 1680 tick/s
```

> **Bẫy.** F lấy theo tick/s của **encoder đang được điều khiển** (encoder motor, 28 tick/vòng), đo **thật** khi có tải
> và pin thật. Thông số không tải luôn lạc quan hơn thực tế.

**Đáp số:** a) **F = 13,77**; b) phần F của bạn C chỉ đưa motor tới khoảng **1428 tick/s** (thiếu 15 %).

</details>

<a id="bai-2-6"></a>

### Bài 2.6 ★★ — Đổi hộp số thì F có đổi không?

*Thay đổi so với bài 2.5: đội cân nhắc dùng loại goBILDA 5203 khác cho các cơ cấu.*

**Đề.** Tính tick/s lớn nhất theo thông số (không tải) và F lý thuyết cho 5 loại: 6000, 1150, 435, 312, 117 RPM. Nhận
xét gì?

<details>
<summary><b>Lời giải 2.6</b></summary>

1. Áp dụng tick/s = RPM / 60 × tick/vòng cho từng loại (bảng kết quả chạy bên dưới).
2. Cả 5 loại đều cho **khoảng 2780–2800 tick/s**, F ≈ 11,7–11,8.
3. Lý do: encoder nằm ở **trục motor, trước hộp số**: 28 tick/vòng × 6000 RPM / 60 = 2800 tick/s, bất kể hộp số nào.
   Hộp số N:1 làm trục ra chậm N lần, nhưng mỗi vòng trục ra lại có nhiều hơn N lần tick — hai cái triệt tiêu nhau.
   Chênh lệch nhỏ đến từ việc RPM trong thông số đã được làm tròn.

```java
/** Bài 2.6 — Tick/giây lớn nhất (không tải) của 5 loại goBILDA 5203. */
static void bai2_6() {
    tieuDe("2.6");
    String[] name = {"6000 RPM (1:1)", "1150 RPM (5.2:1)", "435 RPM (13.7:1)", "312 RPM (19.2:1)", "117 RPM (50.9:1)"};
    double[] rpm = {6000, 1150, 435, 312, 117};
    double[] tpr = {28, 145.1, 384.5, 537.7, 1425.1};
    for (int i = 0; i < rpm.length; i++) {
        double tps = rpmToTps(rpm[i], tpr[i]);
        inRa("%-17s %6.1f tick/vong x %4.0f RPM / 60 = %6.1f tick/s -> F = %.2f", name[i], tpr[i], rpm[i], tps, 32767 / tps);
    }
    inRa("Trong ca 5: 28 tick/vong truc motor x 6000 RPM / 60 = %.0f tick/s", 28 * 6000 / 60.0);
}
```

Kết quả chạy:

```
6000 RPM (1:1)      28.0 tick/vong x 6000 RPM / 60 = 2800.0 tick/s -> F = 11.70
1150 RPM (5.2:1)   145.1 tick/vong x 1150 RPM / 60 = 2781.1 tick/s -> F = 11.78
435 RPM (13.7:1)   384.5 tick/vong x  435 RPM / 60 = 2787.6 tick/s -> F = 11.75
312 RPM (19.2:1)   537.7 tick/vong x  312 RPM / 60 = 2796.0 tick/s -> F = 11.72
117 RPM (50.9:1)  1425.1 tick/vong x  117 RPM / 60 = 2778.9 tick/s -> F = 11.79
Trong ca 5: 28 tick/vong truc motor x 6000 RPM / 60 = 2800 tick/s
```

> **Bẫy.** Tưởng motor 117 RPM "chậm" thì tick/s nhỏ và F phải lớn. F thật sự khác nhau giữa các cơ cấu là vì **tải**
> (bánh đà, ma sát, trọng lực) và **pin** — đó là lý do phải đo như bài 2.5.

**Đáp số:** cả 5 loại khoảng **2800 tick/s**, F lý thuyết ≈ **11,7**.

</details>

<a id="bai-2-7"></a>

### Bài 2.7 ★★★ — Encoder 8192 tick/vòng trên trục bánh đà

*Thay đổi so với bài 2.2: để đo thẳng tốc độ bánh đà, đội gắn REV Through Bore Encoder (8192 tick/vòng) lên trục bánh đà
và cắm vào một cổng encoder của Hub.*

**Đề.** a) Khi bánh đà quay đúng 2400 RPM, `getVelocity()` của cổng đó trả về bao nhiêu? b) Ở 2250 RPM thì sao?
c) RPM lớn nhất mà cổng này còn đo đúng?

<details>
<summary><b>Lời giải 2.7</b></summary>

Hub gửi vận tốc encoder dưới dạng **số nguyên 16 bit có dấu** (−32 768 … 32 767). Số lớn hơn bị "quấn vòng": chỉ còn
lại 16 bit thấp (phần dư khi chia cho 65 536), rồi đọc như số có dấu. Trong Java, phép ép kiểu `(short)` cắt đúng như
vậy. Đây cũng là lý do thư viện RoadRunner có lớp `OverflowEncoder`.

1. a) 2400 × 8192 / 60 = 327 680 tick/s = 5 × 65 536 + **0** ⇒ `getVelocity()` = **0**. Bánh đà đang quay đủ tốc mà
   code thấy nó đứng yên.
2. b) 2250 × 8192 / 60 = 307 200 = 4 × 65 536 + 45 056. Vì 45 056 > 32 767 nên số có dấu là 45 056 − 65 536 =
   **−20 480**: vận tốc **âm**.
3. c) 32 767 × 60 / 8192 = **240 RPM**.

Trên robot thật, tốc độ dao động liên tục nên số đọc nhảy lung tung giữa số dương và số âm — rất khó đoán ra nguyên
nhân nếu không biết giới hạn 16 bit.

```java
/** Bài 2.7 — Through Bore Encoder (8192 tick/vòng) trên trục bánh đà: tràn số 16 bit. */
static void bai2_7() {
    tieuDe("2.7");
    long tps2400 = Math.round(rpmToTps(2400, THROUGH_BORE_CPR));
    long tps2250 = Math.round(rpmToTps(2250, THROUGH_BORE_CPR));
    inRa("2400 RPM x 8192 / 60 = %d tick/s = %d x 65536 + %d", tps2400, tps2400 / 65536, tps2400 % 65536);
    inRa("=> getVelocity() bao %d (trong khi banh da dang quay du toc!)", asHubVelocity(tps2400));
    inRa("2250 RPM: that %d tick/s -> Hub bao %d (so AM)", tps2250, asHubVelocity(tps2250));
    inRa("RPM lon nhat con do dung = 32767 x 60 / 8192 = %.1f RPM", 32767 * 60.0 / THROUGH_BORE_CPR);
}
```

Kết quả chạy:

```
2400 RPM x 8192 / 60 = 327680 tick/s = 5 x 65536 + 0
=> getVelocity() bao 0 (trong khi banh da dang quay du toc!)
2250 RPM: that 307200 tick/s -> Hub bao -20480 (so AM)
RPM lon nhat con do dung = 32767 x 60 / 8192 = 240.0 RPM
```

> **Bẫy.** Một con số vượt quá kiểu dữ liệu chứa nó. Cách chữa: tự tính vận tốc từ `getCurrentPosition()` — số 32 bit,
> không tràn: (pos − lastPos) / Δt; hoặc dùng `OverflowEncoder` của RoadRunner; hoặc đo trên trục motor (28 tick/vòng).

**Đáp số:** a) **0**; b) **−20 480**; c) **240 RPM**.

</details>

<a id="bai-2-8"></a>

### Bài 2.8 ★★ — Pod odometry cắm thẳng vào Hub

*Thay đổi so với bài 2.7: cùng giới hạn 32 767 tick/s, nhưng là pod odometry (2000 tick/vòng) cắm thẳng vào cổng encoder
của Hub thay vì qua Pinpoint.*

**Đề.** Robot chạy nhanh hơn bao nhiêu in/s thì vận tốc của pod bị tràn, với a) pod 4-Bar (32 mm); b) pod Swingarm
(48 mm)? c) Robot dùng motor 435 RPM và bánh 104 mm có chạm ngưỡng không?

<details>
<summary><b>Lời giải 2.8</b></summary>

1. a) Pod 4-Bar: 505,32 tick/in (bài 1.4) ⇒ tràn khi v > 32 767 / 505,32 = **64,8 in/s**.
2. b) Pod Swingarm: 336,88 tick/in ⇒ tràn khi v > **97,3 in/s**.
3. c) Tốc độ không tải của robot: 435 / 60 × 12,8632 = **93,3 in/s** > 64,8 ⇒ với pod 4-Bar, vận tốc tràn khi robot
   chạy gần hết tốc; pod Swingarm vừa đủ.

Vị trí (`getCurrentPosition`) là số 32 bit nên vẫn đúng — chỉ **vận tốc** bị tràn. Nhưng bộ định vị của RoadRunner có
dùng vận tốc pod, nên quickstart của RoadRunner bọc mọi encoder pod bằng `OverflowEncoder`. Pinpoint tự đọc pod và tự
tính vận tốc nên không vướng giới hạn này của Hub.

```java
/** Bài 2.8 — Pod odometry cắm thẳng vào cổng encoder của Hub: robot chạy nhanh tới đâu thì vận tốc bị tràn? */
static void bai2_8() {
    tieuDe("2.8");
    double tpi4bar = POD_CPR / (Math.PI * 32 / MM_PER_IN);
    double tpiSwing = POD_CPR / (Math.PI * 48 / MM_PER_IN);
    double robotMax = 435 / 60.0 * Math.PI * 104 / MM_PER_IN;   // tốc độ không tải: motor 435 RPM, bánh 104 mm
    inRa("Pod 4-bar: %.2f tick/in -> tran khi v > 32767 / %.2f = %.1f in/s", tpi4bar, tpi4bar, 32767 / tpi4bar);
    inRa("Pod swingarm: %.2f tick/in -> tran khi v > %.1f in/s", tpiSwing, 32767 / tpiSwing);
    inRa("Robot 435 RPM, banh 104 mm: toi da %.1f in/s", robotMax);
}
```

Kết quả chạy:

```
Pod 4-bar: 505.32 tick/in -> tran khi v > 32767 / 505.32 = 64.8 in/s
Pod swingarm: 336.88 tick/in -> tran khi v > 97.3 in/s
Robot 435 RPM, banh 104 mm: toi da 93.3 in/s
```

> **Bẫy.** Pod bánh nhỏ cho nhiều tick mỗi inch hơn — "mịn" hơn — nhưng cũng **tràn sớm hơn**. Độ phân giải cao luôn
> đi kèm giới hạn tốc độ đo thấp hơn.

**Đáp số:** a) **64,8 in/s**; b) **97,3 in/s**; c) có, với pod 4-Bar (robot tới 93,3 in/s).

</details>

<a id="bai-2-9"></a>

### Bài 2.9 ★★ — Một "nấc" vận tốc bằng bao nhiêu?

*Thay đổi so với bài 2.2: trở lại encoder motor 28 tick/vòng, target 1680 tick/s. Như ví dụ 6, launcher chỉ đẩy phần
tử khi |v − target| < 1,5 % target.*

**Đề.** a) Nếu tự tính vận tốc = Δtick / Δt với Δt = 20 ms (một vòng lặp), mỗi nấc thay đổi nhỏ nhất là bao nhiêu
tick/s, bao nhiêu RPM motor, RPM bánh đà và bao nhiêu % target? b) Theo chú thích trong mã RoadRunner, Hub đo vận tốc
trong cửa sổ 50 ms nên `getVelocity()` luôn là bội số của 20 tick/s. Những số đọc nào lọt qua cửa sổ ±1,5 %? Nếu siết
lại còn ±1 % thì sao?

<details>
<summary><b>Lời giải 2.9</b></summary>

1. a) Trong 20 ms, số tick đếm được là số nguyên, nên nấc nhỏ nhất là 1 tick / 0,020 s = **50 tick/s** = 107,1 RPM motor
   = 71,4 RPM bánh đà = **3,0 % target**. Cả cửa sổ ±1,5 % (rộng 3 %) chỉ bằng đúng một nấc: kết quả "đủ tốc" sẽ bật
   tắt gần như ngẫu nhiên.
2. b) ±1,5 % của 1680 là ±25,2 tick/s. Các bội số của 20 nằm trong (1654,8; 1705,2) là **1660, 1680, 1700**. Số 1660
   thấp hơn mục tiêu 1,19 % vẫn được coi là "đủ tốc".
3. Siết còn ±1 % (±16,8 tick/s): chỉ **1680** lọt qua. Một dao động nhỏ là cổng đóng lại, launcher chờ mãi.

```java
/** Bài 2.9 — Độ phân giải của vận tốc: một "nấc" bằng bao nhiêu phần trăm? */
static void bai2_9() {
    tieuDe("2.9");
    double target = 1680;                                     // tick/s (bài 2.2)
    double step20ms = 1 / 0.020;                              // a) tự tính Δtick/Δt với Δt = 20 ms
    inRa("a) 1 tick / 0.020 s = %.0f tick/s = %.1f RPM motor = %.1f RPM banh da = %.1f %% muc tieu",
            step20ms, tpsToRpm(step20ms, TPR_6000), tpsToRpm(step20ms, TPR_6000) * PULLEY_MOTOR / PULLEY_FLYWHEEL,
            100 * step20ms / target);
    for (double pct : new double[]{1.5, 1.0}) {               // b) Hub: getVelocity() là bội số của 20 tick/s
        double band = pct / 100 * target;
        StringBuilder pass = new StringBuilder();
        for (int v = 1600; v <= 1760; v += 20) {
            if (Math.abs(v - target) < band) pass.append(v).append(' ');
        }
        inRa("b) Cua so +-%.1f %% = +-%.1f tick/s; so doc lot qua: %s", pct, band, pass.toString().trim());
    }
    inRa("   1660 tick/s = thap hon muc tieu %.2f %% nhung van duoc coi la 'du toc'", 100 * (target - 1660) / target);
}
```

Kết quả chạy:

```
a) 1 tick / 0.020 s = 50 tick/s = 107.1 RPM motor = 71.4 RPM banh da = 3.0 % muc tieu
b) Cua so +-1.5 % = +-25.2 tick/s; so doc lot qua: 1660 1680 1700
b) Cua so +-1.0 % = +-16.8 tick/s; so doc lot qua: 1680
   1660 tick/s = thap hon muc tieu 1.19 % nhung van duoc coi la 'du toc'
```

> **Bẫy.** Một yêu cầu tính bằng phần trăm chỉ có nghĩa khi nó rộng hơn vài nấc đo. Encoder 28 tick/vòng đo vận tốc khá
> thô. Cách chữa: đo trong cửa sổ thời gian dài hơn, thêm điều kiện "ổn định 60 ms" như ví dụ 6, hoặc dùng encoder
> nhiều tick hơn (nhưng nhớ bài 2.7).

**Đáp số:** a) **50 tick/s** = 107,1 RPM motor = 71,4 RPM bánh đà = **3,0 %**; b) **1660, 1680, 1700**; với ±1 % chỉ còn
**1680**.

</details>

<a id="bai-2-10"></a>

### Bài 2.10 ★★★ — Beam break ở cửa ra launcher

*Thay đổi so với bài 2.9: đội muốn đếm phần tử bay ra bằng một beam break đặt ngay cửa ra launcher. POLLEN Ø 2,8 in bay
ra với 6,0 m/s; vòng lặp 20 ms, mỗi vòng code đọc cảm biến một lần.*

**Đề.** a) Tia bị chắn trong bao lâu? b) Xác suất code bỏ lỡ một phần tử là bao nhiêu? c) Đề xuất cách sửa.

<details>
<summary><b>Lời giải 2.10</b></summary>

1. a) 2,8 in = 0,0711 m ⇒ tia bị chắn 0,0711 / 6,0 = **11,85 ms**.
2. b) Thời điểm code đọc rơi ngẫu nhiên trong chu kỳ 20 ms. Code chỉ "thấy" phần tử nếu lần đọc rơi đúng vào 11,85 ms
   tia bị chắn ⇒ xác suất thấy = 11,85 / 20 = 59,3 % ⇒ **bỏ lỡ 40,7 %**.
3. c) Muốn chắc chắn thấy, vòng lặp phải ≤ 11,85 ms — khó giữ khi robot còn camera và nhiều cảm biến I2C. Tốt hơn: đặt
   beam break ở **băng chuyển**, nơi phần tử chạy chậm (25 in/s ⇒ tia bị chắn 112 ms = 5,6 vòng lặp); hoặc đếm phát
   bắn bằng **cú tụt tốc** của bánh đà như ví dụ 6.

Ghi chú: ví dụ 9 đếm phần tử ra ở cửa launcher và chạy đúng trong mô phỏng vì BioBuzz Bot giữ tín hiệu `launch_beam`
khoảng 40 ms. Trên robot thật, xung chỉ dài cỡ 12 ms như tính ở trên — hãy dùng một trong hai cách ở bước 3.

```java
/** Bài 2.10 — Beam break ở cửa ra launcher: tia bị chắn bao lâu, vòng lặp 20 ms có bỏ lỡ không? */
static void bai2_10() {
    tieuDe("2.10");
    double diameterM = 2.8 * MM_PER_IN / 1000;                // POLLEN Ø 2,8 in -> mét
    double blockedMs = diameterM / 6.0 * 1000;                // bay ra 6,0 m/s
    double loopMs = 20;
    double pSee = Math.min(1, blockedMs / loopMs);
    inRa("Tia bi chan %.4f m / 6.0 m/s = %.2f ms; vong lap %.0f ms", diameterM, blockedMs, loopMs);
    inRa("Xac suat thay = %.1f %%, bo lo = %.1f %%", 100 * pSee, 100 * (1 - pSee));
    inRa("Can vong lap <= %.2f ms moi chac chan thay (it nhat 1 lan)", blockedMs);
    double transferMs = 2.8 / 25.0 * 1000;                    // đặt ở băng chuyển, 25 in/s
    inRa("Dat o bang chuyen 25 in/s: tia bi chan %.0f ms = %.1f vong lap", transferMs, transferMs / loopMs);
}
```

Kết quả chạy:

```
Tia bi chan 0.0711 m / 6.0 m/s = 11.85 ms; vong lap 20 ms
Xac suat thay = 59.3 %, bo lo = 40.7 %
Can vong lap <= 11.85 ms moi chac chan thay (it nhat 1 lan)
Dat o bang chuyen 25 in/s: tia bi chan 112 ms = 5.6 vong lap
```

> **Bẫy.** Dùng tốc độ **tại chỗ đặt cảm biến** (cửa ra: 6 m/s, băng chuyển: 25 in/s); đổi đúng in ↔ m; và nhớ rằng
> cảm biến số **không nhớ** điều gì xảy ra giữa hai lần đọc.

**Đáp số:** a) **11,85 ms**; b) bỏ lỡ **40,7 %**; c) đặt cảm biến ở băng chuyển (112 ms) hoặc đếm bằng cú tụt tốc.

</details>

---

<a id="bo-3"></a>

## Bộ 3 — Mô-men, dòng điện và pin: tay quay đưa NECTAR lên FLOWER

**Tình huống chung.** Tay quay dài **45 cm**, khối lượng **0,80 kg** (cả kẹp và một NECTAR), trọng tâm cách trục quay
**30 cm**. Motor goBILDA 5203 **117 RPM**; thông số ở 12 V: mô-men kẹt τ_kẹt = 68,4 kg·cm, dòng kẹt 9,2 A, dòng không
tải I₀ = 0,25 A. Góc θ đo từ phương **nằm ngang**, dương khi tay giơ lên. Dùng mô hình motor tuyến tính của Phần 6
(mục 6.3.1).

**Công thức cần nhớ**

```
mô-men trọng lực    τ_g = m × g × r_trọng tâm × cos θ           (θ so với phương NẰM NGANG)
đổi đơn vị          1 kg·cm = 0,0980665 N·m
qua giảm tốc G      τ_motor = τ_tay / G ;   ω_tay = ω_motor / G
giữ yên (ω = 0)     u = τ_motor / τ_kẹt                         (pin V volt: nhân thêm 12 / V)
dòng điện           I ≈ I₀ + (I_kẹt − I₀) × τ_motor / τ_kẹt
tốc độ              ω = ω₀ × (u × V/12 − τ_motor / τ_kẹt)
nhiệt cuộn dây      P = I² × R,   R ≈ 12 V / I_kẹt
công suất           P_cơ = τ × ω (cùng một trục) ;  P_điện = V × I ;  hiệu suất = P_cơ / P_điện
pin có nội trở      V = V_hở − R_pin × I_tổng
```

**Code dùng chung** (đầu file `Bo3_MomenDongDien.java`):

```java
// ===== Hằng số =====
static final double G = 9.81;                           // m/s²
static final double KGCM_TO_NM = 0.0980665;             // 1 kg·cm (thật ra là kgf·cm) = 0,0980665 N·m

// goBILDA 5203 50,9:1 (117 RPM), thông số ở 12 V của goBILDA
static final double STALL_NM = 68.4 * KGCM_TO_NM;       // mô-men kẹt 68,4 kg·cm = 6,708 N·m
static final double STALL_A = 9.2;                      // dòng kẹt
static final double FREE_A = 0.25;                      // dòng không tải
static final double FREE_RPM = 117;                     // tốc độ không tải
static final double WINDING_OHM = 12.0 / STALL_A;       // điện trở cuộn dây ≈ 12 V / 9,2 A

// Tay quay
static final double ARM_MASS_KG = 0.80;                 // tay + kẹp + 1 NECTAR
static final double ARM_LENGTH_M = 0.45;                // chiều dài tay
static final double ARM_CG_M = 0.30;                    // trọng tâm cách trục quay
static final double CHAIN = 45.0 / 15.0;                // xích 15 răng (motor) -> 45 răng (tay): giảm tốc 3:1

// ===== Hàm dùng chung =====

/** Phần mô-men kẹt mà tải đang chiếm: τ / τ_kẹt (mô-men tính ở TRỤC RA của motor). */
static double loadFraction(double torqueAtMotorNm) {
    return torqueAtMotorNm / STALL_NM;
}

/** Dòng điện (A) theo mô hình tuyến tính. */
static double current(double torqueAtMotorNm) {
    return FREE_A + (STALL_A - FREE_A) * loadFraction(torqueAtMotorNm);
}

/** Tốc độ trục motor (RPM) với lệnh u, điện áp pin V và mô-men tải τ. */
static double motorRpm(double u, double volts, double torqueAtMotorNm) {
    return FREE_RPM * (u * volts / 12.0 - loadFraction(torqueAtMotorNm));
}

/** Mô-men trọng lực (N·m) khi tay nghiêng góc thetaDeg so với phương NẰM NGANG. */
static double gravityTorque(double thetaDeg) {
    return ARM_MASS_KG * G * ARM_CG_M * Math.cos(Math.toRadians(thetaDeg));
}
```

---

<a id="bai-3-1"></a>

### Bài 3.1 ★ — Mô-men giữ tay nằm ngang

**Đề.** Tay đang nằm ngang. Trọng lực tạo ra mô-men bao nhiêu quanh trục quay? Trả lời bằng N·m và bằng kg·cm (đơn vị
trong bảng thông số motor).

<details>
<summary><b>Lời giải 3.1</b></summary>

1. Trọng lượng: W = 0,80 × 9,81 = 7,848 N.
2. Cánh tay đòn là khoảng cách từ trục tới **trọng tâm**: 0,30 m.
3. τ = 7,848 × 0,30 = **2,3544 N·m**.
4. Đổi sang kg·cm: 2,3544 / 0,0980665 = **24,01 kg·cm**.

```java
/** Bài 3.1 — Mô-men giữ tay ở phương nằm ngang. */
static void bai3_1() {
    tieuDe("3.1");
    double weightN = ARM_MASS_KG * G;                    // trọng lượng (N)
    double torque = weightN * ARM_CG_M;                  // cánh tay đòn = khoảng cách TRỌNG TÂM
    inRa("Trong luong = %.2f x %.2f = %.3f N", ARM_MASS_KG, G, weightN);
    inRa("tau = %.3f N x %.2f m = %.4f N.m = %.2f kg.cm", weightN, ARM_CG_M, torque, torque / KGCM_TO_NM);
    inRa("SAI dung chieu dai tay %.2f m: %.4f N.m (lon hon %.0f %%)", ARM_LENGTH_M, weightN * ARM_LENGTH_M,
            100 * (ARM_LENGTH_M / ARM_CG_M - 1));
}
```

Kết quả chạy:

```
Trong luong = 0.80 x 9.81 = 7.848 N
tau = 7.848 N x 0.30 m = 2.3544 N.m = 24.01 kg.cm
SAI dung chieu dai tay 0.45 m: 3.5316 N.m (lon hon 50 %)
```

> **Bẫy.** Dùng chiều dài tay 0,45 m ⇒ 3,53 N·m, lớn hơn 50 %. "kg·cm" trong bảng thông số thật ra là **kgf·cm** — lực
> nặng của 1 kg đặt cách trục 1 cm — nên phải có g khi đổi sang N·m.

**Đáp số:** **2,354 N·m = 24,0 kg·cm**.

</details>

<a id="bai-3-2"></a>

### Bài 3.2 ★ — Tay giơ lên, lệch 50° so với phương thẳng đứng

*Thay đổi so với bài 3.1: tay giơ lên, hợp với phương **thẳng đứng** một góc 50°.*

**Đề.** Mô-men trọng lực bây giờ là bao nhiêu?

<details>
<summary><b>Lời giải 3.2</b></summary>

1. Vẽ hình: chỉ khoảng cách **theo phương ngang** từ trục tới trọng tâm mới là cánh tay đòn của trọng lực.
2. Góc đo từ phương **đứng** ⇒ khoảng cách ngang = 0,30 × sin 50° = 0,30 × cos 40° = 0,2298 m.
3. τ = 7,848 × 0,2298 = **1,8036 N·m**. Hàm chung `gravityTorque(40)` (40° so với phương ngang) cho cùng kết quả.

```java
/** Bài 3.2 — Tay giơ lên, lệch 50° so với phương THẲNG ĐỨNG. */
static void bai3_2() {
    tieuDe("3.2");
    double fromVertical = 50;
    double fromHorizontal = 90 - fromVertical;            // 40° so với phương ngang
    double leverM = ARM_CG_M * Math.sin(Math.toRadians(fromVertical));   // = 0,30 × cos 40°
    double torque = ARM_MASS_KG * G * leverM;
    inRa("Canh tay don ngang = 0.30 x sin 50 = 0.30 x cos %.0f = %.4f m", fromHorizontal, leverM);
    inRa("tau = %.4f N.m  (bang ham chung: %.4f N.m)", torque, gravityTorque(fromHorizontal));
    inRa("SAI dung cos 50: %.4f N.m", ARM_MASS_KG * G * ARM_CG_M * Math.cos(Math.toRadians(fromVertical)));
}
```

Kết quả chạy:

```
Canh tay don ngang = 0.30 x sin 50 = 0.30 x cos 40 = 0.2298 m
tau = 1.8036 N.m  (bang ham chung: 1.8036 N.m)
SAI dung cos 50: 1.5134 N.m
```

> **Bẫy.** cos hay sin tùy góc được đo **từ đâu**. Thấy "50°" là bấm cos 50° ⇒ 1,5134 N·m, thiếu 16 %. Quy ước của bộ
> này: θ đo từ phương ngang thì dùng cos θ.

**Đáp số:** **1,804 N·m**.

</details>

<a id="bai-3-3"></a>

### Bài 3.3 ★ — Gắn tay thẳng vào motor 117 RPM

*Thay đổi so với bài 3.1: tay gắn thẳng vào trục ra của motor 117 RPM và đang nằm ngang; pin 12 V.*

**Đề.** a) Cần `setPower` bao nhiêu để giữ tay đứng yên? b) Dòng điện lúc giữ?

<details>
<summary><b>Lời giải 3.3</b></summary>

1. τ_kẹt = 68,4 × 0,0980665 = 6,7077 N·m.
2. Khi đứng yên (ω = 0), motor không có sức phản điện động, nên mô-men tỉ lệ thẳng với điện áp đặt vào:
   u = τ / τ_kẹt = 2,3544 / 6,7077 = **0,351** (giống hệ số kG của lift trong Phần 6).
3. Dòng điện: I = 0,25 + (9,2 − 0,25) × 0,351 = **3,39 A**.

```java
/** Bài 3.3 — Gắn tay thẳng vào motor 117 RPM: công suất giữ (kG) và dòng giữ ở phương ngang. */
static void bai3_3() {
    tieuDe("3.3");
    double torque = gravityTorque(0);                     // 2,354 N·m, motor gánh toàn bộ
    double kG = loadFraction(torque);                     // đứng yên (ω = 0): u = τ / τ_kẹt
    inRa("tau_ket = 68.4 kg.cm = %.4f N.m", STALL_NM);
    inRa("kG = %.4f / %.4f = %.4f -> setPower(%.3f) de giu", torque, STALL_NM, kG, kG);
    inRa("I = %.2f + (%.1f - %.2f) x %.4f = %.3f A", FREE_A, STALL_A, FREE_A, kG, current(torque));
}
```

Kết quả chạy:

```
tau_ket = 68.4 kg.cm = 6.7077 N.m
kG = 2.3544 / 6.7077 = 0.3510 -> setPower(0.351) de giu
I = 0.25 + (9.2 - 0.25) x 0.3510 = 3.391 A
```

> **Bẫy.** "Đứng yên thì không tốn điện" là sai: dòng điện đi theo **mô-men**, không theo tốc độ. Giữ một tay nặng ở
> phương ngang ngốn 3,4 A suốt thời gian giữ, dù motor không quay.

**Đáp số:** a) **u ≈ 0,351**; b) **I ≈ 3,39 A**.

</details>

<a id="bai-3-4"></a>

### Bài 3.4 ★★ — Thêm xích giảm tốc 3:1

*Thay đổi so với bài 3.3: thêm xích — bánh **15 răng** trên trục motor, **45 răng** trên trục tay.*

**Đề.** Tính lại công suất giữ (kG) và dòng giữ ở phương ngang. Tốc độ không tải của tay là bao nhiêu?

<details>
<summary><b>Lời giải 3.4</b></summary>

1. G = 45 / 15 = 3 ⇒ mô-men ở trục motor = 2,3544 / 3 = 0,7848 N·m.
2. kG = 0,7848 / 6,7077 = **0,1170**.
3. I = 0,25 + 8,95 × 0,1170 = **1,297 A**.
4. Tốc độ không tải của tay: 117 / 3 = **39 RPM**.

```java
/** Bài 3.4 — Thêm xích 15 → 45 răng (giảm tốc 3:1). */
static void bai3_4() {
    tieuDe("3.4");
    double torqueAtMotor = gravityTorque(0) / CHAIN;      // giảm tốc 3 lần => motor chỉ gánh 1/3 mô-men
    double kG = loadFraction(torqueAtMotor);
    inRa("tau o truc motor = %.4f / %.0f = %.4f N.m", gravityTorque(0), CHAIN, torqueAtMotor);
    inRa("kG = %.4f, I = %.3f A", kG, current(torqueAtMotor));
    inRa("Toc do khong tai cua TAY = %.0f / %.0f = %.0f RPM", FREE_RPM, CHAIN, FREE_RPM / CHAIN);
    inRa("SAI dao ti so (nhan 3): tau motor = %.3f N.m = %.0f %% tau_ket", gravityTorque(0) * CHAIN,
            100 * loadFraction(gravityTorque(0) * CHAIN));
}
```

Kết quả chạy:

```
tau o truc motor = 2.3544 / 3 = 0.7848 N.m
kG = 0.1170, I = 1.297 A
Toc do khong tai cua TAY = 117 / 3 = 39 RPM
SAI dao ti so (nhan 3): tau motor = 7.063 N.m = 105 % tau_ket
```

> **Bẫy.** Đảo ngược tỉ số (nhân 3 thay vì chia 3) ⇒ 7,063 N·m = 105 % τ_kẹt: motor không giữ nổi, tay rơi. Giảm tốc
> chia mô-men nhưng cũng **chia tốc độ** — không có gì miễn phí.

**Đáp số:** **kG = 0,1170**, **I = 1,30 A**, tay không tải **39 RPM**.

</details>

<a id="bai-3-5"></a>

### Bài 3.5 ★★ — Giữ tay 20 giây: nóng cỡ nào?

*Thay đổi so với bài 3.4: so sánh nhiệt tỏa ra trong cuộn dây motor khi giữ tay nằm ngang **20 s**, với hai phương án:
gắn thẳng (bài 3.3) và qua xích (bài 3.4). Dùng dòng điện đã tính ở hai bài đó.*

**Đề.** Mỗi phương án tỏa bao nhiêu watt, bao nhiêu joule? Dòng giảm bao nhiêu lần, nhiệt giảm bao nhiêu lần?

<details>
<summary><b>Lời giải 3.5</b></summary>

1. Điện trở cuộn dây ước lượng từ dòng kẹt: R = 12 / 9,2 = 1,3043 Ω.
2. Khi giữ yên, công suất cơ τ × ω = 0: **toàn bộ** điện năng thành nhiệt, P = I² × R.
3. Gắn thẳng: 3,391² × 1,3043 = 15,00 W ⇒ 20 s: **300 J**.
4. Qua xích: 1,297² × 1,3043 = 2,19 W ⇒ 20 s: **44 J**.
5. Dòng giảm 2,61 lần nhưng nhiệt giảm **6,84 lần** (= 2,61²).

```java
/** Bài 3.5 — Giữ tay ngang 20 s: nhiệt toả ra trong cuộn dây (I²R) khi gắn thẳng và khi có xích. */
static void bai3_5() {
    tieuDe("3.5");
    double iDirect = current(gravityTorque(0));
    double iChain = current(gravityTorque(0) / CHAIN);
    double pDirect = iDirect * iDirect * WINDING_OHM;
    double pChain = iChain * iChain * WINDING_OHM;
    inRa("R = 12 / 9.2 = %.4f ohm", WINDING_OHM);
    inRa("Gan thang: I = %.3f A -> P = %.2f W -> 20 s: %.0f J", iDirect, pDirect, 20 * pDirect);
    inRa("Co xich:   I = %.3f A -> P = %.2f W -> 20 s: %.0f J", iChain, pChain, 20 * pChain);
    inRa("Dong giam %.2f lan nhung nhiet giam %.2f lan (= %.2f^2)", iDirect / iChain, pDirect / pChain, iDirect / iChain);
}
```

Kết quả chạy:

```
R = 12 / 9.2 = 1.3043 ohm
Gan thang: I = 3.391 A -> P = 15.00 W -> 20 s: 300 J
Co xich:   I = 1.297 A -> P = 2.19 W -> 20 s: 44 J
Dong giam 2.61 lan nhung nhiet giam 6.84 lan (= 2.61^2)
```

> **Bẫy.** Nhiệt tỉ lệ với **bình phương** dòng điện, không tỉ lệ thuận. Giảm dòng một nửa thì nhiệt chỉ còn một phần
> tư — lý do các đội cho tay và lift một tỉ số giảm tốc đủ lớn.

**Đáp số:** gắn thẳng **15,0 W / 300 J**; qua xích **2,19 W / 44 J**; nhiệt giảm **6,84 lần**.

</details>

<a id="bai-3-6"></a>

### Bài 3.6 ★★ — Bù trọng lực theo góc, và lỗi radian

*Thay đổi so với bài 3.4: tay đi qua nhiều góc; lệnh bù trọng lực là u(θ) = kG × cos θ với kG = 0,1170.*

**Đề.** Tính u ở θ = −30°, 0°, 45°, 90° và 120°. Bạn D viết `KG * Math.cos(theta)` với `theta` tính bằng độ — D nhận
được các số nào?

<details>
<summary><b>Lời giải 3.6</b></summary>

1. Trọng lực mạnh nhất ở phương ngang (cos 0° = 1), giảm dần khi tay giơ lên, bằng 0 ở phương thẳng đứng (90°).
2. Qua khỏi 90°, cos θ **âm**: tay đã ngả ra sau, trọng lực kéo nó về phía sau, nên lệnh bù phải **đổi dấu**.
3. Kết quả: +0,1013 (−30°), +0,1170 (0°), +0,0827 (45°), 0 (90°), −0,0585 (120°).
4. Code của D: `Math.cos` của Java nhận **radian**, nên `Math.cos(45)` là cos của 45 rad. D nhận được những số vô
   nghĩa, có chỗ sai cả dấu: −0,0524 ở 90° và +0,0953 ở 120°.

```java
/** Bài 3.6 — Bù trọng lực u(θ) = kG·cos θ, và lỗi quên đổi độ sang radian. */
static void bai3_6() {
    tieuDe("3.6");
    double kG = loadFraction(gravityTorque(0) / CHAIN);   // 0,1170 (bài 3.4)
    for (double theta : new double[]{-30, 0, 45, 90, 120}) {
        double right = kG * Math.cos(Math.toRadians(theta));
        double wrong = kG * Math.cos(theta);              // LỖI: Math.cos nhận RADIAN
        inRa("theta = %4.0f do: u = %+.4f   (Math.cos(theta) sai: %+.4f)", theta, right, wrong);
    }
}
```

Kết quả chạy:

```
theta =  -30 do: u = +0.1013   (Math.cos(theta) sai: +0.0180)
theta =    0 do: u = +0.1170   (Math.cos(theta) sai: +0.1170)
theta =   45 do: u = +0.0827   (Math.cos(theta) sai: +0.0615)
theta =   90 do: u = +0.0000   (Math.cos(theta) sai: -0.0524)
theta =  120 do: u = -0.0585   (Math.cos(theta) sai: +0.0953)
```

> **Bẫy.** `Math.sin`, `Math.cos`, `Math.tan` luôn nhận **radian**: viết `Math.cos(Math.toRadians(theta))`. Lỗi này
> không làm chương trình báo gì — chỉ có tay quay hành xử kỳ lạ ở vài góc.

**Đáp số:** u = **+0,1013 / +0,1170 / +0,0827 / 0 / −0,0585**.

</details>

<a id="bai-3-7"></a>

### Bài 3.7 ★★ — Pin đầy, pin yếu

*Thay đổi so với bài 3.6: kG = 0,1170 được đo khi pin 12,0 V. Đầu trận pin **13,4 V**, cuối trận còn **11,6 V**.*

**Đề.** Lệnh u để giữ tay nằm ngang ở ba mức pin 13,4 V, 12,0 V, 11,6 V? Nếu nhân nhầm V/12 thay vì 12/V thì sao?

<details>
<summary><b>Lời giải 3.7</b></summary>

1. Mô-men tỉ lệ với **u × V**. Muốn mô-men không đổi thì u × V = kG × 12 ⇒ u = kG × 12 / V.
2. 13,4 V: 0,1170 × 12 / 13,4 = **0,1048**; 12,0 V: **0,1170**; 11,6 V: **0,1210**.
3. Nhân nhầm V/12: 0,1306 ở 13,4 V (đẩy dư, tay trôi lên) và 0,1131 ở 11,6 V (thiếu, tay sụt xuống).

```java
/** Bài 3.7 — Pin 13,4 V và 11,6 V: bù điện áp cho lệnh giữ tay. */
static void bai3_7() {
    tieuDe("3.7");
    double kG = loadFraction(gravityTorque(0) / CHAIN);
    for (double volts : new double[]{13.4, 12.0, 11.6}) {
        inRa("V = %.1f: u = %.4f x 12 / %.1f = %.4f   (SAI nhan V/12: %.4f)", volts, kG, volts, kG * 12 / volts,
                kG * volts / 12);
    }
}
```

Kết quả chạy:

```
V = 13.4: u = 0.1170 x 12 / 13.4 = 0.1048   (SAI nhan V/12: 0.1306)
V = 12.0: u = 0.1170 x 12 / 12.0 = 0.1170   (SAI nhan V/12: 0.1170)
V = 11.6: u = 0.1170 x 12 / 11.6 = 0.1210   (SAI nhan V/12: 0.1131)
```

> **Bẫy.** Pin **mạnh** hơn thì cần lệnh **nhỏ** hơn. Tự kiểm tra bằng suy luận trước khi tin con số: 13,4 V khỏe hơn
> 12 V nên u phải giảm.

**Đáp số:** **0,1048** (13,4 V) · **0,1170** (12,0 V) · **0,1210** (11,6 V).

</details>

<a id="bai-3-8"></a>

### Bài 3.8 ★★ — Nâng toàn lực: tay quay nhanh cỡ nào?

*Thay đổi so với bài 3.4: tay qua xích 3:1, u = 1, pin 12 V. Tay đi từ −30° lên 90°.*

**Đề.** a) Tốc độ tay (độ/s) khi đi qua phương ngang và khi tới phương thẳng đứng. b) Bỏ qua thời gian tăng tốc, ước
lượng thời gian đi hết 120°: cho một chặn dưới và một chặn trên.

<details>
<summary><b>Lời giải 3.8</b></summary>

1. Ở phương ngang, tải lớn nhất (τ/τ_kẹt = 0,1170): ω_motor = 117 × (1 − 0,1170) = 103,31 RPM ⇒ tay 34,44 RPM
   = **206,6 °/s**.
2. Ở phương thẳng đứng, trọng lực không cản: tay chạy bằng tốc độ không tải 39 RPM = **234,0 °/s**.
3. Trong đoạn −30° … 90°, tải lớn nhất ở 0°, nhỏ nhất ở 90° ⇒ tốc độ luôn nằm giữa 206,6 và 234,0 °/s.
4. Thời gian đi 120° nằm giữa 120 / 234,0 = **0,513 s** và 120 / 206,6 = **0,581 s**.

```java
/** Bài 3.8 — u = 1 ở 12 V: tay quay nhanh cỡ nào, và mất bao lâu để đi từ −30° lên 90°? */
static void bai3_8() {
    tieuDe("3.8");
    double loadNm = gravityTorque(0) / CHAIN;             // tải lớn nhất: tay nằm ngang
    double rpmMotor = motorRpm(1.0, 12.0, loadNm);
    double degPerSecSlow = rpmMotor / CHAIN * 360 / 60;   // chậm nhất: ở phương ngang
    double degPerSecFast = FREE_RPM / CHAIN * 360 / 60;   // nhanh nhất: ở phương đứng (không tải trọng lực)
    inRa("O phuong ngang: motor %.2f RPM -> tay %.2f RPM = %.1f do/s", rpmMotor, rpmMotor / CHAIN, degPerSecSlow);
    inRa("O phuong dung: tay %.0f RPM = %.1f do/s", FREE_RPM / CHAIN, degPerSecFast);
    inRa("120 do mat tu %.3f s (luon nhanh nhat) toi %.3f s (luon cham nhat)", 120 / degPerSecFast, 120 / degPerSecSlow);
}
```

Kết quả chạy:

```
O phuong ngang: motor 103.31 RPM -> tay 34.44 RPM = 206.6 do/s
O phuong dung: tay 39 RPM = 234.0 do/s
120 do mat tu 0.513 s (luon nhanh nhat) toi 0.581 s (luon cham nhat)
```

> **Bẫy.** Dùng tốc độ không tải 39 RPM cho mọi góc ⇒ đánh giá quá lạc quan. Thực tế còn mất thêm thời gian tăng tốc ở
> đầu và hãm ở cuối, nên trong kế hoạch thời gian của Autonomous hãy dùng chặn trên và cộng thêm phần dư.

**Đáp số:** a) **206,6 °/s** (ngang), **234,0 °/s** (đứng); b) từ **0,513 s** đến **0,581 s**.

</details>

<a id="bai-3-9"></a>

### Bài 3.9 ★★★ — Hiệu suất: gắn thẳng hay qua xích?

*Thay đổi so với bài 3.8: so sánh hai phương án — gắn thẳng (1:1) và qua xích (3:1) — tại phương ngang, u = 1, 12 V.*

**Đề.** Với mỗi phương án, tính: tốc độ tay, công suất cơ, dòng điện, công suất điện và hiệu suất.

<details>
<summary><b>Lời giải 3.9</b></summary>

1. Gắn thẳng: τ/τ_kẹt = 0,351 ⇒ motor (cũng là tay) 117 × 0,649 = 75,93 RPM = 7,951 rad/s. P_cơ = 2,3544 × 7,951
   = 18,72 W. I = 3,391 A ⇒ P_điện = 12 × 3,391 = 40,70 W ⇒ hiệu suất **46,0 %**.
2. Qua xích: tay 34,44 RPM = 3,606 rad/s ⇒ P_cơ = 2,3544 × 3,606 = 8,49 W. I = 1,297 A ⇒ P_điện = 15,57 W ⇒ hiệu suất
   **54,5 %**.
3. Gắn thẳng nhanh gấp 2,2 lần nhưng ngốn 2,6 lần điện, vì motor làm việc gần vùng kẹt hơn (35 % τ_kẹt so với 12 %).
   Tay cần nhanh và chỉ nâng thỉnh thoảng: có thể chọn gắn thẳng; tay phải giữ lâu: chọn xích.

```java
/** Bài 3.9 — Hiệu suất ở phương ngang, u = 1, 12 V: gắn thẳng hay qua xích? */
static void bai3_9() {
    tieuDe("3.9");
    double armTorque = gravityTorque(0);
    for (double ratio : new double[]{1.0, CHAIN}) {
        double loadNm = armTorque / ratio;
        double armRadS = motorRpm(1.0, 12.0, loadNm) / ratio * 2 * Math.PI / 60;
        double pOut = armTorque * armRadS;                // công suất cơ = τ × ω (ở cùng một trục)
        double pIn = 12.0 * current(loadNm);              // công suất điện = V × I
        inRa("Ti so %.0f:1 -> tay %.2f RPM, P_co = %.2f W, I = %.3f A, P_dien = %.2f W, hieu suat = %.1f %%",
                ratio, armRadS * 60 / (2 * Math.PI), pOut, current(loadNm), pIn, 100 * pOut / pIn);
    }
}
```

Kết quả chạy:

```
Ti so 1:1 -> tay 75.93 RPM, P_co = 18.72 W, I = 3.391 A, P_dien = 40.70 W, hieu suat = 46.0 %
Ti so 3:1 -> tay 34.44 RPM, P_co = 8.49 W, I = 1.297 A, P_dien = 15.57 W, hieu suat = 54.5 %
```

> **Bẫy.** Công suất cơ phải tính ở **cùng một trục**: τ_tay × ω_tay (hoặc τ_motor × ω_motor — ra cùng một số). Nhân
> τ_tay với ω_motor sẽ ra công suất lớn gấp 3 lần, và "hiệu suất" vượt 100 %.

**Đáp số:** gắn thẳng **75,9 RPM, 18,7 W cơ / 40,7 W điện, 46 %**; qua xích **34,4 RPM, 8,5 W / 15,6 W, 54,5 %**.

</details>

<a id="bai-3-10"></a>

### Bài 3.10 ★★★ — Xuất phát toàn lực: pin sụt

*Thay đổi so với bài 3.8: khoảnh khắc đầu TeleOp — người lái đẩy hết cần, 4 motor bánh (312 RPM) bắt đầu từ đứng yên
nên gần như kẹt, mỗi motor ăn I = 9,2 × V/12 — đúng lúc tay đang nâng (u = 1, ở phương ngang, qua xích). Pin: điện
áp hở **13,0 V**, nội trở pin cộng dây **0,08 Ω**.*

**Đề.** a) Điện áp còn lại ở Hub? b) Tổng dòng điện — so với cầu chì 20 A? c) Tay chậm đi bao nhiêu phần trăm?

<details>
<summary><b>Lời giải 3.10</b></summary>

1. Dòng của 4 motor bánh đang đứng yên tỉ lệ với điện áp: a × V với a = 4 × 9,2 / 12 = 3,0667 A/V. Dòng của tay đi
   theo mô-men tải nên coi như không đổi: b = 1,297 A (bài 3.4).
2. Pin: V = 13,0 − 0,08 × (a × V + b). Chuyển vế:
   V = (13,0 − 0,08 × 1,297) / (1 + 0,08 × 3,0667) = **10,356 V**.
3. b) I = 3,0667 × 10,356 + 1,297 = **33,1 A** — vượt 20 A. Chỉ trong khoảnh khắc nên cầu chì chưa đứt ngay, nhưng
   lặp lại nhiều lần là rủi ro (Phần 6, bài 6.7).
4. c) Tay: 117 × (10,356 / 12 − 0,1170) / 3 = 29,09 RPM, so với 34,44 RPM ở 12 V ⇒ chậm đi **15,5 %**. Bù điện áp
   không cứu được vì u đã bằng 1.

```java
/** Bài 3.10 — Xuất phát toàn lực: 4 motor bánh gần như kẹt + tay đang nâng. Pin sụt bao nhiêu, tay chậm đi bao nhiêu? */
static void bai3_10() {
    tieuDe("3.10");
    double vOpen = 13.0, rBattery = 0.08;                 // pin + dây
    double a = 4 * 9.2 / 12.0;                            // 4 motor bánh đứng yên: I = 4 × 9,2 × V/12 = a·V
    double b = current(gravityTorque(0) / CHAIN);         // tay: dòng theo mô-men, ≈ không đổi
    double v = (vOpen - rBattery * b) / (1 + rBattery * a);   // giải V = Vopen − R·(a·V + b)
    double iTotal = a * v + b;
    double armRpm12 = motorRpm(1.0, 12.0, gravityTorque(0) / CHAIN) / CHAIN;
    double armRpmSag = motorRpm(1.0, v, gravityTorque(0) / CHAIN) / CHAIN;
    inRa("V = (%.1f - %.2f x %.3f) / (1 + %.2f x %.4f) = %.3f V", vOpen, rBattery, b, rBattery, a, v);
    inRa("I tong = %.4f x %.3f + %.3f = %.1f A (cau chi 20 A)", a, v, b, iTotal);
    inRa("SAI coi pin luon 12 V: I = %.1f + %.1f = %.1f A va tay khong cham di", 4 * 9.2, b, 4 * 9.2 + b);
    inRa("Tay o phuong ngang, u = 1: %.2f RPM (12 V) -> %.2f RPM (%.1f %%)", armRpm12, armRpmSag,
            100 * (armRpmSag / armRpm12 - 1));
}
```

Kết quả chạy:

```
V = (13.0 - 0.08 x 1.297) / (1 + 0.08 x 3.0667) = 10.356 V
I tong = 3.0667 x 10.356 + 1.297 = 33.1 A (cau chi 20 A)
SAI coi pin luon 12 V: I = 36.8 + 1.3 = 38.1 A va tay khong cham di
Tay o phuong ngang, u = 1: 34.44 RPM (12 V) -> 29.09 RPM (-15.5 %)
```

> **Bẫy.** Dòng phụ thuộc điện áp, mà điện áp lại phụ thuộc dòng: phải **giải phương trình** (hoặc tính lặp vài lần).
> Coi pin lúc nào cũng 12 V ⇒ 36,8 + 1,3 = 38,1 A và tay không chậm đi — sai cả hai. Bài học cho người lái: tránh đẩy
> hết cần lúc cơ cấu đang nâng tải nặng.

**Đáp số:** a) **10,36 V**; b) **33,1 A** (> 20 A, trong khoảnh khắc); c) tay chậm **15,5 %** (34,44 → 29,09 RPM).

</details>

---

<a id="bo-4"></a>

## Bộ 4 — Cảm biến: khoảng cách, IMU và màu

**Tình huống chung.** Để đặt NECTAR vào FLOWER, robot mẫu phải căn chính xác vào tường sân bằng **REV 2m Distance
Sensor** (chip VL53L0X: đo tới 2 m, góc nhìn toàn phần 25°, mỗi lần đo mặc định khoảng 33 ms) và IMU. Quy ước: x hướng
ra trước robot, y sang trái, góc dương là quay **trái** (ngược chiều kim đồng hồ) — giống IMU của SDK. Cảm biến "trước"
đặt trên trục giữa robot, trước tâm **7,0 in**, nhìn thẳng ra trước. Ba bài cuối chuyển sang cảm biến màu ở miệng
intake, với ngưỡng như ví dụ 8: POLLEN 25°–75°, đỏ ≤ 20° hoặc ≥ 340°, xanh 200°–260°.

**Công thức cần nhớ**

```
tâm → tường (vuông góc) = (số đọc + x_cảm biến) × cos θ − y_cảm biến × sin θ      (θ: góc lệch của robot)
hai cảm biến cách nhau s:  tan θ = (đọc_trái − đọc_phải) / s                     (cùng một đơn vị!)
sai số hướng            = normalizeDegrees(hướng đích − hướng hiện tại),  thuộc [−180°, 180°)
dữ liệu cũ t giây       : robot đã đi thêm v × t ;   phanh giảm tốc đều trong t_p: đi thêm v × t_p / 2
vết đo của cảm biến     : đường kính = 2 × d × tan(góc nhìn / 2)
sắc độ H (Δ = max − min): max = R: 60 × (G − B)/Δ  (âm thì cộng 360)
                          max = G: 60 × (2 + (B − R)/Δ) ;  max = B: 60 × (4 + (R − G)/Δ)
độ bão hòa              : S = Δ / max
```

**Code dùng chung** (đầu file `Bo4_CamBien.java`):

```java
// ===== Hằng số =====
static final double MM_PER_IN = 25.4;
static final double SENSOR_FWD_IN = 7.0;        // cảm biến khoảng cách đặt trước tâm robot 7 in
static final double FOV_DEG = 25;               // góc nhìn TOÀN PHẦN của VL53L0X (REV 2m Distance Sensor)

// ===== Hàm dùng chung =====

/**
 * Khoảng cách vuông góc từ TÂM robot tới tường.
 * readingMm: số đọc của cảm biến; fwdIn, leftIn: vị trí cảm biến so với tâm (trước, trái);
 * thetaDeg: góc lệch của robot so với hướng nhìn thẳng vào tường (dương = robot quay trái).
 */
static double centerToWall(double readingMm, double fwdIn, double leftIn, double thetaDeg) {
    double d = readingMm / MM_PER_IN;
    double t = Math.toRadians(thetaDeg);
    return (fwdIn + d) * Math.cos(t) - leftIn * Math.sin(t);
}

/** Đưa góc về khoảng [-180, 180) — y hệt AngleUnit.normalizeDegrees() của SDK FTC. */
static double normalizeDegrees(double deg) {
    while (deg >= 180.0) deg -= 360.0;
    while (deg < -180.0) deg += 360.0;
    return deg;
}

/** Sắc độ (hue, độ, 0..360) từ R, G, B ở thang bất kỳ (0..255, số đếm thô...). */
static double hue(double r, double g, double b) {
    double max = Math.max(r, Math.max(g, b));
    double min = Math.min(r, Math.min(g, b));
    double delta = max - min;
    if (delta == 0) return 0;                       // xám: không có sắc độ
    double h;
    if (max == r) h = 60 * ((g - b) / delta);       // vùng đỏ: có thể ra số ÂM
    else if (max == g) h = 60 * (2 + (b - r) / delta);
    else h = 60 * (4 + (r - g) / delta);
    if (h < 0) h += 360;                            // sắc độ âm => cộng 360°
    return h;
}

/** Độ bão hòa 0..1. */
static double saturation(double r, double g, double b) {
    double max = Math.max(r, Math.max(g, b));
    double min = Math.min(r, Math.min(g, b));
    return max == 0 ? 0 : (max - min) / max;
}
```

---

<a id="bai-4-1"></a>

### Bài 4.1 ★ — Một cảm biến, robot nhìn thẳng vào tường

**Đề.** Robot nhìn thẳng vào tường; `getDistance(DistanceUnit.MM)` trả về **300**. Tâm robot cách tường bao nhiêu inch?

<details>
<summary><b>Lời giải 4.1</b></summary>

1. Đổi đơn vị: 300 / 25,4 = 11,811 in (hoặc gọi thẳng `getDistance(DistanceUnit.INCH)`).
2. Cảm biến nằm **trước** tâm 7,0 in, tức gần tường hơn tâm ⇒ cộng thêm: 11,811 + 7,0 = **18,811 in**.

```java
/** Bài 4.1 — Một cảm biến, robot nhìn thẳng vào tường. */
static void bai4_1() {
    tieuDe("4.1");
    double readingIn = 300 / MM_PER_IN;               // getDistance(DistanceUnit.MM) = 300
    double center = readingIn + SENSOR_FWD_IN;         // cảm biến ở TRƯỚC tâm => cộng thêm 7 in
    inRa("300 mm = %.3f in; tam -> tuong = %.3f + %.1f = %.3f in", readingIn, readingIn, SENSOR_FWD_IN, center);
    inRa("SAI tru di: %.3f in | SAI quen doi mm: %.1f \"in\"", readingIn - SENSOR_FWD_IN, 300 + SENSOR_FWD_IN);
    inRa("Bang ham chung: %.3f in", centerToWall(300, SENSOR_FWD_IN, 0, 0));
}
```

Kết quả chạy:

```
300 mm = 11.811 in; tam -> tuong = 11.811 + 7.0 = 18.811 in
SAI tru di: 4.811 in | SAI quen doi mm: 307.0 "in"
Bang ham chung: 18.811 in
```

> **Bẫy.** Trừ thay vì cộng (4,811 in) — vẽ hình là thấy ngay. Quên đổi mm ⇒ "307 in", dài hơn cả sân (144 in): một con
> số vô lý phải bị nghi ngay.

**Đáp số:** **18,81 in**.

</details>

<a id="bai-4-2"></a>

### Bài 4.2 ★ — Robot lệch 12°

*Thay đổi so với bài 4.1: robot quay lệch **12°** so với hướng vuông góc với tường; số đọc vẫn là 300 mm.*

**Đề.** Khoảng cách **vuông góc** từ tâm robot tới tường là bao nhiêu?

<details>
<summary><b>Lời giải 4.2</b></summary>

1. Tia đo đi theo hướng robot đang nhìn, nên nó nghiêng 12° so với đường vuông góc với tường.
2. Đoạn từ tâm tới cảm biến (7 in) cũng nằm trên hướng đó, nên cũng nghiêng 12°.
3. Chiếu cả hai đoạn lên đường vuông góc: (11,811 + 7) × cos 12° = 18,811 × 0,97815 = **18,400 in**.

```java
/** Bài 4.2 — Robot lệch 12°: số đọc dài hơn khoảng cách vuông góc. */
static void bai4_2() {
    tieuDe("4.2");
    double readingIn = 300 / MM_PER_IN;
    double cos12 = Math.cos(Math.toRadians(12));
    double right = (readingIn + SENSOR_FWD_IN) * cos12;    // cả đoạn 7 in cũng nghiêng 12°
    inRa("cos 12 = %.5f; tam -> tuong = (%.3f + 7) x %.5f = %.3f in", cos12, readingIn, cos12, right);
    inRa("SAI chi chieu so doc: %.3f x cos12 + 7 = %.3f in", readingIn, readingIn * cos12 + SENSOR_FWD_IN);
    inRa("SAI Math.cos(12) (12 radian): %.3f in", (readingIn + SENSOR_FWD_IN) * Math.cos(12));
}
```

Kết quả chạy:

```
cos 12 = 0.97815; tam -> tuong = (11.811 + 7) x 0.97815 = 18.400 in
SAI chi chieu so doc: 11.811 x cos12 + 7 = 18.553 in
SAI Math.cos(12) (12 radian): 15.874 in
```

> **Bẫy.** Chỉ chiếu số đọc mà quên đoạn 7 in ⇒ 18,553 in. Gõ `Math.cos(12)` (12 radian) ⇒ 15,874 in. Thêm một lưu ý về
> phần cứng: tia càng xiên thì tín hiệu phản xạ về càng yếu, nên cảm biến chỉ nên dùng khi góc lệch nhỏ.

**Đáp số:** **18,40 in**.

</details>

<a id="bai-4-3"></a>

### Bài 4.3 ★★ — Cảm biến lệch sang trái

*Thay đổi so với bài 4.2: cảm biến được dời sang **trái** 5,0 in (vẫn trước tâm 7,0 in), số đọc vẫn 300 mm. Xét hai
trường hợp: robot quay **trái** 12° (θ = +12°) và quay **phải** 12° (θ = −12°).*

**Đề.** Tính khoảng cách vuông góc từ tâm tới tường cho mỗi trường hợp.

<details>
<summary><b>Lời giải 4.3</b></summary>

1. Phần "trước" và số đọc chiếu như bài 4.2: 18,400 in.
2. Đoạn lệch ngang 5 in khi robot quay góc θ có hình chiếu lên đường vuông góc là 5 × sin θ, và được **trừ** đi theo
   công thức chung: (d + 7) × cos θ − 5 × sin θ.
3. θ = +12°: 18,400 − 1,040 = **17,360 in**. θ = −12°: 18,400 + 1,040 = **19,440 in**.

Kiểm tra bằng hình: khi robot quay trái, cảm biến ở góc trước–trái bị kéo lùi về phía sau so với tâm — theo phương
vuông góc với tường, nó chỉ còn nằm trước tâm 5,807 in thay vì 7 × cos 12° = 6,847 in. Với cùng một số đọc, tâm vì thế
**gần** tường hơn — đúng là 17,36 < 18,40. Quay phải thì ngược lại: cảm biến nằm trước tâm 7,887 in.

```java
/** Bài 4.3 — Cảm biến lệch sang trái 5 in: dấu của phần lệch ngang phụ thuộc chiều quay. */
static void bai4_3() {
    tieuDe("4.3");
    for (double theta : new double[]{12, -12}) {
        double t = Math.toRadians(theta);
        double c = (SENSOR_FWD_IN + 300 / MM_PER_IN) * Math.cos(t);
        double s = 5.0 * Math.sin(t);
        double sensorAhead = SENSOR_FWD_IN * Math.cos(t) - s;    // cảm biến nằm trước tâm bao xa (vuông góc tường)
        inRa("theta = %+3.0f do: %.3f - (%+.3f) = %.3f in; cam bien truoc tam %.3f in", theta, c, s,
                centerToWall(300, SENSOR_FWD_IN, 5.0, theta), sensorAhead);
    }
}
```

Kết quả chạy:

```
theta = +12 do: 18.400 - (+1.040) = 17.360 in; cam bien truoc tam 5.807 in
theta = -12 do: 18.400 - (-1.040) = 19.440 in; cam bien truoc tam 7.887 in
```

> **Bẫy.** Cùng số đọc 300 mm mà hai trường hợp chênh nhau **2,08 in** chỉ vì chiều quay. Đừng bỏ qua phần lệch ngang
> của cảm biến khi robot không vuông góc với tường.

**Đáp số:** **17,36 in** (quay trái) · **19,44 in** (quay phải).

</details>

<a id="bai-4-4"></a>

### Bài 4.4 ★★ — Hai cảm biến: robot lệch bao nhiêu?

*Thay đổi so với bài 4.3: robot có hai cảm biến phía trước: trái ở (7; +5) in, phải ở (7; −5) in — cách nhau 10 in. Số
đọc: trái **280 mm**, phải **330 mm**.*

**Đề.** a) Robot đang lệch bao nhiêu độ, sang bên nào? Cần quay bên nào để vuông góc với tường? b) Tâm cách tường bao
xa?

<details>
<summary><b>Lời giải 4.4</b></summary>

1. Đổi về cùng đơn vị: 10 in = 254 mm.
2. tan θ = (280 − 330) / 254 = −0,19685 ⇒ θ = **−11,14°**.
3. θ âm ⇒ robot đang lệch **phải** 11,14° ⇒ phải quay **trái** 11,14° để vuông góc. Kiểm tra bằng hình: cảm biến phải
   đọc xa hơn, tức phía phải của robot lùi xa tường — robot đã xoay mũi sang phải.
4. b) Dùng công thức bài 4.3 cho từng cảm biến với θ = −11,14°: cả hai đều cho **18,650 in**. Hai con số khớp nhau là
   một phép kiểm tra chéo tốt cho chiều của θ.

```java
/** Bài 4.4 — Hai cảm biến cách nhau 10 in: tính góc lệch so với tường. */
static void bai4_4() {
    tieuDe("4.4");
    double left = 280, right = 330;                    // mm
    double spacingMm = 10 * MM_PER_IN;                 // đổi khoảng cách 10 in sang mm cho cùng đơn vị
    double theta = Math.toDegrees(Math.atan((left - right) / spacingMm));
    inRa("tan(theta) = (%.0f - %.0f) / %.0f = %.5f -> theta = %.2f do", left, right, spacingMm,
            (left - right) / spacingMm, theta);
    inRa("=> Robot dang lech %s %.2f do; quay %s %.2f do de vuong goc", theta < 0 ? "PHAI" : "TRAI", Math.abs(theta),
            theta < 0 ? "TRAI" : "PHAI", Math.abs(theta));
    inRa("Tam -> tuong: tinh tu cam bien trai %.3f in, tu cam bien phai %.3f in",
            centerToWall(left, SENSOR_FWD_IN, 5, theta), centerToWall(right, SENSOR_FWD_IN, -5, theta));
    inRa("SAI tron don vi (50 mm / 10 in): theta = %.1f do | SAI dung asin: %.2f do",
            Math.toDegrees(Math.atan(50 / 10.0)), -Math.toDegrees(Math.asin(50 / spacingMm)));
}
```

Kết quả chạy:

```
tan(theta) = (280 - 330) / 254 = -0.19685 -> theta = -11.14 do
=> Robot dang lech PHAI 11.14 do; quay TRAI 11.14 do de vuong goc
Tam -> tuong: tinh tu cam bien trai 18.650 in, tu cam bien phai 18.650 in
SAI tron don vi (50 mm / 10 in): theta = 78.7 do | SAI dung asin: -11.35 do
```

> **Bẫy.** Trộn mm với in (50 / 10) ⇒ θ = 78,7° — vô lý. Dùng asin thay cho atan ⇒ −11,35°: hai cảm biến đo theo hướng
> robot nhìn, nên hiệu số đọc và khoảng cách giữa chúng là hai **cạnh góc vuông** ⇒ dùng tan. Và luôn suy luận lại
> chiều quay bằng hình vẽ.

**Đáp số:** a) lệch **phải 11,14°**, quay **trái 11,14°**; b) **18,65 in**.

</details>

<a id="bai-4-5"></a>

### Bài 4.5 ★★ — IMU: quay bên nào?

*Thay đổi so với bài 4.4: bỏ cảm biến khoảng cách, dùng IMU. Robot cần nhìn thẳng vào tường, tức hướng **180°** trong hệ
tọa độ sân; IMU đang đọc yaw **−171°**.*

**Đề.** Sai số hướng là bao nhiêu? Robot phải quay bên nào, bao nhiêu độ?

<details>
<summary><b>Lời giải 4.5</b></summary>

1. Sai số thô: 180 − (−171) = 351°. Đưa thẳng số này vào bộ điều khiển P thì robot quay **trái** gần hết một vòng.
2. Chuẩn hóa về [−180°, 180°): 351 − 360 = **−9°** ⇒ quay **phải** (theo chiều kim đồng hồ) **9°**.
3. Kiểm tra: −171° chính là 189°, đã vượt 180° 9° theo chiều trái ⇒ quay phải 9° là đúng.
4. Thêm một chi tiết: `AngleUnit.normalizeDegrees` trả về khoảng **[−180°, 180°)**, nên `normalizeDegrees(180)` = −180.
   Một lệnh "quay đúng 180°" sẽ quay sang **phải**. (Các tài liệu trước trong bộ này viết khoảng (−180°, 180°]; theo
   đúng mã nguồn của SDK là [−180°, 180°). Hai cách viết chỉ khác nhau ở đúng mốc ±180°.)

```java
/** Bài 4.5 — IMU: muốn nhìn hướng 180°, IMU đang đọc −171°. Quay bên nào, bao nhiêu? */
static void bai4_5() {
    tieuDe("4.5");
    double target = 180, yaw = -171;
    double raw = target - yaw;
    double err = normalizeDegrees(raw);
    inRa("Sai so tho = 180 - (-171) = %.0f do", raw);
    inRa("normalizeDegrees(%.0f) = %.0f do -> quay %s %.0f do", raw, err, err < 0 ? "PHAI (theo chieu kim dong ho)" : "TRAI",
            Math.abs(err));
    inRa("Them: normalizeDegrees(180) = %.0f, normalizeDegrees(-180) = %.0f", normalizeDegrees(180), normalizeDegrees(-180));
}
```

Kết quả chạy:

```
Sai so tho = 180 - (-171) = 351 do
normalizeDegrees(351) = -9 do -> quay PHAI (theo chieu kim dong ho) 9 do
Them: normalizeDegrees(180) = -180, normalizeDegrees(-180) = -180
```

> **Bẫy.** Phải chuẩn hóa **sai số** (hiệu hai góc), chứ không chỉ góc đọc từ IMU — cả 180° lẫn −171° đều đã nằm trong
> khoảng chuẩn, nhưng hiệu của chúng thì không.

**Đáp số:** sai số **−9°** ⇒ quay **phải 9°**.

</details>

<a id="bai-4-6"></a>

### Bài 4.6 ★★ — Lao vào tường: phải ra lệnh dừng sớm bao nhiêu?

*Thay đổi so với bài 4.1: robot nhìn thẳng, chạy vào tường với **24 in/s**, cần dừng khi tâm cách tường **12,0 in**.
Cảm biến cho số đo mới mỗi **33 ms**; vòng lặp **20 ms**; phanh từ 24 in/s về 0 mất **0,10 s** (giảm tốc đều).*

**Đề.** Code phải ra lệnh dừng khi số đọc của cảm biến (mm) nhỏ hơn bao nhiêu?

<details>
<summary><b>Lời giải 4.6</b></summary>

1. Số đo cũ nhất mà code có thể đang dùng: 33 + 20 = 53 ms. Trong khoảng đó robot đã đi thêm 24 × 0,053 = 1,272 in mà
   code chưa biết.
2. Quãng phanh (giảm tốc đều, vận tốc trung bình bằng một nửa): 24 × 0,10 / 2 = 1,20 in.
3. Tổng: 2,47 in ⇒ ra lệnh dừng khi tâm cách tường ≤ 12,0 + 2,47 = **14,47 in**, tức cảm biến đọc
   ≤ 14,47 − 7,0 = 7,47 in = **190 mm**.

```java
/** Bài 4.6 — Robot lao vào tường 24 in/s: dữ liệu cũ + quãng phanh => phải ra lệnh dừng sớm bao nhiêu? */
static void bai4_6() {
    tieuDe("4.6");
    double v = 24;                                     // in/s
    double ageS = (33 + 20) / 1000.0;                  // số đo cũ tối đa: 33 ms cảm biến + 20 ms vòng lặp
    double brakeS = 0.10;                              // thời gian phanh, giảm tốc đều
    double lateIn = v * ageS;
    double brakeIn = v * brakeS / 2;                   // giảm tốc đều: quãng đường = v × t / 2
    double trigger = 12.0 + lateIn + brakeIn;
    inRa("Du lieu cu: %.0f ms x %.0f in/s = %.3f in", ageS * 1000, v, lateIn);
    inRa("Phanh: %.0f x %.2f / 2 = %.2f in", v, brakeS, brakeIn);
    inRa("=> Ra lenh dung khi tam -> tuong <= %.2f in, tuc cam bien doc <= %.2f in = %.0f mm", trigger,
            trigger - SENSOR_FWD_IN, (trigger - SENSOR_FWD_IN) * MM_PER_IN);
    inRa("SAI quen doi ms (53 x 24): %.0f in", 53 * v);
}
```

Kết quả chạy:

```
Du lieu cu: 53 ms x 24 in/s = 1.272 in
Phanh: 24 x 0.10 / 2 = 1.20 in
=> Ra lenh dung khi tam -> tuong <= 14.47 in, tuc cam bien doc <= 7.47 in = 190 mm
SAI quen doi ms (53 x 24): 1272 in
```

> **Bẫy.** Quên đổi ms sang s (53 × 24 = "1272 in"); chỉ tính một trong hai độ trễ; quãng phanh là v × t / **2**, không
> phải v × t. Cách tốt hơn ngưỡng cứng: giảm tốc dần theo khoảng cách còn lại (bộ P như ví dụ 10).

**Đáp số:** dừng khi cảm biến đọc ≤ **190 mm** (tâm ≤ 14,47 in).

</details>

<a id="bai-4-7"></a>

### Bài 4.7 ★★ — Một số đo hỏng: trung bình hay trung vị?

*Thay đổi so với bài 4.6: robot đứng yên, 5 số đọc liên tiếp (mm) là 312, 309, **820**, 311, 310 — số 820 là một lần đo
hỏng (tia trượt qua mép tường).*

**Đề.** a) Nếu lấy trung bình, rồi nếu lấy trung vị, tâm robot cách tường bao xa? b) Trung vị của 5 số gần nhất trễ
khoảng bao lâu? Nếu robot chạy 24 in/s thì trong thời gian đó nó đi thêm bao xa?

<details>
<summary><b>Lời giải 4.7</b></summary>

1. a) Trung bình = 412,4 mm ⇒ tâm cách tường 412,4 / 25,4 + 7 = **23,24 in** — sai gần 4 in vì một số đo hỏng.
2. Trung vị: sắp xếp 309, 310, 311, 312, 820 và lấy số ở giữa: 311 mm ⇒ **19,24 in**. Số hỏng không ảnh hưởng.
3. b) Một thay đổi thật chỉ hiện ra ở trung vị khi nó chiếm quá nửa cửa sổ 5 số, nên trung vị trễ khoảng 2 lần đo =
   **66 ms** ⇒ ở 24 in/s robot đi thêm **1,58 in**. Phải cộng phần này vào ngưỡng dừng của bài 4.6.

```java
/** Bài 4.7 — Một số đo hỏng: trung bình hay trung vị? */
static void bai4_7() {
    tieuDe("4.7");
    double[] mm = {312, 309, 820, 311, 310};
    double mean = Arrays.stream(mm).average().orElse(0);
    double[] sorted = mm.clone();
    Arrays.sort(sorted);
    double median = sorted[sorted.length / 2];
    inRa("Trung binh = %.1f mm -> tam cach tuong %.2f in", mean, mean / MM_PER_IN + SENSOR_FWD_IN);
    inRa("Sap xep: %s -> trung vi = %.0f mm -> %.2f in", Arrays.toString(sorted), median, median / MM_PER_IN + SENSOR_FWD_IN);
    double lagS = 2 * 0.033;                            // trung vị của 5 số đo gần nhất trễ ≈ 2 lần đo
    inRa("Do tre them cua trung vi 5 so: %.0f ms -> o 24 in/s robot di them %.2f in", lagS * 1000, 24 * lagS);
}
```

Kết quả chạy:

```
Trung binh = 412.4 mm -> tam cach tuong 23.24 in
Sap xep: [309.0, 310.0, 311.0, 312.0, 820.0] -> trung vi = 311 mm -> 19.24 in
Do tre them cua trung vi 5 so: 66 ms -> o 24 in/s robot di them 1.58 in
```

> **Bẫy.** Trung bình rất nhạy với số đo hỏng; trung vị chống được, nhưng thêm độ trễ. Bộ lọc nào cũng có giá phải
> trả — tính nó vào thiết kế.

**Đáp số:** a) trung bình **23,24 in**, trung vị **19,24 in**; b) trễ khoảng **66 ms** ⇒ thêm **1,58 in**.

</details>

<a id="bai-4-8"></a>

### Bài 4.8 ★★ — Vết đo 25°

*Thay đổi so với bài 4.1: dùng chính cảm biến khoảng cách để "thấy" một quả bóng nằm trên sàn trước robot. Mô hình đơn
giản: vùng nhìn là một hình nón có góc **toàn phần** 25°; muốn chắc chắn số đo là của quả bóng thì cả vết đo phải nằm gọn
trên quả bóng.*

**Đề.** Khoảng cách lớn nhất từ cảm biến tới NECTAR (Ø 3,6 in) và tới POLLEN (Ø 2,8 in)?

<details>
<summary><b>Lời giải 4.8</b></summary>

1. Ở khoảng cách d, vết đo có đường kính 2 × d × tan(25° / 2) = 2 × d × tan 12,5° = **0,4434 × d**.
2. NECTAR: 0,4434 × d ≤ 3,6 ⇒ d ≤ **8,12 in**.
3. POLLEN: d ≤ 2,8 / 0,4434 = **6,31 in**.
4. Xa hơn, vết đo rộng hơn quả bóng: cảm biến "thấy" cả sàn hoặc tường phía sau và số đo bị trộn.

```java
/** Bài 4.8 — Góc nhìn 25°: ở khoảng cách nào vết đo còn nằm gọn trên quả bóng? */
static void bai4_8() {
    tieuDe("4.8");
    double halfTan = Math.tan(Math.toRadians(FOV_DEG / 2));     // DÙNG NỬA GÓC
    inRa("Duong kinh vet do = 2 x d x tan(12.5) = %.4f x d", 2 * halfTan);
    for (double ball : new double[]{3.6, 2.8}) {
        inRa("Bong %.1f in: d <= %.1f / %.4f = %.2f in", ball, ball, 2 * halfTan, ball / (2 * halfTan));
    }
    inRa("SAI dung ca goc 25 lam nua goc: NECTAR d <= %.2f in", 3.6 / (2 * Math.tan(Math.toRadians(FOV_DEG))));
}
```

Kết quả chạy:

```
Duong kinh vet do = 2 x d x tan(12.5) = 0.4434 x d
Bong 3.6 in: d <= 3.6 / 0.4434 = 8.12 in
Bong 2.8 in: d <= 2.8 / 0.4434 = 6.31 in
SAI dung ca goc 25 lam nua goc: NECTAR d <= 3.86 in
```

> **Bẫy.** 25° là góc **toàn phần**: trong công thức phải dùng **nửa góc** 12,5°. Lấy 25° làm nửa góc ⇒ chỉ được 3,86 in
> — khắt khe vô ích.

**Đáp số:** NECTAR **≤ 8,1 in**; POLLEN **≤ 6,3 in**.

</details>

<a id="bai-4-9"></a>

### Bài 4.9 ★★ — NECTAR đỏ và cái bẫy khi lấy trung bình sắc độ

*Thay đổi so với bài 4.8: chuyển sang cảm biến màu ở miệng intake. Hai lần đọc liên tiếp một NECTAR đỏ (số đếm thô):
(840, 210, 260) và (820, 250, 215). Bạn E lấy trung bình sắc độ của hai lần đọc "cho đỡ nhiễu".*

**Đề.** a) Tính sắc độ và độ bão hòa của từng lần đọc. b) Trung bình của bạn E là bao nhiêu, phân loại ra gì? c) Cách
làm đúng?

<details>
<summary><b>Lời giải 4.9</b></summary>

1. a) Lần 1: max = R = 840, min = G = 210, Δ = 630 ⇒ H = 60 × (210 − 260) / 630 = −4,76° ⇒ **âm**, cộng 360 ⇒
   **355,24°**; S = 630 / 840 = 0,75.
2. Lần 2: max = R = 820, min = B = 215, Δ = 605 ⇒ H = 60 × (250 − 215) / 605 = **3,47°**; S = 0,74. Cả hai lần đều
   là đỏ (≥ 340° hoặc ≤ 20°).
3. b) Bạn E: (355,24 + 3,47) / 2 = **179,35°** — màu xanh lơ, không phải đỏ, cũng không lọt vùng xanh 200°–260° ⇒
   "không rõ": robot không nhận ra quả NECTAR đỏ.
4. c) Sắc độ là một **góc** trên vòng tròn: 355° và 3° chỉ cách nhau 8°. Cách đúng: lấy trung bình RGB trước rồi mới
   tính sắc độ — (830; 230; 237,5) ⇒ **359,25°**; hoặc lấy trung bình trên vòng tròn (cộng hai véc-tơ đơn vị, dùng
   atan2) ⇒ 359,35°.
5. Thêm: co các kênh về 0..255 (để dùng `Color.RGBToHSV` của SDK) không làm đổi sắc độ — vẫn 355,24°.

```java
/** Bài 4.9 — NECTAR đỏ: sắc độ âm, và cái bẫy khi lấy trung bình sắc độ. */
static void bai4_9() {
    tieuDe("4.9");
    double h1 = hue(840, 210, 260), h2 = hue(820, 250, 215);
    inRa("Lan 1 (840, 210, 260): 60 x (210 - 260) / 630 = %.2f -> + 360 = %.2f do, S = %.2f",
            60 * (210 - 260) / 630.0, h1, saturation(840, 210, 260));
    inRa("Lan 2 (820, 250, 215): H = %.2f do, S = %.2f", h2, saturation(820, 250, 215));
    inRa("SAI trung binh sac do: (%.2f + %.2f) / 2 = %.2f do (xanh lo!)", h1, h2, (h1 + h2) / 2);
    inRa("DUNG 1 - trung binh RGB (830, 230, 237.5): H = %.2f do", hue(830, 230, 237.5));
    double x = Math.cos(Math.toRadians(h1)) + Math.cos(Math.toRadians(h2));
    double y = Math.sin(Math.toRadians(h1)) + Math.sin(Math.toRadians(h2));
    double circular = Math.toDegrees(Math.atan2(y, x));
    if (circular < 0) circular += 360;
    inRa("DUNG 2 - trung binh tren vong tron (atan2): H = %.2f do", circular);
    inRa("Co ve 0..255 (chia 840/255): H = %.2f do (khong doi)", hue(255, 210 * 255 / 840.0, 260 * 255 / 840.0));
}
```

Kết quả chạy:

```
Lan 1 (840, 210, 260): 60 x (210 - 260) / 630 = -4.76 -> + 360 = 355.24 do, S = 0.75
Lan 2 (820, 250, 215): H = 3.47 do, S = 0.74
SAI trung binh sac do: (355.24 + 3.47) / 2 = 179.35 do (xanh lo!)
DUNG 1 - trung binh RGB (830, 230, 237.5): H = 359.25 do
DUNG 2 - trung binh tren vong tron (atan2): H = 359.35 do
Co ve 0..255 (chia 840/255): H = 355.24 do (khong doi)
```

> **Bẫy.** Hai cái bẫy trong một bài: sắc độ có thể ra số **âm** (phải cộng 360°), và **không được** lấy trung bình
> cộng các góc nằm hai bên mốc 0°/360°.

**Đáp số:** a) **355,24°** (S = 0,75) và **3,47°** (S = 0,74); b) **179,35°** ⇒ "không rõ"; c) trung bình RGB ⇒
**359,25°** (đỏ).

</details>

<a id="bai-4-10"></a>

### Bài 4.10 ★★★ — Cân bằng trắng

*Thay đổi so với bài 4.9: dưới đèn sân thi đấu, một tấm giấy trắng đọc được (1500, 2100, 1800) — kênh xanh lá nhạy hơn
hai kênh kia. Một quả POLLEN đọc được (900, 1350, 420).*

**Đề.** a) Tính sắc độ thô của POLLEN. Bộ phân loại sẽ trả về gì? b) Chia từng kênh cho số đọc của tấm trắng rồi tính
lại. c) Nếu thay vào đó nhân **cả ba** kênh với cùng một số thì sắc độ có đổi không?

<details>
<summary><b>Lời giải 4.10</b></summary>

1. a) max = G = 1350, min = B = 420, Δ = 930 ⇒ H = 60 × (2 + (420 − 900) / 930) = **89,03°** — nằm ngoài vùng POLLEN
   25°–75° ⇒ "không rõ" (trông vàng ngả xanh lá).
2. b) Cân bằng trắng: (900/1500, 1350/2100, 420/1800) = (0,6000; 0,6429; 0,2333) ⇒ H = **66,28°**, S = 0,64 ⇒ POLLEN.
3. c) Không đổi (vẫn 89,03°): nhân chung một số không làm thay đổi **tỉ lệ** giữa các kênh. Chỉ hệ số **riêng cho từng
   kênh** mới làm đổi sắc độ — và đó chính là điều cân bằng trắng sửa.

```java
/** Bài 4.10 — Cân bằng trắng: chia từng kênh cho số đo của một tấm giấy trắng. */
static void bai4_10() {
    tieuDe("4.10");
    double wr = 1500, wg = 2100, wb = 1800;            // tấm trắng dưới đèn sân
    double r = 900, g = 1350, b = 420;                 // một quả POLLEN (vàng)
    inRa("Tho: H = %.2f do, S = %.2f", hue(r, g, b), saturation(r, g, b));
    double rn = r / wr, gn = g / wg, bn = b / wb;
    inRa("Sau can bang trang: (%.4f, %.4f, %.4f) -> H = %.2f do, S = %.2f", rn, gn, bn, hue(rn, gn, bn),
            saturation(rn, gn, bn));
    inRa("Nhan ca 3 kenh voi cung mot so (vd 2): H = %.2f do (khong doi)", hue(2 * r, 2 * g, 2 * b));
}
```

Kết quả chạy:

```
Tho: H = 89.03 do, S = 0.69
Sau can bang trang: (0.6000, 0.6429, 0.2333) -> H = 66.28 do, S = 0.64
Nhan ca 3 kenh voi cung mot so (vd 2): H = 89.03 do (khong doi)
```

> **Bẫy.** Ngưỡng màu đo ở phòng tập, dùng dưới đèn sân thi đấu sẽ lệch. Mỗi khi tới sân mới, đo lại một tấm trắng
> (Phụ lục B của tài liệu 10 ví dụ). Tăng độ khuếch đại (gain) của cảm biến chỉ nhân chung các kênh nên **không** thay
> được cân bằng trắng.

**Đáp số:** a) **89,03°** ⇒ "không rõ"; b) **66,28°** ⇒ POLLEN; c) **không đổi**.

</details>

---

<a id="dap-so"></a>

## Bảng đáp số

| Bài | Đáp số | Bẫy chính |
|---|---|---|
| [1.1](#bai-1-1) | `setTargetPosition(1003)` · 41,80 tick/in | mm ↔ in |
| [1.2](#bai-1-2) | 1756 | target là vị trí tuyệt đối |
| [1.3](#bai-1-3) | 989; theo "20:1" robot lố tới 25,36 in | tỉ số trên nhãn ≠ tỉ số thật |
| [1.4](#bai-1-4) | 23,65 in; bánh trượt 0,35 in | pod không có hộp số |
| [1.5](#bai-1-5) | 7967 tick; code báo 15,77 in (−33 %) | hằng số theo đúng loại pod |
| [1.6](#bai-1-6) | 46,08°, quay trái | radian; W là khoảng cách giữa hai pod |
| [1.7](#bai-1-7) | 891 | góc mốc −30° |
| [1.8](#bai-1-8) | 79,47°, cos > 0 | mốc ở cả hai chiều đổi |
| [1.9](#bai-1-9) | 799 (57,05 tick/in) | cascade nhân đôi quãng đường |
| [1.10](#bai-1-10) | 1025 ⇒ robot đi 24,52 in | Java chia số nguyên |
| [2.1](#bai-2-1) | `setVelocity(1120)` | tick/s, không phải RPM |
| [2.2](#bai-2-2) | `setVelocity(1680)` | encoder ở trục motor; chiều tỉ số |
| [2.3](#bai-2-3) | 5,53 m/s | rad/s và bán kính |
| [2.4](#bai-2-4) | 376,99 rad/s; khai báo sai ⇒ 32 262 tick/s | `AngleUnit` là của trục motor; loại motor trong cấu hình |
| [2.5](#bai-2-5) | F = 13,77; F theo thông số chỉ đưa tới 1428 tick/s | đo thật, không dùng thông số không tải |
| [2.6](#bai-2-6) | cả 5 loại ≈ 2800 tick/s, F ≈ 11,7 | encoder nằm trước hộp số |
| [2.7](#bai-2-7) | 0; −20 480; 240 RPM | tràn số 16 bit |
| [2.8](#bai-2-8) | 64,8 in/s (4-Bar), 97,3 in/s (Swingarm); robot 93,3 in/s | pod bánh nhỏ tràn sớm |
| [2.9](#bai-2-9) | nấc 50 tick/s = 3 %; lọt qua 1660/1680/1700; ±1 % chỉ 1680 | phép đo có nấc |
| [2.10](#bai-2-10) | 11,85 ms; bỏ lỡ 40,7 % | lấy mẫu; tốc độ tại chỗ đặt cảm biến |
| [3.1](#bai-3-1) | 2,354 N·m = 24,0 kg·cm | trọng tâm, không phải chiều dài tay |
| [3.2](#bai-3-2) | 1,804 N·m | sin hay cos tùy góc mốc |
| [3.3](#bai-3-3) | u = 0,351; I = 3,39 A | đứng yên vẫn tốn dòng |
| [3.4](#bai-3-4) | kG = 0,117; I = 1,30 A; tay 39 RPM | chiều tỉ số |
| [3.5](#bai-3-5) | 300 J so với 44 J (giảm 6,84 lần) | nhiệt ∝ I² |
| [3.6](#bai-3-6) | +0,101 / +0,117 / +0,083 / 0 / −0,059 | radian; đổi dấu sau 90° |
| [3.7](#bai-3-7) | 0,1048 / 0,1170 / 0,1210 | nhân 12/V, không phải V/12 |
| [3.8](#bai-3-8) | 206,6–234,0 °/s; 0,513–0,581 s | tốc độ khi có tải |
| [3.9](#bai-3-9) | hiệu suất 46,0 % (1:1) và 54,5 % (3:1) | τ và ω ở cùng một trục |
| [3.10](#bai-3-10) | 10,36 V; 33,1 A; tay chậm 15,5 % | dòng phụ thuộc điện áp |
| [4.1](#bai-4-1) | 18,81 in | cộng phần lệch; đổi đơn vị |
| [4.2](#bai-4-2) | 18,40 in | chiếu cả phần lệch 7 in |
| [4.3](#bai-4-3) | 17,36 in (quay trái) / 19,44 in (quay phải) | dấu của phần lệch ngang |
| [4.4](#bai-4-4) | lệch phải 11,14°; tâm cách tường 18,65 in | cùng đơn vị; dùng atan |
| [4.5](#bai-4-5) | −9° ⇒ quay phải 9° | chuẩn hóa sai số góc |
| [4.6](#bai-4-6) | dừng khi cảm biến đọc ≤ 190 mm | hai độ trễ + quãng phanh |
| [4.7](#bai-4-7) | trung bình 23,24 in, trung vị 19,24 in; trễ 66 ms ⇒ 1,58 in | số đo hỏng; trễ của bộ lọc |
| [4.8](#bai-4-8) | NECTAR ≤ 8,1 in; POLLEN ≤ 6,3 in | dùng nửa góc nhìn |
| [4.9](#bai-4-9) | 355,24° và 3,47°; E ra 179,35°; đúng: 359,25° | sắc độ âm; trung bình góc |
| [4.10](#bai-4-10) | 89,03° ⇒ 66,28° sau cân bằng trắng | hệ số riêng từng kênh |

---

<a id="bay"></a>

## 20 cái bẫy cần nhớ

Đọc lại danh sách này trước mỗi lần viết code cho cơ cấu mới, và trước giải.

**Đơn vị, góc và hình học**

1. Đổi về **cùng một đơn vị** trước khi tính: mm ↔ in, m ↔ in, ms ↔ s. Một con số vô lý (307 in, 52,8 m/s) là dấu
   hiệu quên đổi. *(1.1, 2.3, 2.10, 4.1, 4.4, 4.6)*
2. Chu vi = π × **đường kính**; vận tốc dài v = ω × **bán kính**. *(1.1, 2.3)*
3. `Math.sin/cos/tan` nhận **radian**; "cung chia bán kính" cho ra radian; sai số hướng phải chuẩn hóa về
   [−180°, 180°). *(1.6, 3.6, 4.2, 4.5)*
4. Vẽ hình trước khi chọn sin hay cos: góc đo từ phương nào? Cánh tay đòn là khoảng cách **ngang** tới **trọng tâm**.
   *(3.1, 3.2, 4.3)*

**Encoder và tỉ số truyền**

5. Tỉ số = răng bánh **bị dẫn** / răng bánh **dẫn**. Tự hỏi: bên nào phải quay nhanh hơn? *(1.7, 2.2, 3.4)*
6. Tỉ số trên nhãn chỉ là tên gọi: "20:1" của UltraPlanetary là 18,93:1; "13,7:1" của goBILDA là 13,7336:1. *(1.3)*
7. Encoder đếm tick của **trục mà nó gắn vào**: trục motor, trục ra hay bánh pod. *(1.4, 2.2, 2.6)*
8. Target của `RUN_TO_POSITION` là **tuyệt đối**; tính chặng mới từ tổng quãng đường, không từ chỗ robot đang dừng.
   *(1.2)*
9. Góc mốc lúc reset encoder phải có mặt ở **cả hai chiều** đổi: góc → tick và tick → góc. *(1.7, 1.8)*
10. Đổi phần cứng thì rà lại hằng số: loại pod, loại motor khai báo trong Robot Configuration. *(1.5, 2.4)*
11. Cơ cấu nhân quãng đường (cascade) thì chia số tick mỗi inch và nhân lực cần kéo. *(1.9)*
12. Java: chia hai số nguyên ra số nguyên; `(int)` cắt phần lẻ chứ không làm tròn. *(1.10)*

**Vận tốc và phép đo**

13. `setVelocity(double)` nhận **tick/giây**; phiên bản có `AngleUnit` là góc của **trục motor**. *(2.1, 2.4)*
14. F = 32767 / vận tốc lớn nhất **đo thật**. Hộp số gần như không làm đổi F; tải và pin thì có. *(2.5, 2.6)*
15. Vận tốc encoder Hub gửi về là số **16 bit có dấu**: tràn ở 32 767 tick/s (encoder 8192 tick/vòng, pod odometry
    cắm thẳng vào Hub). Vị trí là số 32 bit nên không tràn. *(2.7, 2.8)*
16. Phép đo có **nấc**; xung ngắn hơn chu kỳ vòng lặp có thể bị lỡ. *(2.9, 2.10)*

**Motor và pin**

17. Dòng điện đi theo **mô-men**, kể cả khi đứng yên; nhiệt tỏa ra tỉ lệ với **I²**. *(3.3, 3.5)*
18. Pin mạnh hơn thì lệnh nhỏ hơn (nhân 12/V). Nhiều motor cùng ăn dòng thì pin sụt, mọi cơ cấu yếu đi cùng lúc — và khi
    u đã bằng 1 thì không bù được nữa. *(3.7, 3.10)*

**Cảm biến**

19. Số đo khoảng cách luôn **cũ** và có lúc **hỏng**: cộng độ trễ của cảm biến, của vòng lặp, của bộ lọc và quãng
    phanh; dùng trung vị để chống số hỏng; góc nhìn 25° là góc **toàn phần**. *(4.6, 4.7, 4.8)*
20. Sắc độ là một **góc**: số âm thì cộng 360°, không lấy trung bình cộng qua mốc 0°. Màu thật phải **cân bằng trắng**
    theo từng kênh; nhân chung cả ba kênh không đổi được sắc độ. *(4.9, 4.10)*

---

<a id="nguon"></a>

## Nguồn

- goBILDA — 5203 Series Yellow Jacket:
  [6000 RPM](https://www.gobilda.com/5203-series-yellow-jacket-motor-1-1-ratio-24mm-length-8mm-rex-shaft-6000-rpm-3-3-5v-encoder/) ·
  [435 RPM](https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-13-7-1-ratio-24mm-length-8mm-rex-shaft-435-rpm-3-3-5v-encoder/) ·
  [312 RPM](https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-19-2-1-ratio-24mm-length-8mm-rex-shaft-312-rpm-3-3-5v-encoder/) ·
  [117 RPM](https://www.gobilda.com/5203-series-yellow-jacket-planetary-gear-motor-50-9-1-ratio-24mm-length-8mm-rex-shaft-117-rpm-3-3-5v-encoder/)
  (tỉ số, công thức tick/vòng, mô-men và dòng điện ở 12 V); trang
  [ServoCity của motor 435 RPM](https://www.servocity.com/5203-series-yellow-jacket-planetary-gear-motor-13-7-1-ratio-24mm-length-8mm-rex-shaft-435-rpm-3-3-5v-encoder)
  ghi 383,6 tick/vòng
- goBILDA — [4-Bar Odometry Pod (32mm Wheel)](https://gobilda.com/4-bar-odometry-pod-32mm-wheel): 2000 tick/vòng
- REV Robotics — [UltraPlanetary Cartridge Details](https://docs.revrobotics.com/rev-crossover-products/ultraplanetary/cartridge-details)
  (tỉ số thật 84:29, 76:21, 68:13) ·
  [Through Bore Encoder datasheet](https://www.revrobotics.com/content/docs/REV-11-1271-DS.pdf) (8192 tick/vòng) ·
  [2m Distance Sensor](https://docs.revrobotics.com/rev-crossover-products/sensors/2m-distance) (chip VL53L0X, tầm 2 m)
- VL53L0X: [ST](https://www.st.com/en/imaging-and-photonics-solutions/vl53l0x.html) (tầm 2 m) ·
  [ESPHome](https://esphome.io/components/sensor/vl53l0x/) (thời gian đo mặc định 33 ms) ·
  [diễn đàn Pololu](https://forum.pololu.com/t/vl53l0x-beam-width-angle/11483) (góc nhìn 25° theo mục 5 của datasheet)
- Acme Robotics — road-runner-quickstart:
  [`util/Encoder.java`](https://github.com/acmerobotics/road-runner-quickstart/blob/quickstart1/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/util/Encoder.java)
  (vận tốc gửi dưới dạng 16 bit; cửa sổ đo 50 ms) và
  [`TwoDeadWheelLocalizer.java`](https://github.com/acmerobotics/road-runner-quickstart/blob/master/TeamCode/src/main/java/org/firstinspires/ftc/teamcode/TwoDeadWheelLocalizer.java)
  (pod odometry bọc bằng `OverflowEncoder`)
- Beta 8397 — [virtual_robot](https://github.com/Beta8397/virtual_robot): `GoBildaPinpointDriver` (hằng số pod),
  `DcMotorExImpl.setVelocity(double, AngleUnit)`, `AngleUnit.normalizeDegrees`
- Game Manual 0 — [Rigging](https://gm0.org/en/latest/docs/common-mechanisms/linear-motion-guide/rigging.html) (cascade:
  tầng thứ n đi nhanh gấp n lần tầng đầu)
- FIRST Tech Challenge — [Competition Manual, Section 12](https://ftc-resources.firstinspires.org/ftc/game/manual-12)
  (R601: cầu chì pin 20 A); AndyMark — [BIOBUZZ Scoring Elements](https://andymark.com/products/biobuzz-scoring-elements)
  (cỡ POLLEN, NECTAR)
- Tài liệu cùng bộ: `MOTOR_CAM_BIEN_BIOBUZZ_2026_2027.md` (mô hình motor mục 6.3.1, bảng 6.2.6, bài 6.7) và
  `VI_DU_MOTOR_CAM_BIEN_BIOBUZZ.md` (ví dụ 4, 6, 8, 9, 10; Phụ lục B)
