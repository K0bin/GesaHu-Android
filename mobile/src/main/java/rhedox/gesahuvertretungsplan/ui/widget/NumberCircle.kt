package rhedox.gesahuvertretungsplan.ui.widget

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.Switch
import org.jetbrains.anko.displayMetrics
import rhedox.gesahuvertretungsplan.R

/**
 * Created by robin on 23.01.2017.
 */
class NumberCircle: View {
    //no allocations in draw method
    private val rect: RectF = RectF()
    private val outlinePaint: Paint = Paint();
    private val ovalPaint: Paint = Paint();
    private val ovalTextPaint: Paint = Paint();
    private val circleTextPaint: Paint = Paint();
    private val ovalRect: RectF = RectF()
    private var textRect: Rect = Rect();

    var ovalColor: Int = Color.argb(1,1,0,0)
        get() = field
        set(value) {
            field = value

            ovalPaint.color = value
        }
    var ovalText: String = "";
    var ovalTextColor: Int = Color.argb(1,0,0,0)
        get() = field
        set(value) {
            field = value

            ovalTextPaint.color = value
        }

    var outlineColor: Int = Color.argb(1,1,0,0)
        get() = field
        set(value) {
            field = value

            outlinePaint.color = value
        }
    var outlineText: String = "";
    var outlineThickness: Float = 1 * context.displayMetrics.density;
        get() = field
        set(value) {
            field = value

            outlinePaint.strokeWidth = value
        }
    var outlineTextColor: Int = Color.argb(1,0,0,0)
        get() = field
        set(value) {
            field = value

            circleTextPaint.color = value
        }


    constructor(context: Context) : super(context) {

        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.isAntiAlias = true

        ovalPaint.style = Paint.Style.FILL
        ovalPaint.isAntiAlias = true

        ovalTextPaint.style = Paint.Style.FILL
        ovalTextPaint.isAntiAlias = true

        circleTextPaint.style = Paint.Style.FILL
        circleTextPaint.isAntiAlias = true
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumberCircle, 0, 0)

        outlinePaint.style = Paint.Style.STROKE
        outlinePaint.isAntiAlias = true

        ovalPaint.style = Paint.Style.FILL
        ovalPaint.isAntiAlias = true

        ovalTextPaint.style = Paint.Style.FILL
        ovalTextPaint.isAntiAlias = true

        circleTextPaint.style = Paint.Style.FILL
        circleTextPaint.isAntiAlias = true

        try {
            outlineColor = typedArray.getColor(R.styleable.NumberCircle_outlineColor, 0);
            outlineThickness = typedArray.getDimension(R.styleable.NumberCircle_outlineThickness, 1f);
            ovalColor = typedArray.getColor(R.styleable.NumberCircle_ovalColor, 0);
            ovalText = (typedArray.getString(R.styleable.NumberCircle_ovalText) ?: "").toUpperCase()
            ovalTextColor = typedArray.getColor(R.styleable.NumberCircle_ovalTextColor, 0)
            outlineTextColor = typedArray.getColor(R.styleable.NumberCircle_outlineTextColor, 0)
            outlineText = typedArray.getString(R.styleable.NumberCircle_outlineText) ?: ""
        } finally {
            typedArray.recycle()
        }
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        val size = Math.min(this.width, this.height).toFloat()
        canvas.drawCircle(this.width / 2f, this.height / 2f, (size - outlineThickness * 2) / 2f, outlinePaint)

        rect.left = this.width / 2 - size / 2
        rect.right = this.width / 2 + size / 2
        rect.top = this.height / 2 - size / 2
        rect.bottom = this.height / 2 + size / 2

        ovalRect.left = rect.left + size * 0.1f
        ovalRect.top = rect.top + size * 0.75f
        ovalRect.right = rect.right - size * 0.1f
        ovalRect.bottom = rect.bottom
        canvas.drawRoundRect(ovalRect, size * 0.125f, size * 0.125f, ovalPaint)

        circleTextPaint.textSize = size * 0.4f
        val circleTextWidth = circleTextPaint.measureText(outlineText)
        circleTextPaint.getTextBounds(outlineText, 0, outlineText.length, textRect)
        val circleTextHeight = textRect.height()
        canvas.drawText(outlineText, rect.centerX() - circleTextWidth / 2, rect.centerY() + circleTextHeight / 2, circleTextPaint)

        ovalTextPaint.textSize = ovalRect.height() * 0.6f
        var ovalTextWidth = ovalTextPaint.measureText(ovalText)
        if (ovalTextWidth > ovalRect.width()) {
            ovalTextPaint.textSize *= (ovalRect.width() / ovalTextWidth) * 0.8f
        }
        ovalTextWidth = ovalTextPaint.measureText(ovalText)
        ovalTextPaint.getTextBounds(ovalText, 0, ovalText.length, textRect)
        val ovalTextHeight = textRect.height()
        canvas.drawText(ovalText, ovalRect.centerX() - ovalTextWidth / 2, ovalRect.centerY() + ovalTextHeight / 2, ovalTextPaint)
    }
}