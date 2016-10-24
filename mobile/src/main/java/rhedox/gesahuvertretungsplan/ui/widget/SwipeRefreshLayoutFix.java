package rhedox.gesahuvertretungsplan.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Robin on 18.09.2015.
 * Workaround/ fix for SwipeRefreshLayout
 */
public class SwipeRefreshLayoutFix extends SwipeRefreshLayout {
	private boolean isGuestureEnabled = false;

    public SwipeRefreshLayoutFix(Context context) {
        super(context);
    }

    public SwipeRefreshLayoutFix(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

	public void setIsGuestureEnabled(boolean isEnabled) {
		isGuestureEnabled = isEnabled;
	}
	public boolean getIsGuestureEnabled() {
		return isGuestureEnabled;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_POINTER_UP:
				return false;
			default:
				return isGuestureEnabled && super.onInterceptTouchEvent(ev);
		}
	}
}
