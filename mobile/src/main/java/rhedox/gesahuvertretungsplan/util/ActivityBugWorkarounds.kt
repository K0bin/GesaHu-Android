package rhedox.gesahuvertretungsplan.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.transition.Transition
import android.transition.TransitionManager
import android.util.ArrayMap
import android.view.ViewGroup
import java.lang.ref.WeakReference
import java.util.*
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * Created by robin on 24.01.2017.
 */

fun Activity.removeActivityFromTransitionManager() {
    if (Build.VERSION.SDK_INT < 21) {
        return;
    }
    val transitionManagerClass = TransitionManager::class.java;
    try {
        val runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
        runningTransitionsField.isAccessible = true;
        //noinspection unchecked
        val runningTransitions = runningTransitionsField.get(transitionManagerClass) as? ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>;
        val map = runningTransitions?.get()?.get();
        val decorView = this.window.decorView;
        if (map?.containsKey(decorView) ?: false) {
            map?.remove(decorView);
        }
    } catch (e: NoSuchFieldException) {
        e.printStackTrace();
    } catch (e: IllegalAccessException ) {
        e.printStackTrace();
    }
}

/**
 * @author androidmalin
 */
fun Activity.fixInputMethod() {
    val context = this;
    var inputMethodManager: InputMethodManager? = null
    try {
        inputMethodManager = context.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    } catch (th: Throwable) {
        th.printStackTrace()
    }

    if (inputMethodManager == null) return
    val strArr = arrayOf("mCurRootView", "mServedView", "mNextServedView")
    for (i in 0..2) {
        try {
            val declaredField = inputMethodManager.javaClass.getDeclaredField(strArr[i]) ?: continue
            if (!declaredField.isAccessible()) {
                declaredField.setAccessible(true)
            }
            val obj = declaredField.get(inputMethodManager)
            if (obj == null || obj !is View) continue
            if (obj.context === context) {
                declaredField.set(inputMethodManager, null)
            } else {
                return
            }
        } catch (th: Throwable) {
            th.printStackTrace()
        }
    }
}
