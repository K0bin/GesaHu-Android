package rhedox.gesahuvertretungsplan.ui.widget

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewCompat
import android.support.v4.view.WindowInsetsCompat
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import org.jetbrains.anko.forEachChild
import android.view.Gravity
import android.view.WindowInsets
import android.view.ViewGroup

/**
 * Dispatches ApplyWindowInsets to all children instead of having the first one consume it.
 */
class DispatchInsetLinearLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayoutCompat(context, attrs, defStyle) {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setOnApplyWindowInsetsListener(this,
                    { v, insets ->
                        forEachChild {
                            ViewCompat.dispatchApplyWindowInsets(it, insets)
                        }
                        return@setOnApplyWindowInsetsListener insets.consumeSystemWindowInsets()
                    });
        }
    }
}