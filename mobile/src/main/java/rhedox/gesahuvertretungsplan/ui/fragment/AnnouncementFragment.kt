package rhedox.gesahuvertretungsplan.ui.fragment

import android.animation.*
import android.annotation.TargetApi
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.transition.ArcMotion
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import io.codetail.animation.arcanimator.ArcAnimator
import io.codetail.animation.arcanimator.Side

import rhedox.gesahuvertretungsplan.R
import java.lang.ref.WeakReference

/**
 * Created by Robin on 10.07.2015.
 */
class AnnouncementFragment : DialogFragment(), DialogInterface.OnShowListener, DialogInterface.OnDismissListener, DialogInterface.OnKeyListener, View.OnClickListener {
    val fabPosition = PointF()
    val fabSize = PointF()
    var fabElevation = 0f

    var _showListener: WeakReference<DialogInterface.OnShowListener>? = null
    var showListener: DialogInterface.OnShowListener?
        get() = _showListener?.get()
        set(value) {
            if (value == null) {
                _showListener = null;
            } else {
                _showListener = WeakReference(value)
            }
        }
    var _dismissListener: WeakReference<DialogInterface.OnDismissListener>? = null
    var dismissListener: DialogInterface.OnDismissListener?
        get() = _dismissListener?.get()
        set(value) {
            if (value == null) {
                _dismissListener = null;
            } else {
                _dismissListener = WeakReference(value)
            }
        }

    private val fabCenter = PointF()
    private var dialogPosition: PointF? = null
    private var dialogSize: Point? = null
    private var dialogCenter: PointF? = null

    private var accentColor = 0
    private var cardColor = 0
    private var textColor = 0
    private var dialogElevation = 0f
    private var longDuration = 0L
    private var shortDuration = 0L

    private val revealPositionFraction = 0.6f;

    private var isAnimating = false;

    var text: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val a = context.theme.obtainStyledAttributes(TypedValue().data, intArrayOf(R.attr.colorAccent, R.attr.cardBackgroundColor, R.attr.textPrimary))
        accentColor = a.getColor(0, 0)
        cardColor = a.getColor(1, 0)
        textColor = a.getColor(2, 0)
        a.recycle()

        dialogElevation = context.resources.getDimension(R.dimen.dialogElevation)
        longDuration = context.resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        shortDuration = context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val announcement = arguments?.getString(ARGUMENTS_ANNOUNCEMENT)
        if (announcement != null) {
            val dialog = Dialog(context, R.style.AnnouncementDialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog)
            dialog.setOnShowListener(this)
            dialog.setOnDismissListener(this)
            dialog.setOnKeyListener(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                dialog.window.statusBarColor = 0
            }
            val dialogBackground = dialog.findViewById<View>(R.id.dialogBackground)
            val dialogBody = dialog.findViewById<TextView>(R.id.dialogBody)
            val dialogTitle = dialog.findViewById<TextView>(R.id.dialogTitle)
            dialogBody.alpha = 1f
            dialogTitle.alpha = 1f
            dialogBackground.setOnClickListener(this)
            dialogBody.text = text
            dialogBody.setTextColor(textColor)
            dialogTitle.setTextColor(textColor)

            return dialog
        } else
            return super.onCreateDialog(savedInstanceState)
    }

    private fun measureDialog(view: View) {
        dialogPosition = PointF(view.x, view.y)
        dialogSize = Point(view.width, view.height)
        dialogCenter = PointF(dialogPosition!!.x + dialogSize!!.x / 2, dialogPosition!!.y + dialogSize!!.y / 2)

        fabCenter.x = fabPosition.x + fabSize.x / 2
        fabCenter.y = fabPosition.y + fabSize.y / 2
    }

    override fun onShow(dialog: DialogInterface) {
        showListener?.onShow(dialog)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) animate(this.dialog.findViewById(R.id.dialog), true)
    }

    override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isAnimating) {
                animate(this.dialog.findViewById(R.id.dialog), false)
            }
            return true
        }
        return false
    }

    override fun onClick(v: View) {
        if (v.id == R.id.dialogBackground) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!isAnimating) {
                    animate(this.dialog.findViewById(R.id.dialog), false)
                }
            } else {
                dismiss()
            }
            return
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animate(view: View, isVisible: Boolean) {
        if (dialogPosition == null || dialogSize == null || dialogCenter == null) {
            measureDialog(view)
        }

        if (!isVisible) {
            view.x = dialogPosition!!.x
            view.y = dialogPosition!!.y
            view.visibility = View.VISIBLE
        } else {
            view.x = fabPosition.x - dialogSize!!.x * revealPositionFraction
            view.y = fabPosition.y - dialogSize!!.y * revealPositionFraction
            view.visibility = View.INVISIBLE
        }

        val biggerRadiusFraction = Math.max(revealPositionFraction, 1 - revealPositionFraction)
        val cardRevealRadius = Math.hypot(dialogSize!!.x.toDouble() * biggerRadiusFraction, dialogSize!!.y.toDouble() * biggerRadiusFraction).toFloat()
        val fabRevealRadius = Math.max(fabSize.x, fabSize.y) / 2
        val animation = ViewAnimationUtils.createCircularReveal(view, (dialogSize!!.x * revealPositionFraction).toInt(), (dialogSize!!.y * revealPositionFraction).toInt(), if (isVisible) fabRevealRadius else cardRevealRadius, if (isVisible) cardRevealRadius else fabRevealRadius)
        animation.interpolator = FastOutSlowInInterpolator()
        animation.duration = longDuration

        val colorFade = ObjectAnimator.ofObject(view, "cardBackgroundColor", ArgbEvaluator(),  if (isVisible) accentColor else cardColor,  if (isVisible) cardColor else accentColor)
        colorFade.interpolator = FastOutSlowInInterpolator()
        colorFade.duration = longDuration

        val dialogArcAnimator = ArcAnimator.createArcAnimator(view, if (isVisible) dialogCenter!!.x else fabCenter.x - dialogSize!!.x * (revealPositionFraction - 0.5f), if (isVisible) dialogCenter!!.y else fabCenter.y - dialogSize!!.y * (revealPositionFraction - 0.5f), 90f, Side.LEFT)
        dialogArcAnimator.setInterpolator(FastOutSlowInInterpolator())
        dialogArcAnimator.duration = longDuration

        val elevationAnimator = ObjectAnimator.ofObject(view, "cardElevation", FloatEvaluator(), if (isVisible) fabElevation else dialogElevation, if (isVisible) dialogElevation else fabElevation)
        elevationAnimator.interpolator = FastOutSlowInInterpolator()
        elevationAnimator.duration = longDuration

        val dialogBody = view.findViewById<View>(R.id.dialogBody)
        val opacityAnimator = ObjectAnimator.ofObject(dialogBody, "alpha", FloatEvaluator(),  if (isVisible) 0f else 1f,  if (isVisible) 1f else 0f)
        opacityAnimator.interpolator = FastOutSlowInInterpolator()
        opacityAnimator.duration = shortDuration
        opacityAnimator.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(anim: Animator?) {}
            override fun onAnimationEnd(anim: Animator?) {
                dialogBody.alpha = if (isVisible) 1f else 0f
            }
            override fun onAnimationCancel(anim: Animator?) {}
            override fun onAnimationStart(anim: Animator?) {
                dialogBody.alpha = if (isVisible) 0f else 1f
            }
        })

        val dialogTitle = view.findViewById<View>(R.id.dialogTitle)
        val titleOpacityAnimator = ObjectAnimator.ofObject(dialogTitle, "alpha", FloatEvaluator(),  if (isVisible) 0f else 1f,  if (isVisible) 1f else 0f)
        titleOpacityAnimator.interpolator = FastOutSlowInInterpolator()
        titleOpacityAnimator.duration = shortDuration
        titleOpacityAnimator.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(anim: Animator?) {}
            override fun onAnimationEnd(anim: Animator?) {
                dialogTitle.alpha = if (isVisible) 1f else 0f
            }
            override fun onAnimationCancel(anim: Animator?) {}
            override fun onAnimationStart(anim: Animator?) {
                dialogTitle.alpha = if (isVisible) 0f else 1f
            }
        })

        dialogArcAnimator.addListener(object: com.nineoldandroids.animation.Animator.AnimatorListener {
            override fun onAnimationRepeat(anim: com.nineoldandroids.animation.Animator?) {}
            override fun onAnimationEnd(anim: com.nineoldandroids.animation.Animator?) {
                if (!isVisible) {
                    dismiss()
                }
                isAnimating = false;
            }
            override fun onAnimationCancel(anim: com.nineoldandroids.animation.Animator?) {}
            override fun onAnimationStart(anim: com.nineoldandroids.animation.Animator?) {
                isAnimating = true;
            }

        })
        animation.addListener(object: Animator.AnimatorListener {
            override fun onAnimationRepeat(anim: Animator?) {}
            override fun onAnimationEnd(anim: Animator?) {
                if (!isVisible) {
                    view.visibility = View.INVISIBLE
                }
            }
            override fun onAnimationCancel(anim: Animator?) {}
            override fun onAnimationStart(anim: Animator?) {
                if (isVisible) {
                    view.visibility = View.VISIBLE
                }
            }

        })

        animation.start()
        colorFade.start()
        dialogArcAnimator.start()
        opacityAnimator.start()
        titleOpacityAnimator.start()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        dismissListener?.onDismiss(dialog)
    }

    companion object {
        val TAG = "AnnouncementDialogFragment"
        val ARGUMENTS_ANNOUNCEMENT = "Argument_Announcement"

        fun newInstance(announcement: String): AnnouncementFragment {
            val args = Bundle()
            args.putString(AnnouncementFragment.ARGUMENTS_ANNOUNCEMENT, announcement)

            val fragment = AnnouncementFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
