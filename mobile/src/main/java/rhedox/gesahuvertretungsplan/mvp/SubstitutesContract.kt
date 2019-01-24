package rhedox.gesahuvertretungsplan.mvp

import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState

/**
 * Created by robin on 20.10.2016.
 */
interface SubstitutesContract {
    interface Presenter {
        fun onDatePickerIconClicked()
        fun onDatePicked(date: LocalDate)
        fun onActivePageChanged(position: Int)
        fun onFabClicked()
        fun onPageAttached(position: Int)
        fun onShareButtonClicked()
        fun onListItemClicked(listEntry: Int)
        fun onRefresh()
        fun onCabClosed()
        fun attachView(view: View)
        fun detachView()
        fun destroy()
        fun saveState(): SubstitutesState
    }

    interface View {
        fun showList(position: Int, list: List<Substitute>)
        fun showDatePicker(defaultDate: LocalDate)
        var currentTab: Int
        var isFabVisible: Boolean
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