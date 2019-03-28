package rhedox.gesahuvertretungsplan.ui.view_holder

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormatterBuilder
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.entity.Mark

/**
 * Created by robin on 30.01.2017.
 */
class MarkViewHolder(view: View): ModelViewHolder<Mark>(view) {
    val kind: TextView = view.findViewById(R.id.kind)
    val date: TextView = view.findViewById(R.id.date)
    val description: TextView = view.findViewById(R.id.description)
    val mark: TextView = view.findViewById(R.id.mark)
    val average: TextView = view.findViewById(R.id.average)

    val formatter: DateTimeFormatter = DateTimeFormatterBuilder().appendDayOfMonth(2).appendLiteral('.').appendMonthOfYear(2).appendLiteral('.').appendYear(4,4).toFormatter()

    private val markKindTest: String = view.context.getString(R.string.mark_kind_test)
    private val markKindMonthlyOral: String = view.context.getString(R.string.mark_kind_monthlyOral)
    private val markKindTestOrComplexTask: String = view.context.getString(R.string.mark_kind_testOrComplexTask)
    private val markKindOral: String = view.context.getString(R.string.mark_kind_oral)

    @SuppressLint("SetTextI18n")
    override fun bind(model: Mark) {
        description.text = model.description
        average.text = "Ø ${model.average}"
        mark.text = model.mark.toString()
        date.text = model.date.toString(formatter)
        kind.text = when (model.kind) {
            Mark.KindValues.test -> markKindTest
            Mark.KindValues.testOrComplexTask -> markKindTestOrComplexTask
            Mark.KindValues.monthlyOral -> markKindMonthlyOral
            Mark.KindValues.oral-> markKindOral
            else -> ""
        }
    }
}