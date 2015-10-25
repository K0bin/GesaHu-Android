package rhedox.gesahuvertretungsplan.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Robin on 18.09.2015.
 * Workaround/ fix for SwipeRefreshLayout
 */
public class SwipeRefreshLayoutFix extends SwipeRefreshLayout {
    private boolean canRefresh = false;

    public SwipeRefreshLayoutFix(Context context) {
        super(context);
    }

    public SwipeRefreshLayoutFix(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setRefreshing(boolean refreshing) {
        if (!refreshing || canRefresh) {
            super.setRefreshing(refreshing);
        } else {
            this.post(new Runnable() {
                @Override
                public void run() {
                    canRefresh = true;
                    setRefreshing(true);
                }
            });
        }
    }
}
