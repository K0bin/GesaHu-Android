package rhedox.gesahuvertretungsplan.ui.anko

import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.text.TextUtils
import android.text.style.TextAppearanceSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import org.jetbrains.anko.*
import rhedox.gesahuvertretungsplan.R

/**
 * Created by robin on 17.11.2016.
 */
class SubstituteView(context: Context) {
    val selectableItemId: Int;
    val textColorPrimaryInverse: Int;
    val textColorPrimary: Int;
    val textColorHint: Int;
    val textColorSecondary: Int;
    val tileHeight: Int = context.dimen(R.dimen.tileHeight);
    val tilePadding: Int = context.dimen(R.dimen.tilePadding);
    val condensed = Typeface.create("sans-serif-condensed", Typeface.NORMAL)

    init {
        val attributes = context.theme.obtainStyledAttributes(intArrayOf(R.attr.selectableItemBackground, android.R.attr.textColorPrimaryInverse, android.R.attr.textColorPrimary, android.R.attr.textColorHint, android.R.attr.textColorSecondary));
        selectableItemId = attributes.getResourceId(0, -1);
        textColorPrimaryInverse = attributes.getColor(1, -1)
        textColorPrimary = attributes.getColor(2, -1)
        textColorHint = attributes.getColor(3, -1)
        textColorSecondary = attributes.getColor(4, -1)
        attributes.recycle()
    }

    fun createView(parent: ViewGroup): View {
        val selectableItem = ContextCompat.getDrawable(parent.context, selectableItemId)

        return with(parent.context) {
            relativeLayout {
                id = R.id.rootFrame
                minimumHeight = tileHeight
                foreground = selectableItem
                layoutTransition = null

                //Lesson
                textView {
                    id = R.id.lesson
                    gravity = android.view.Gravity.CENTER
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18f)
                    text = "Subject"
                    textColor = textColorPrimaryInverse
                    typeface = condensed

                    lines = 1
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    includeFontPadding = false
                }.lparams {
                    width = dip(40)
                    height = dip(40)
                    margin = dip(16)
                }

                //Subject
                textView {
                    id = R.id.subject
                    text = "Deutsch Förderu. 05a"
                    setTextAppearance(R.style.TextAppearance_AppCompat_Subhead)
                    textColor = textColorPrimary

                    lines = 1
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    includeFontPadding = false
                }.lparams {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        marginStart = dip(72)
                        marginEnd = dip(8)
                    } else {
                        leftMargin = dip(72)
                        rightMargin = dip(8)
                    }
                    topMargin = dip(16)
                }

                //Room
                textView {
                    id = R.id.room
                    text = "Raum 08-05"
                    setTextAppearance(R.style.TextAppearance_AppCompat_Caption)
                    textColor = textColorHint
                    gravity = GravityCompat.END
                    typeface = condensed

                    lines = 1
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    includeFontPadding = false

                }.lparams {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        marginEnd = dip(16)
                        marginStart = dip(8)
                        alignParentEnd()
                    } else {
                        rightMargin = dip(16)
                        leftMargin = dip(8)
                        alignParentRight()
                    }
                    addRule(RelativeLayout.ALIGN_BASELINE, R.id.subject)
                }

                //Teacher
                textView {
                    id = R.id.teacher
                    text = "Lindenberg"
                    setTextAppearance(R.style.TextAppearance_AppCompat_Body1)
                    textColor = textColorSecondary
                    gravity = Gravity.CENTER_VERTICAL

                    lines = 1
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    includeFontPadding = false
                }.lparams {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        marginStart = dip(72)
                        marginEnd = dip(8)
                    } else {
                        leftMargin = dip(72)
                        rightMargin = dip(8)
                    }
                    below(R.id.subject)
                }

                //Substitute
                textView {
                    id = R.id.substituteTeacher
                    text = "Körschner"
                    setTextAppearance(R.style.TextAppearance_AppCompat_Body1)
                    textColor = textColorSecondary
                    gravity = Gravity.CENTER_VERTICAL

                    lines = 1
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    includeFontPadding = false
                }.lparams {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        marginStart = dip(8)
                        marginEnd = dip(16)
                    } else {
                        leftMargin = dip(8)
                        rightMargin = dip(16)
                    }
                    alignParentRight()
                    addRule(RelativeLayout.ALIGN_BASELINE, R.id.teacher)
                }

                //Hint
                textView {
                    id = R.id.hint
                    text = "Klausur findet statt"
                    setTextAppearance(R.style.TextAppearance_AppCompat_Caption)
                    textColor = textColorHint
                    gravity = Gravity.CENTER_VERTICAL

                    lines = 1
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    includeFontPadding = false
                }.lparams {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        marginStart = dip(72)
                    } else {
                        leftMargin = dip(72)
                    }
                    below(R.id.teacher)
                    bottomMargin = tilePadding
                }
            }
        }
    }
}