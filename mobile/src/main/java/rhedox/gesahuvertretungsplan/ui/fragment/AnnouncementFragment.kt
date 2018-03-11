package rhedox.gesahuvertretungsplan.ui.fragment

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Point
import android.graphics.PointF
import android.os.Build
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.CardView
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.view.Window
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

    var toDismiss = false

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val a = context!!.theme.obtainStyledAttributes(TypedValue().data, intArrayOf(R.attr.colorAccent, R.attr.cardBackgroundColor, R.attr.textPrimary))
        accentColor = a.getColor(0, 0)
        cardColor = a.getColor(1, 0)
        textColor = a.getColor(2, 0)
        a.recycle()

        dialogElevation = context!!.resources.getDimension(R.dimen.dialogElevation)
        longDuration = context!!.resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        shortDuration = context!!.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        toDismiss = savedInstanceState?.getBoolean(stateDismiss, false) ?: false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val announcement = arguments?.getString(argumentAnnouncement)
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

        Log.d("FabAnimation", "fabCenter: $fabCenter")
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

    override fun onResume() {
        super.onResume()
        if (toDismiss) {
            dismiss()
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
            view.x = fabCenter.x - dialogSize!!.x * revealPositionFraction
            view.y = fabCenter.y - dialogSize!!.y * revealPositionFraction
            view.visibility = View.INVISIBLE
        }

        val biggerRadiusFraction = Math.max(revealPositionFraction, 1 - revealPositionFraction)
        val cardRevealRadius = Math.hypot(dialogSize!!.x.toDouble() * biggerRadiusFraction, dialogSize!!.y.toDouble() * biggerRadiusFraction).toFloat()
        val fabRevealRadius = Math.max(fabSize.x, fabSize.y) / 2
        val animation = ViewAnimationUtils.createCircularReveal(view, (dialogSize!!.x * revealPositionFraction).toInt(), (dialogSize!!.y * revealPositionFraction).toInt(), if (isVisible) fabRevealRadius else cardRevealRadius, if (isVisible) cardRevealRadius else fabRevealRadius)
        animation.interpolator = FastOutSlowInInterpolator()
        animation.duration = longDuration

        val colorFade = ObjectAnimator.ofObject(view as CardView, "cardBackgroundColor", ArgbEvaluator(),  if (isVisible) accentColor else cardColor,  if (isVisible) cardColor else accentColor)
        colorFade.interpolator = FastOutSlowInInterpolator()
        colorFade.duration = longDuration

        Log.d("FabAnimation", "ArcTarget (hide): {x: ${fabCenter.x - dialogSize!!.x * (revealPositionFraction - 0.5f)} y: ${fabCenter.y - dialogSize!!.y * (revealPositionFraction - 0.5f)}}")
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
                    if (isResumed) {
                        dismiss()
                    } else {
                        toDismiss = true;
                    }
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
        toDismiss = false
        dismissListener?.onDismiss(dialog)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBoolean(stateDismiss, toDismiss)
    }

    companion object {
        const val tag = "AnnouncementDialogFragment"
        const val argumentAnnouncement = "Argument_Announcement"
        const val stateDismiss = "dismiss"

        fun newInstance(announcement: String): AnnouncementFragment {
            val args = Bundle()
            args.putString(AnnouncementFragment.argumentAnnouncement, announcement)

            val fragment = AnnouncementFragment()
            fragment.arguments = args

            return fragment
        }
    }
}
