package rhedox.gesahuvertretungsplan.ui.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import rhedox.gesahuvertretungsplan.R

/**
 * Created by robin on 26.01.2017.
 */
class ContextualActionBar : Toolbar {
    private lateinit var cabFadeIn: Animation;
    private lateinit var cabFadeOut: Animation;
    private lateinit var cabDrawerAnimator: ValueAnimator;
    private lateinit var cabDrawerIcon: DrawerArrowDrawable;

    constructor(context: Context) : super(context) { init() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { init() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    fun init() {
        cabDrawerIcon = DrawerArrowDrawable(context)
        cabDrawerIcon.color = Color.WHITE
        navigationIcon = cabDrawerIcon
        cabFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        cabFadeIn.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {visibility = View.VISIBLE}
            override fun onAnimationStart(animation: Animation) {visibility = View.VISIBLE}
        })
        cabFadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        cabFadeOut.setAnimationListener(object: Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {visibility = View.GONE}
            override fun onAnimationStart(animation: Animation) {visibility = View.VISIBLE}
        })
        cabDrawerAnimator = ValueAnimator.ofFloat(0f, 1f)
        cabDrawerAnimator.addUpdateListener(ValueAnimator.AnimatorUpdateListener { valueAnimator ->
            val slideOffset = valueAnimator.animatedValue as Float
            cabDrawerIcon.progress = slideOffset
        })
        cabDrawerAnimator.interpolator = DecelerateInterpolator()
        //cabDrawerAnimator.duration = 250
    }

    fun hide() {
        clearAnimation()
        startAnimation(cabFadeOut)
    }

    fun show() {
        clearAnimation()
        startAnimation(cabFadeIn)
    }
}