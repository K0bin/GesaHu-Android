package rhedox.gesahuvertretungsplan.ui.widget

import android.content.Context
import android.support.design.internal.ScrimInsetsFrameLayout
import android.support.v4.view.ViewCompat
import android.support.v4.view.WindowInsetsCompat
import android.util.AttributeSet
import org.jetbrains.anko.forEachChild

/**
 * Created by robin on 15.04.2017.
 */

/**
 * Applies a padding to the content so the content fits the system window
 */
class InsetPaddingFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ScrimInsetsFrameLayout(context, attrs, defStyleAttr) {

    override fun onInsetsChanged(insets: WindowInsetsCompat?) {
        super.onInsetsChanged(insets)
        if (insets == null)
            return;
        dispatchApplyWindowInsetsPadding(insets)
    }

    fun dispatchApplyWindowInsetsPadding(insets: WindowInsetsCompat) {
        val top = insets.systemWindowInsetTop
        this.setPadding(0, top, 0, 0);
    }
}