package rhedox.gesahuvertretungsplan.mvp

import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 20.10.2016.
 */
interface SubstitutesContract {
    interface Presenter : BaseContract.Presenter {
        fun onDatePickerIconClicked();
        fun onDatePicked(date: LocalDate)
        fun getSubstitutes(position: Int): List<Substitute>
        fun onActiveTabChanged(position: Int)
        fun onFabClicked()
        fun onTabCreated(position: Int)
        fun onListItemSelected(listEntry: Int)
        fun onRefresh()
        fun onSettingsClicked()
        fun onAboutClicked()
    }

    interface View : BaseContract.View {
        fun populateList(position: Int, list: List<Substitute>)
        fun showDatePicker(defaultDate: LocalDate)
        var currentTab: Int
        var isFloatingActionButtonVisible: Boolean
        var isBackButtonVisible: Boolean
        var isAppBarExpanded: Boolean
        var tabTitles: Array<String>
        var isRefreshing: Boolean
        fun showDialog(text: String)
    }
}