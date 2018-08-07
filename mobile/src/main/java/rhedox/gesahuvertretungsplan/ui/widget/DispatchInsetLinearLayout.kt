package rhedox.gesahuvertretungsplan.ui.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.ViewCompat
import org.jetbrains.anko.forEachChild

/**
 * Dispatches ApplyWindowInsets to all children instead of having the first one consume it.
 */
class DispatchInsetLinearLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : LinearLayoutCompat(context, attrs, defStyle) {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
                forEachChild {
                    ViewCompat.dispatchApplyWindowInsets(it, insets)
                }
                return@setOnApplyWindowInsetsListener insets.consumeSystemWindowInsets()
            };
        }
    }
}