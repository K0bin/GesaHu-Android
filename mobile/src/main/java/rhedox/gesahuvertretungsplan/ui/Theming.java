package rhedox.gesahuvertretungsplan.ui;

import android.support.design.widget.TabLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import rhedox.gesahuvertretungsplan.R;

/**
 * Created by Robin on 16.07.2015.
 */
public final class Theming {

    public static final HashMap<Integer, Integer> THEMES = new HashMap<Integer, Integer>();
    public static final HashMap<Integer, Integer> THEMES_DARK = new HashMap<Integer, Integer>();
    static {
        THEMES.put(0xFFFF1744, R.style.red1);
        THEMES.put(0xFFD50000, R.style.red2);

        THEMES.put(0xFFF50057, R.style.pink1);
        THEMES.put(0xFFC51162, R.style.pink2);

        THEMES.put(0xFFD500F9, R.style.purple1);
        THEMES.put(0xFFAA00FF, R.style.purple2);

        THEMES.put(0xFF651FFF, R.style.deep_purple1);
        THEMES.put(0xFF6200EA, R.style.deep_purple2);

        THEMES.put(0xFF3D5AFE, R.style.indigo1);
        THEMES.put(0xFF304FFE, R.style.indigo2);

        THEMES.put(0xFF2979FF, R.style.blue1);
        THEMES.put(0xFF2962FF, R.style.blue2);

        THEMES.put(0xFF00B0FF, R.style.light_blue1);
        THEMES.put(0xFF0091EA, R.style.light_blue2);

        THEMES.put(0xFF00E5FF, R.style.cyan1);
        THEMES.put(0xFF00B8D4, R.style.cyan2);

        THEMES.put(0xFF1DE9B6, R.style.teal1);
        THEMES.put(0xFF00BFA5, R.style.teal2);

        THEMES.put(0xFF00E676, R.style.green1);
        THEMES.put(0xFF00C853, R.style.green2);

        THEMES.put(0xFF76FF03, R.style.light_green1);
        THEMES.put(0xFF64DD17, R.style.light_green2);

        THEMES.put(0xFFC6FF00, R.style.lime1);
        THEMES.put(0xFFAEEA00, R.style.lime2);

        THEMES.put(0xFFFFEA00, R.style.yellow1);
        THEMES.put(0xFFFFD600, R.style.yellow2);

        THEMES.put(0xFFFFC400, R.style.amber1);
        THEMES.put(0xFFFFAB00, R.style.amber2);

        THEMES.put(0xFFFF9100, R.style.orange1);
        THEMES.put(0xFFFF6D00, R.style.orange2);

        THEMES.put(0xFFFF3D00, R.style.deep_orange1);
        THEMES.put(0xFFDD2C00, R.style.deep_orange2);


        THEMES_DARK.put(0xFFFF1744, R.style.red1_dark);
        THEMES_DARK.put(0xFFD50000, R.style.red2_dark);

        THEMES_DARK.put(0xFFF50057, R.style.pink1_dark);
        THEMES_DARK.put(0xFFC51162, R.style.pink2_dark);

        THEMES_DARK.put(0xFFD500F9, R.style.purple1_dark);
        THEMES_DARK.put(0xFFAA00FF, R.style.purple2_dark);

        THEMES_DARK.put(0xFF651FFF, R.style.deep_purple1_dark);
        THEMES_DARK.put(0xFF6200EA, R.style.deep_purple2_dark);

        THEMES_DARK.put(0xFF3D5AFE, R.style.indigo1_dark);
        THEMES_DARK.put(0xFF304FFE, R.style.indigo2_dark);

        THEMES_DARK.put(0xFF2979FF, R.style.blue1_dark);
        THEMES_DARK.put(0xFF2962FF, R.style.blue2_dark);

        THEMES_DARK.put(0xFF00B0FF, R.style.light_blue1_dark);
        THEMES_DARK.put(0xFF0091EA, R.style.light_blue2_dark);

        THEMES_DARK.put(0xFF00E5FF, R.style.cyan1_dark);
        THEMES_DARK.put(0xFF00B8D4, R.style.cyan2_dark);

        THEMES_DARK.put(0xFF1DE9B6, R.style.teal1_dark);
        THEMES_DARK.put(0xFF00BFA5, R.style.teal2_dark);

        THEMES_DARK.put(0xFF00E676, R.style.green1_dark);
        THEMES_DARK.put(0xFF00C853, R.style.green2_dark);

        THEMES_DARK.put(0xFF76FF03, R.style.light_green1_dark);
        THEMES_DARK.put(0xFF64DD17, R.style.light_green2_dark);

        THEMES_DARK.put(0xFFC6FF00, R.style.lime1_dark);
        THEMES_DARK.put(0xFFAEEA00, R.style.lime2_dark);

        THEMES_DARK.put(0xFFFFEA00, R.style.yellow1_dark);
        THEMES_DARK.put(0xFFFFD600, R.style.yellow2_dark);

        THEMES_DARK.put(0xFFFFC400, R.style.amber1_dark);
        THEMES_DARK.put(0xFFFFAB00, R.style.amber2_dark);

        THEMES_DARK.put(0xFFFF9100, R.style.orange1_dark);
        THEMES_DARK.put(0xFFFF6D00, R.style.orange2_dark);

        THEMES_DARK.put(0xFFFF3D00, R.style.deep_orange1_dark);
        THEMES_DARK.put(0xFFDD2C00, R.style.deep_orange2_dark);
    }

    private Theming() {}

    public static int getTheme(boolean darkTheme, int color) {
        if(!darkTheme) {
            if (THEMES.containsKey(color))
                return THEMES.get(color);
            else
                return R.style.GesahuTheme;
        } else {
            if (THEMES_DARK.containsKey(color))
                return THEMES_DARK.get(color);
            else
                return R.style.GesahuThemeDark;
        }
    }

    //Workaround
    public static void setTabSelectorColor(TabLayout tabs, int color) {
        try {
            Field field = TabLayout.class.getDeclaredField("mTabStrip");
            field.setAccessible(true);
            Object value = field.get(tabs);

            Method method = value.getClass().getDeclaredMethod("setSelectedIndicatorColor", Integer.TYPE);
            method.setAccessible(true);
            method.invoke(value, color);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
