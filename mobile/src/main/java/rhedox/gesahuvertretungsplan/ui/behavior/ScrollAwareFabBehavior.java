package rhedox.gesahuvertretungsplan.ui.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.view.ViewCompat;

public class ScrollAwareFabBehavior extends FloatingActionButton.Behavior {
    public ScrollAwareFabBehavior(Context context, AttributeSet attrs) {
        super();
    }

    private FloatingActionButton.OnVisibilityChangedListener listener = new FloatingActionButton.OnVisibilityChangedListener() {
        @Override
        public void onShown(FloatingActionButton fab) {
            super.onShown(fab);
        }

        @Override
        public void onHidden(FloatingActionButton fab) {
            super.onHidden(fab);
            fab.hide();
        }
    };

    @Override
    public boolean onStartNestedScroll(@androidx.annotation.NonNull androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout, @androidx.annotation.NonNull FloatingActionButton child, @androidx.annotation.NonNull View directTargetChild, @androidx.annotation.NonNull View target, int axes, int type) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public void onNestedScroll(@androidx.annotation.NonNull androidx.coordinatorlayout.widget.CoordinatorLayout coordinatorLayout, @androidx.annotation.NonNull FloatingActionButton child, @androidx.annotation.NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type);
        if (dyConsumed > 0) {
            // User scrolled down and the FAB is currently visible -> hide the FAB
            child.hide(listener);
        } else if (dyConsumed < 0 && child.isEnabled()) {
            // User scrolled up and the FAB is currently not visible -> show the FAB
            child.show();
        }
    }
}

//Listener workaround to this bug: https://code.google.com/p/android/issues/detail?id=230298

