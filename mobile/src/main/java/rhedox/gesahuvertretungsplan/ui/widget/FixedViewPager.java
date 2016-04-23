package rhedox.gesahuvertretungsplan.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Robin on 23.04.2016.
 */
public class FixedViewPager extends ViewPager {

    public FixedViewPager(Context context) {
        super(context);
    }

    public FixedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        // prevent NPE if fake dragging and touching ViewPager
        if(isFakeDragging()) return false;

        return super.onInterceptTouchEvent(ev);
    }
}
