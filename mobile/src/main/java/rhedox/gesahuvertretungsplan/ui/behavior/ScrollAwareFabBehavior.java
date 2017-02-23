package rhedox.gesahuvertretungsplan.ui.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

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
            fab.setVisibility(View.INVISIBLE);
        }
    };

    @Override
    public boolean onStartNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       final View directTargetChild, final View target, final int nestedScrollAxes) {
        // Ensure we react to vertical scrolling
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
                || super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(final CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
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

