package org.firstinspires.ftc.teamcode.course.p2;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Bản ĐỎ của P2_DecodeAutoBlue: mọi vị trí được lấy đối xứng qua đường x = 72 bằng PoseFactory.mirrorX(72)
 *   x -> 144 − x,  heading -> 180° − heading.   Đặt robot tại (88, 9) nhìn lên trước khi INIT.
 */
@Autonomous(name = "P2_DecodeAutoRed", group = "P2-Pedro")
public class P2_DecodeAutoRed extends P2_DecodeAutoBlue {
    public P2_DecodeAutoRed() {
        red = true;
    }
}
