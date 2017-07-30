package rhedox.gesahuvertretungsplan.ui.fragment

import android.support.v4.app.Fragment
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import rhedox.gesahuvertretungsplan.R

/**
 * Created by robin on 30.07.2017.
 */
abstract class AnimationFragment: Fragment() {
    var useSlideAnimation = false

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation {
        if (useSlideAnimation) {
            useSlideAnimation = false;
            return if (enter) AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right) else AnimationUtils.loadAnimation(context, R.anim.slide_out_to_left)
        } else {
            return if (enter) AnimationUtils.loadAnimation(context, R.anim.fade_in) else AnimationUtils.loadAnimation(context, R.anim.fade_out)
        }
    }
}