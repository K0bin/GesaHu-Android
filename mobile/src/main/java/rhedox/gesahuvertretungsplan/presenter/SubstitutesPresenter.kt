package rhedox.gesahuvertretungsplan.presenter

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.github.salomonbrys.kodein.*
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.*
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPresenter(kodeIn: Kodein, state: SubstitutesContract.State?) : BasePresenter(kodeIn), SubstitutesContract.Presenter {
    private val date: LocalDate;
    private var view: SubstitutesContract.View? = null
    private var substitutes = kotlin.arrayOfNulls<List<Substitute>>(5)
    private var announcements = arrayOf("","","","","")
    /**
     * The selected substitute (of the current page); null for none
     */
    private var selected: Int? = null
    private val syncObserver: SyncObserver
    /**
     * The page (day of week) which is currently visible to the user
     */
    private var currentPage: Int;
    private val repository: SubstitutesRepository

    /**
     * Determines whether or not the view was started by the date picker and is sitting on top of another SubstitutesActivity
     */
    private val canGoUp: Boolean;

    init {
        val _date: LocalDate = state?.date ?: SchoolWeek.nextFromNow()

        Log.d("SubstitutesPresenter", "Date: $_date")

        currentPage = Math.max(0, Math.min(_date.dayOfWeek - DateTimeConstants.MONDAY, 4))
        this.date = getFirstDayOfWeek(_date)
        this.canGoUp = state?.canGoUp ?: false

        val repositoryProvider: () -> SubstitutesRepository = kodeIn.provider()
        repository = repositoryProvider.invoke()
        repository.substitutesCallback = { date: LocalDate, list: List<Substitute> -> onSubstitutesLoaded(date, list) }
        repository.announcementCallback = { date: LocalDate, text: String -> onAnnouncementLoaded(date, text) }

        for(i in 0..4) {
            repository.loadSubstitutesForDay(this.date.withFieldAdded(DurationFieldType.days(), i))
            repository.loadAnnouncementForDay(this.date.withFieldAdded(DurationFieldType.days(), i))
        }

        val syncObserverProvider: () -> SyncObserver = kodeIn.provider()
        syncObserver = syncObserverProvider.invoke()
        syncObserver.callback = {
            if (account != null) {
                if (view is Activity) {
                    (view as Activity).runOnUiThread {
                        view?.isRefreshing = ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority)
                    }
                }
            }
        }

        selected = state?.selected
    }

    override fun attachView(view: BaseContract.View, isRecreated: Boolean) {
        super.attachView(view, false)
        Log.d("SubstitutesPresenter", "attachView")

        this.view = view as SubstitutesContract.View

        view.currentTab = currentPage
        view.isFabVisible = selected == null && announcements[currentPage].isNotBlank()
        view.isCabVisible = selected != null
        if(selected != null)
            view.isAppBarExpanded = true

        view.tabTitles = arrayOf(
                date.withFieldAdded(DurationFieldType.days(), 0).toString("EEE. dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 1).toString("EEE. dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 2).toString("EEE. dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 3).toString("EEE. dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 4).toString("EEE. dd.MM.yy", Locale.GERMANY)
        )

        view.isBackButtonVisible = canGoUp
        view.isSwipeRefreshEnabled = account != null;
        view.currentDrawerId = R.id.substitutes;

        Log.d("SubstitutesPresenter", "viewattached selected $selected")
    }

    override fun detachView() {
        super.detachView()
        this.view = null;
        Log.d("SubstitutesPresenter", "view detached")
    }

    override fun destroy() {
        repository.destroy()
        syncObserver.destroy()
    }

    fun onSubstitutesLoaded(date:LocalDate, substitutes: List<Substitute>) {
        Log.d("SubstitutePresenter", "SubstitutesContract loaded: $date, ${substitutes.size} items")
        if(date.dayOfWeekIndex > 4) {
            return;
        }

        if (date.weekOfWeekyear == this.date.weekOfWeekyear) {
            val position = date.dayOfWeekIndex
            this.substitutes[position] = substitutes
            view?.showList(position, substitutes)
        }

        if(account != null)
            view?.isRefreshing = ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority)
    }

    fun onAnnouncementLoaded(date:LocalDate, text: String) {
        if(date.dayOfWeekIndex > 4) {
            return;
        }

        if (date.weekOfWeekyear == this.date.weekOfWeekyear) {
            val position = date.dayOfWeekIndex
            announcements[position] = text
            view?.isFabVisible = selected == null && announcements[currentPage].isNotBlank();
        }

        if(account != null)
            view?.isRefreshing = ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority)
    }

    override fun onDatePickerIconClicked() {
        view?.showDatePicker(date)
    }

    private fun getFirstDayOfWeek(date: LocalDate): LocalDate {
        val monday = date.minusDays(date.dayOfWeekIndex)
        return monday
    }

    override fun onDatePicked(date: LocalDate) {
        if (date.weekOfWeekyear != this.date.weekOfWeekyear) {
            //Launch a new activity with that week
            view?.openSubstitutesForDate(date)
        } else {
            //Same week => just switch to day-tab
            val dayIndex = Math.max(0, Math.min(date.dayOfWeekIndex, 5))
            view?.currentTab = dayIndex
        }
    }
    override fun onFabClicked() {
        view?.showDialog(announcements[currentPage])
    }

    override fun onListItemClicked(listEntry: Int) {
        if (currentPage < 0 || listEntry < 0 || listEntry >= substitutes[currentPage]?.size ?: 0) {
            return;
        }

        selected = if(selected == listEntry) null else listEntry
        if(view != null) {
            view!!.setSelected(currentPage, selected)
            view!!.isCabVisible = selected != null
            if(selected != null)
                view!!.isAppBarExpanded = true;

            view!!.isFabVisible = selected == null && announcements[currentPage].isNotBlank();
        }
    }

    override fun onRefresh() {
        if (account != null) {
            repository.requestUpdate(account!!, date.withFieldAdded(DurationFieldType.days(), currentPage))
        } else
            view?.isRefreshing = false
    }

    override fun onActivePageChanged(position: Int) {
        Log.d("SubstitutesPresenter", "onActivePageChanged position: $position, selected: $selected, currentPage: $currentPage")

        val previousPosition = currentPage
        currentPage = position
        if(previousPosition != position)
            selected = null

        if(view != null) {
            if(previousPosition != position) {
                view!!.isCabVisible = false
                view!!.setSelected(previousPosition, null)
            }
            view!!.setSelected(position, selected)
            view!!.isFabVisible = selected == null && announcements[currentPage].isNotBlank()
        }
    }

    override fun onPageAttached(position: Int) {
        Log.d("SubstitutesPresenter", "onPageAttached position: $position, selected: $selected, currentPage: $currentPage")

        if(view != null) {
            if (substitutes[position] != null)
                view!!.showList(position, substitutes[position]!!)

            if (position == currentPage)
                view!!.setSelected(position, selected)
        }
    }
    override fun onCabClosed() {
        view!!.setSelected(currentPage, null)
        selected = null;
        view!!.isCabVisible = false
        view!!.isFabVisible = announcements[currentPage].isNotBlank()
    }
    override fun onShareButtonClicked() {
        val currentDate = date.withFieldAdded(DurationFieldType.days(), currentPage)
        val substitutesOfDay = substitutes[currentPage]

        if(substitutesOfDay != null && selected != null) {
            val substitute = substitutesOfDay[selected!!]
            //view?.share(SubstituteFormatter.makeShareText(context, currentDate, substitute))
        }
    }

    override fun onBackPressed() {
        if(selected != null) {
            selected = null;
            view?.setSelected(currentPage, null)
            view?.isCabVisible = false;
        } else {
            view?.goBack()
        }
    }

    override fun saveState(): SubstitutesContract.State {
        return SubstitutesState(date, canGoUp, selected)
    }
}