package rhedox.gesahuvertretungsplan.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Robin on 18.09.2015.
 * Workaround/ fix for SwipeRefreshLayout
 */
public class SwipeRefreshLayoutFix extends SwipeRefreshLayout {
	private boolean isGestureEnabled = false;

    public SwipeRefreshLayoutFix(Context context) {
        super(context);
    }

    public SwipeRefreshLayoutFix(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

	public void setIsGestureEnabled(boolean isEnabled) {
		isGestureEnabled = isEnabled;
	}
	public boolean getIsGestureEnabled() {
		return isGestureEnabled;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_POINTER_UP:
				return false;
			default:
				return isGestureEnabled && super.onInterceptTouchEvent(ev);
		}
	}
}
