package rhedox.gesahuvertretungsplan.util;

import android.graphics.Color;
import android.support.design.widget.TabLayout;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Robin on 10.10.2015.
 */
public final class TabLayoutHelper {
    private TabLayoutHelper() {}

    public static void setContentInsetStart(TabLayout tabLayout, int value) {
        if(tabLayout == null)
            return;

        try {
            Field field = TabLayout.class.getDeclaredField("mContentInsetStart");
            field.setAccessible(true);
            field.setInt(tabLayout, value);

            Method method = TabLayout.class.getDeclaredMethod("applyModeAndGravity");
            method.setAccessible(true);
            method.invoke(tabLayout);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
