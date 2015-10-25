package rhedox.gesahuvertretungsplan.util;

import android.graphics.Color;
import android.support.annotation.ColorInt;

/**
 * Created by Robin on 04.05.2015.
 */
public final class ColorHelper {
    private ColorHelper() {}
    public static float getLuminance(@ColorInt int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return 0.299f * r + 0.587f * g + 0.114f * b;
    }
    public static boolean isDark(float luminance) {
        return luminance <= 127;
    }
}

