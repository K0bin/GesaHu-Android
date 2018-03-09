package rhedox.gesahuvertretungsplan.mvp

import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision
import rhedox.gesahuvertretungsplan.presenter.state.SupervisionsState

/**
 * Created by robin on 20.10.2016.
 */
interface SupervisionsContract {
    interface Presenter {
        fun onDatePickerIconClicked();
        fun onDatePicked(date: LocalDate)
        fun onActivePageChanged(position: Int)
        fun onPageAttached(position: Int)
        fun onShareButtonClicked()
        fun onListItemClicked(listEntry: Int)
        fun onRefresh()
        fun onCabClosed()
        fun attachView(view: View)
        fun detachView()
        fun destroy()
        fun saveState(): SupervisionsState
    }

    interface View {
        fun showList(position: Int, list: List<Supervision>)
        fun showDatePicker(defaultDate: LocalDate)
        var currentTab: Int
        var isAppBarExpanded: Boolean
        var tabTitles: Array<String>
        var isRefreshing: Boolean
        var isSwipeRefreshEnabled: Boolean
        var isCabVisible: Boolean
        fun setSelected(position: Int, listPosition: Int?)
        fun openSupervisionsForDay(date: LocalDate)
        fun share(text: String)
    }
}