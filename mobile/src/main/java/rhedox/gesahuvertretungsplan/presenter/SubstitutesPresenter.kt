package rhedox.gesahuvertretungsplan.presenter

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.SyncRequest
import android.os.Build
import android.os.Bundle
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
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPresenter(context: Context, date: LocalDate? = null, private val canGoBack: Boolean = false) : BasePresenter(context), SubstitutesContract.Presenter {
    private val date: LocalDate = getFirstDayOfWeek(date ?: SchoolWeek.nextFromNow())
    private val wasDateProvided = date != null;
    private var view: SubstitutesContract.View? = null
    private var substitutes = kotlin.arrayOfNulls<List<Substitute>>(5)
    private var announcements = arrayOf("","","","","")
    private var selected = arrayOf(-1, -1, -1, -1, -1)
    private var syncListenerHandle: Any;
    private var currentPosition: Int = 0;

    private var repository: SubstitutesRepository;

    init {
        currentPosition = (date ?: SchoolWeek.nextFromNow()).dayOfWeek - DateTimeConstants.MONDAY

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
    }

    override fun onViewAttached(view: BaseContract.View) {
        super.onViewAttached(view)

        this.view = view as SubstitutesContract.View
        view.currentTab = currentPosition
        view.isFabVisible = false

        view.tabTitles = arrayOf(
                date.withFieldAdded(DurationFieldType.days(), 0).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 1).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 2).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 3).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 4).toString("EEE dd.MM.yy", Locale.GERMANY)
        )

        if (wasDateProvided)
            view.isBackButtonVisible = canGoBack

        view.isSwipeRefreshEnabled = account != null;
        view.currentDrawerId = R.id.substitutes;
    }

    override fun onViewDetached() {
        super.onViewDetached()
        this.view = null;
    }

    //repository.destroy()
    //ContentResolver.removeStatusChangeListener(syncListenerHandle)

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
        view?.isFabVisible = selected[position] == -1 && announcements[currentPosition] != "";
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
        view?.showDialog(announcements[currentPosition])
    }

    override fun onListItemSelected(position: Int, listEntry: Int) {
        selected[position] = if(selected[position] == listEntry) -1 else listEntry
        if(view != null) {
            view!!.setSelected(position, selected[position])
            view!!.isCabVisible = selected[position] != -1
            if(selected[position] != -1)
                view!!.isAppBarExpanded = true;

            view!!.isFabVisible = selected[position] == -1 && announcements[currentPosition] != "";
        }
    }

    override fun onRefresh() {
        if (account != null) {
            if(!ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority) && !ContentResolver.isSyncPending(account, SubstitutesContentProvider.authority)) {
                for(i in 0..4) {
                    view?.setSelected(i, -1)
                }

                val extras = Bundle()
                extras.putInt(SubstitutesSyncService.SyncAdapter.extraDate, date.withFieldAdded(DurationFieldType.days(), currentPosition).unixTimeStamp)
                extras.putBoolean(SubstitutesSyncService.SyncAdapter.extraSingleDay, false)
                //extras.putLong(SyncAdapter.extraDate, date.toDateTime(LocalTime(0)).millis)
                extras.putBoolean(SubstitutesSyncService.SyncAdapter.extraIgnorePast, true)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    val syncRequest = SyncRequest.Builder()
                            .setSyncAdapter(account, SubstitutesContentProvider.authority)
                            .setExpedited(true)
                            .setManual(true)
                            .setDisallowMetered(false)
                            .setIgnoreSettings(true)
                            .setIgnoreBackoff(true)
                            .setNoRetry(true)
                            .setExtras(extras)
                            .syncOnce()
                            .build()
                    ContentResolver.requestSync(syncRequest)
                } else {
                    val bundle = Bundle()
                    bundle.putAll(extras)
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF, true)
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS, true)
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_REQUIRE_CHARGING, false)
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true)

                    ContentResolver.requestSync(account, SubstitutesContentProvider.authority, bundle)
                }
            }
        } else
            view?.isRefreshing = false
    }

    override fun onActiveTabChanged(position: Int) {
        val previousPosition = currentPosition
        currentPosition = position

        if(view != null) {
            view!!.isCabVisible = false
            view!!.setSelected(previousPosition, -1)
            view!!.isFabVisible = announcements[currentPosition] != ""
        }
    }

    override fun onTabCreated(position: Int) {
        Log.d("Presenter", "TabCreated: $position")

        view?.populateList(position, substitutes[position] ?: listOf())
    }
    override fun onCabClosed() {
        view!!.setSelected(currentPosition, -1)
        view!!.isCabVisible = false
    }
    override fun onShareButtonClicked() {
        val currentDate = date.withFieldAdded(DurationFieldType.days(), currentPosition)
        val selectedIndex = selected[currentPosition];
        val substitutesOfDay = substitutes[currentPosition]

        if(substitutesOfDay != null && selectedIndex != -1) {
            val substitute = substitutesOfDay[selectedIndex]
            view?.share(SubstituteFormatter.makeShareText(context, currentDate, substitute))
        }
    }

    /*fun onAboutClicked() {
        AboutLibs.start(context)
    }*/
}