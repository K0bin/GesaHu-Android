package rhedox.gesahuvertretungsplan.presenter

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NotificationCompatSideChannelService
import android.util.Log
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.*
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPresenter(context: Context, date: LocalDate? = null, canGoUp: Boolean = false, state: Bundle? = null) : BasePresenter(context, state), SubstitutesContract.Presenter {
    private val date: LocalDate;
    private var view: SubstitutesContract.View? = null
    private var substitutes = kotlin.arrayOfNulls<List<Substitute>>(5)
    private var announcements = arrayOf("","","","","")
    /**
     * The selected substitute (of the current page); -1 for none
     */
    private var selected = -1
    private var syncListenerHandle: Any;
    /**
     * The page (day of week) which is currently visible to the user
     */
    private var currentPage: Int = 0;
    private var repository: SubstitutesRepository;

    /**
     * Determines whether or not the view was started by the date picker and is sitting on top of another SubstitutesActivity
     */
    private val canGoUp: Boolean;

    private object State {
        const val selected = "selected"
        const val date = "date"
        const val canGoUp = "canGoUp"
    }

    init {
        val _date: LocalDate;
        if(state != null && state.containsKey(State.date))
            _date = localDateFromUnix(state.getInt(State.date))
        else if (date != null)
            _date = date;
        else
            _date = SchoolWeek.nextFromNow()

        if(state != null && state.containsKey(State.canGoUp))
            this.canGoUp = state.getBoolean(State.canGoUp, false)
        else
            this.canGoUp = canGoUp;

        currentPage = _date.dayOfWeek - DateTimeConstants.MONDAY
        this.date = getFirstDayOfWeek(_date)

        repository = SubstitutesRepository(context)
        repository.substitutesCallback = { date: LocalDate, list: List<Substitute> -> onSubstitutesLoaded(date, list) }
        repository.announcementCallback = { date: LocalDate, text: String -> onAnnouncementLoaded(date, text) }

        for(i in 0..4) {
            repository.loadSubstitutesForDay(this.date.withFieldAdded(DurationFieldType.days(), i))
            repository.loadAnnouncementForDay(this.date.withFieldAdded(DurationFieldType.days(), i))
        }

        syncListenerHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE or ContentResolver.SYNC_OBSERVER_TYPE_PENDING or ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, {
            Log.d("SyncObserver", "Observed change in $it");
            if (account != null) {
                (view as Activity).runOnUiThread {
                    view?.isRefreshing = ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority)
                }
            }
        })

        selected = state?.getInt(State.selected) ?: -1;
    }

    override fun attachView(view: BaseContract.View) {
        super.attachView(view)

        this.view = view as SubstitutesContract.View
        view.currentTab = currentPage
        view.isFabVisible = false

        view.tabTitles = arrayOf(
                date.withFieldAdded(DurationFieldType.days(), 0).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 1).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 2).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 3).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 4).toString("EEE dd.MM.yy", Locale.GERMANY)
        )

        view.isBackButtonVisible = canGoUp

        view.isSwipeRefreshEnabled = account != null;
        view.currentDrawerId = R.id.substitutes;
        view.setSelected(currentPage, selected)
    }

    override fun detachView() {
        super.detachView()
        this.view = null;
    }

    override fun destroy() {
        repository.destroy()
        ContentResolver.removeStatusChangeListener(syncListenerHandle)
    }

    fun onSubstitutesLoaded(date:LocalDate, substitutes: List<Substitute>) {
        Log.d("SubstitutePresenter", "SubstitutesContract loaded: $date, ${substitutes.size} items")

        val position = date.dayOfWeekIndex
        this.substitutes[position] = substitutes
        view?.populateList(position, substitutes)
        if(account != null)
            view?.isRefreshing = ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority)
    }

    fun onAnnouncementLoaded(date:LocalDate, text: String) {
        val position = date.dayOfWeekIndex
        announcements[position] = text
        view?.isFabVisible = selected == -1 && announcements[currentPage] != "";
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
    override fun getSubstitutes(position: Int): List<Substitute> {
        return substitutes[position] ?: listOf()
    }
    override fun onFabClicked() {
        view?.showDialog(announcements[currentPage])
    }

    override fun onListItemSelected(position: Int, listEntry: Int) {
        selected = if(selected == listEntry) -1 else listEntry
        if(view != null) {
            view!!.setSelected(position, selected)
            view!!.isCabVisible = selected != -1
            if(selected != -1)
                view!!.isAppBarExpanded = true;

            view!!.isFabVisible = selected == -1 && announcements[currentPage] != "";
        }
    }

    override fun onRefresh() {
        if (account != null) {
            repository.requestUpdate(account!!, date.withFieldAdded(DurationFieldType.days(), currentPage))
        } else
            view?.isRefreshing = false
    }

    override fun onActiveTabChanged(position: Int) {
        val previousPosition = currentPage
        currentPage = position
        selected = -1

        if(view != null) {
            view!!.isCabVisible = false
            view!!.setSelected(previousPosition, -1)
            view!!.isFabVisible = announcements[currentPage] != ""
        }
    }

    override fun onTabCreated(position: Int) {
        Log.d("Presenter", "TabCreated: $position")

        view?.populateList(position, substitutes[position] ?: listOf())
    }
    override fun onCabClosed() {
        view!!.setSelected(currentPage, -1)
        view!!.isCabVisible = false
    }
    override fun onShareButtonClicked() {
        val currentDate = date.withFieldAdded(DurationFieldType.days(), currentPage)
        val substitutesOfDay = substitutes[currentPage]

        if(substitutesOfDay != null && selected != -1) {
            val substitute = substitutesOfDay[selected]
            view?.share(SubstituteFormatter.makeShareText(context, currentDate, substitute))
        }
    }

    override fun saveState(bundle: Bundle) {
        bundle.putInt(State.selected, selected)
        bundle.putInt(State.date, date.withFieldAdded(DurationFieldType.days(), currentPage).unixTimeStamp)
    }
}