package rhedox.gesahuvertretungsplan.ui.view_holder

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision
import rhedox.gesahuvertretungsplan.mvp.SupervisionsContract

/**
 * Created by robin on 19.01.2017.
 */
class SupervisionViewHolder(private val view: View, private val presenter: SupervisionsContract.Presenter, circleColor: Int, circleColorRelevant: Int, @Dimension private val selectedElevation: Float) : ModelViewHolder<Supervision>(view) {
    private val relevant = view.findViewById<ImageView>(R.id.relevant)
    private val time = view.findViewById<TextView>(R.id.time)
    private val substituteTeacher = view.findViewById<TextView>(R.id.substituteTeacher)
    private val teacher = view.findViewById<TextView>(R.id.teacher)
    private val location = view.findViewById<TextView>(R.id.location)

    private var circleRelevantBackground: Drawable? = ContextCompat.getDrawable(view.context, R.drawable.circle)
    private var circleBackground: Drawable? = ContextCompat.getDrawable(view.context, R.drawable.circle)
    @ColorInt private val selectedColor: Int = ContextCompat.getColor(view.context, R.color.selected)

    private val backgroundAnimator: ObjectAnimator
    private val elevationAnimator: ObjectAnimator?

    init {
        backgroundAnimator = ObjectAnimator.ofObject(view, "backgroundColor", ArgbEvaluator(), 0, selectedColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevationAnimator = ObjectAnimator.ofFloat(view, "elevation", 0f, selectedElevation)
        } else {
            elevationAnimator = null
        }

        view.isClickable = true

        if (circleBackground != null) {
            circleBackground = DrawableCompat.wrap(circleBackground!!)
            DrawableCompat.setTint(circleBackground!!, circleColor)
        }
        if (circleRelevantBackground != null) {
            circleRelevantBackground = DrawableCompat.wrap(circleRelevantBackground!!)
            DrawableCompat.setTint(circleRelevantBackground!!, circleColorRelevant)
        }

        view.setOnClickListener {
            presenter.onListItemClicked(this.adapterPosition)
        }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun bind(supervision: Supervision) {
        time.text = supervision.time
        teacher.text = supervision.teacher
        substituteTeacher.text = supervision.substitute
        location.text = supervision.location
        if (supervision.isRelevant) {
            relevant.background = circleRelevantBackground
            time.typeface = Typeface.DEFAULT_BOLD
        } else {
            relevant.background = circleBackground
            time.typeface = Typeface.DEFAULT
        }
    }

    fun setSelected(selected: Boolean, animate: Boolean) {
        view.isActivated = selected
        if (animate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (selected) {
                backgroundAnimator.start()
                elevationAnimator?.start()
            } else {
                backgroundAnimator.reverse()
                elevationAnimator?.reverse()
            }
        } else {
            if (selected) {
                view.setBackgroundColor(selectedColor)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.elevation = selectedElevation
                }
            } else {
                view.setBackgroundColor(0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.elevation = 0f
                }
            }
        }
    }
}