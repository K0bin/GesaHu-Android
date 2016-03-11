

/*package rhedox.gesahuvertretungsplan.ui.behavior;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import rhedox.gesahuvertretungsplan.ui.widget.SwipeRefreshView;

/**
 * Created by Robin on 26.02.2016.

public class SwipeRefreshBehavior extends CoordinatorLayout.Behavior<SwipeRefreshView> {

    private boolean appBarCanScroll = false;
    private int appBarHeight = 0;



    private float totalUnconsumed = 0;
    private boolean isNestedScrollInProgress = false;



    public SwipeRefreshBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, SwipeRefreshView child, View directTargetChild, View target, int nestedScrollAxes) {
        coordinatorLayout.bringChildToFront(child);

        return !child.isRefreshing() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && !ViewCompat.canScrollVertically(target, -1) && !appBarCanScroll;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, SwipeRefreshView child, View target, int dx, int dy, int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && totalUnconsumed > 0) {
            if (dy > totalUnconsumed) {
                consumed[1] = dy - (int) totalUnconsumed;
                totalUnconsumed = 0;
            } else {
                totalUnconsumed -= dy;
                consumed[1] = dy;

            }
            moveSpinner(totalUnconsumed);
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
                && Math.abs(dy - consumed[1]) > 0) {
            child.mCircleView.setVisibility(View.GONE);
        }

        // Now let our nested parent consume the leftovers
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    private void moveSpinner(float overscrollTop) {
        mProgress.showArrow(true);
        float originalDragPercent = overscrollTop / mTotalDragDistance;

        float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        float extraOS = Math.abs(overscrollTop) - mTotalDragDistance;
        float slingshotDist = mUsingCustomStart ? mSpinnerFinalOffset - mOriginalOffsetTop
                : mSpinnerFinalOffset;
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, slingshotDist * 2)
                / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                (tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent * 2;

        int targetY = mOriginalOffsetTop + (int) ((slingshotDist * dragPercent) + extraMove);
        // where 1.0f is a full circle
        if (mCircleView.getVisibility() != View.VISIBLE) {
            mCircleView.setVisibility(View.VISIBLE);
        }
        if (!mScale) {
            ViewCompat.setScaleX(mCircleView, 1f);
            ViewCompat.setScaleY(mCircleView, 1f);
        }

        if (mScale) {
            setAnimationProgress(Math.min(1f, overscrollTop / mTotalDragDistance));
        }
        if (overscrollTop < mTotalDragDistance) {
            if (mProgress.getAlpha() > STARTING_PROGRESS_ALPHA
                    && !isAnimationRunning(mAlphaStartAnimation)) {
                // Animate the alpha
                startProgressAlphaStartAnimation();
            }
        } else {
            if (mProgress.getAlpha() < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation();
            }
        }
        float strokeStart = adjustedPercent * .8f;
        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart));
        mProgress.setArrowScale(Math.min(1f, adjustedPercent));

        float rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f;
        mProgress.setProgressRotation(rotation);
        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop, true /* requires update );
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, SwipeRefreshView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if(dyUnconsumed < 0) {
            ViewCompat.setTranslationY(child, appBarHeight + 192 - dyUnconsumed);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, SwipeRefreshView child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }

    @Override
    public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, SwipeRefreshView child, View directTargetChild, View target, int nestedScrollAxes) {
        isNestedScrollInProgress = true;
        totalUnconsumed = 0;

    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, SwipeRefreshView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, SwipeRefreshView child, View dependency) {
        appBarCanScroll = ViewCompat.canScrollVertically(dependency, -1);
        appBarHeight = ViewCompat.getMinimumHeight(dependency);

        return true;
    }
}


    <View
        android:layout_width="64dp"
        android:layout_height="64dp"
        android:background="@android:color/holo_blue_bright"
		android:layout_gravity="top|center_horizontal"
		app:layout_behavior="rhedox.gesahuvertretungsplan.ui.behavior.SwipeRefreshBehavior"/>
 */
