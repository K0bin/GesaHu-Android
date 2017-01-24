package rhedox.gesahuvertretungsplan.util

import android.app.Activity
import android.os.Build
import android.transition.Transition
import android.transition.TransitionManager
import android.util.ArrayMap
import android.view.ViewGroup
import java.lang.ref.WeakReference
import java.util.*

/**
 * Created by robin on 24.01.2017.
 */

fun Activity.removeActivityFromTransitionManager() {
    if (Build.VERSION.SDK_INT < 21) {
        return;
    } else return;
    val transitionManagerClass = TransitionManager::class.java;
    try {
        val runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
        runningTransitionsField.isAccessible = true;
        //noinspection unchecked
        val runningTransitions = runningTransitionsField.get(transitionManagerClass) as ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>;
        if (runningTransitions.get() == null || runningTransitions.get().get() == null) {
            return;
        }
        val map = runningTransitions.get().get();
        val decorView = this.window.decorView;
        if (map.containsKey(decorView)) {
            map.remove(decorView);
        }
    } catch (e: NoSuchFieldException) {
        e.printStackTrace();
    } catch (e: IllegalAccessException ) {
        e.printStackTrace();
    }
}