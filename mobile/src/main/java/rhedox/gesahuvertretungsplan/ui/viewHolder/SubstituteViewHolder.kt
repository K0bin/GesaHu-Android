package rhedox.gesahuvertretungsplan.ui.viewHolder

import android.animation.*
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.Dimension
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract

/**
 * Created by robin on 19.01.2017.
 */
class SubstituteViewHolder(private val view: View, private val presenter: SubstitutesContract.Presenter, @ColorInt private val textColor: Int, @ColorInt private val textColorRelevant: Int, circleColor: Int, circleColorRelevant: Int, @Dimension private val selectedElevation: Float) : ModelViewHolder<Substitute>(view) {
    private val lesson = view.findViewById<TextView>(R.id.lesson)
    private val subject = view.findViewById<TextView>(R.id.subject)
    private val substituteTeacher = view.findViewById<TextView>(R.id.substituteTeacher)
    private val teacher = view.findViewById<TextView>(R.id.teacher)
    private val room = view.findViewById<TextView>(R.id.room)
    private val hint = view.findViewById<TextView>(R.id.hint)

    private var circleRelevantBackground: Drawable = ContextCompat.getDrawable(view.context, R.drawable.circle)
    private var circleBackground: Drawable = ContextCompat.getDrawable(view.context, R.drawable.circle)
    @ColorInt private val selectedColor: Int = ContextCompat.getColor(view.context, R.color.selected)

    private val backgroundAnimator: ObjectAnimator;
    private val elevationAnimator: ObjectAnimator?;

    init {
        backgroundAnimator = ObjectAnimator.ofObject(view, "backgroundColor", ArgbEvaluator(), 0, selectedColor)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevationAnimator = ObjectAnimator.ofFloat(view, "elevation", 0f, selectedElevation)
        } else {
            elevationAnimator = null;
        }

        view.isClickable = true

        circleBackground = DrawableCompat.wrap(circleBackground)
        DrawableCompat.setTint(circleBackground, circleColor)
        circleRelevantBackground = DrawableCompat.wrap(circleRelevantBackground)
        DrawableCompat.setTint(circleRelevantBackground, circleColorRelevant)

        view.setOnClickListener {
            presenter.onListItemClicked(this.adapterPosition)
        }
    }

    override fun bind(substitute: Substitute) {
        lesson.text = substitute.lessonText
        subject.text = substitute.title
        teacher.text = substitute.teacher
        substituteTeacher.text = substitute.substitute
        room.text = substitute.room
        hint.text = substitute.hint
        if (substitute.isRelevant) {
            lesson.background = circleRelevantBackground
            subject.typeface = Typeface.DEFAULT_BOLD
            lesson.setTextColor(textColorRelevant)
        } else {
            lesson.background = circleBackground
            subject.typeface = Typeface.DEFAULT
            lesson.setTextColor(textColor)
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