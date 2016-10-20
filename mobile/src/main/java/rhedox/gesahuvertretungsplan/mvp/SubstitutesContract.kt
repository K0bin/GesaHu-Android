package rhedox.gesahuvertretungsplan.mvp

import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 20.10.2016.
 */
interface SubstitutesContract {
    interface Presenter {
        fun onDatePickerIconClicked();
        fun onDatePicked(date: LocalDate)
        fun getSubstitutes(position: Int): List<Substitute>
        fun getTabTitle(position: Int): String
        fun onFabClicked()
        fun onListItemSelected(listEntry: Int)
    }

    interface View {
        fun populateList(position: Int, list: List<Substitute>)
        fun showDatePicker(defaultDate: LocalDate)
        var currentTab: Int
        var isFloatingActionButtonVisible: Boolean
        var isBackButtonVisible: Boolean
        var isAppBarExpanded: Boolean
        fun showDialog(text: String)
    }
}