package org.firstinspires.ftc.teamcode.course.util;

import android.graphics.Color;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/** Nhận dạng màu bóng DECODE (tím / xanh lá) bằng sắc độ (hue). */
public final class ColorUtil {
    private ColorUtil() {}

    /** 'P' = tím, 'G' = xanh lá, '-' = không có bóng (cảm biến khoảng cách thấy xa hơn 5 cm) hoặc không rõ. */
    public static char artifactColor(ColorSensor color, DistanceSensor distance) {
        if (distance.getDistance(DistanceUnit.CM) > 5) return '-';
        float[] hsv = new float[3];
        Color.RGBToHSV(color.red(), color.green(), color.blue(), hsv);
        if (hsv[1] < 0.3) return '-';
        if (hsv[0] > 90 && hsv[0] < 170) return 'G';
        if (hsv[0] >= 250 && hsv[0] < 320) return 'P';
        return '-';
    }
}
