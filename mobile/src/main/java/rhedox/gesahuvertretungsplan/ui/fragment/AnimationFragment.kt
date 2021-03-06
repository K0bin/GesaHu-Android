package rhedox.gesahuvertretungsplan.ui.fragment

import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import rhedox.gesahuvertretungsplan.R

/**
 * Created by robin on 30.07.2017.
 */
abstract class AnimationFragment: Fragment() {
    var useSlideAnimation = false

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val animation = if (useSlideAnimation) {
            if (enter) AnimationUtils.loadAnimation(context, R.anim.slide_in_from_right) else AnimationUtils.loadAnimation(context, R.anim.slide_out_to_left)
        } else {
            if (enter) AnimationUtils.loadAnimation(context, R.anim.fade_in) else AnimationUtils.loadAnimation(context, R.anim.fade_out)
        }
        useSlideAnimation = false
        return animation
    }
}