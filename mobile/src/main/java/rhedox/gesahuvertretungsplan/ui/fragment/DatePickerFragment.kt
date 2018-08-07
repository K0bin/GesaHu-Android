package rhedox.gesahuvertretungsplan.ui.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import org.joda.time.DateTime
import org.joda.time.LocalDate

/**
 * Created by robin on 20.10.2016.
 */
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    private lateinit var date: LocalDate;
    private var isPickerDone = false

    var callback: ((date: LocalDate) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (savedInstanceState != null && savedInstanceState.containsKey(keyDate))
            date = DateTime(savedInstanceState.getLong(keyDate)).toLocalDate()
        else if (arguments != null && arguments!!.containsKey(argumentDate))
            date = DateTime(arguments!!.getLong(argumentDate)).toLocalDate()
        else
            date = LocalDate.now()

        isPickerDone = false
        return DatePickerDialog(requireActivity(), this, date.year, date.monthOfYear - 1, date.dayOfMonth)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(keyDate, date.toDateTimeAtStartOfDay().millis)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        if (!isPickerDone) {
            this.callback?.invoke(LocalDate(year, month+1, dayOfMonth))

            isPickerDone = true
        }

        dismiss()
    }

    companion object {
        val tag = "DatePickerFragment1"
        val keyDate = "date"
        val argumentDate = "ArgumentDate"

        @JvmStatic
        fun newInstance(date: LocalDate): DatePickerFragment {
            val bundle = Bundle()
            bundle.putLong(argumentDate, date.toDateTimeAtCurrentTime().millis)

            val fragment = DatePickerFragment()
            fragment.arguments = bundle

            return fragment
        }
    }
}