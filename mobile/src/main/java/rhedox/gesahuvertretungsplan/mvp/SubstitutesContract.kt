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
        fun onActivePageChanged(position: Int)
        fun onFabClicked()
        fun onPageAttached(position: Int)
        fun onShareButtonClicked()
        fun onListItemClicked(position: Int, listEntry: Int)
        fun onRefresh()
        fun onCabClosed()
    }

    interface View : BaseContract.View {
        fun showList(position: Int, list: List<Substitute>)
        fun showDatePicker(defaultDate: LocalDate)
        var currentTab: Int
        var isFabVisible: Boolean
        var isBackButtonVisible: Boolean
        var isAppBarExpanded: Boolean
        var tabTitles: Array<String>
        var isRefreshing: Boolean
        var isSwipeRefreshEnabled: Boolean
        var isCabVisible: Boolean
        fun setSelected(position: Int, listPosition: Int?)
        fun showDialog(text: String)
        fun openSubstitutesForDate(date: LocalDate)
        fun share(text: String)
    }
}