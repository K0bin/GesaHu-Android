package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import android.content.*
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.util.Log
import android.widget.Toast
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity
import com.pawegio.kandroid.startActivity;
import org.joda.time.*
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.*
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import rhedox.gesahuvertretungsplan.ui.activity.AboutLibs
import rhedox.gesahuvertretungsplan.ui.activity.PreferenceActivity
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPresenter : BasePresenter(), SubstitutesContract.Presenter {

    companion object {
        const val tag = "SubstitutesPresenter";
    }

    private var date: LocalDate = LocalDate();
    private var view: SubstitutesContract.View? = null
    private var substitutes = kotlin.arrayOfNulls<List<Substitute>>(5)
    private var announcements = arrayOf("","","","","")
    private var selected = arrayOf(-1, -1, -1, -1, -1)
    private lateinit var syncListenerHandle: Any;
    private var currentPosition: Int = 0;

    private lateinit var repository: SubstitutesRepository;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(SubstitutesActivity.EXTRA_DATE)) {
            date = localDateFromUnix(arguments.getInt(SubstitutesActivity.EXTRA_DATE))
        } else
            date = SchoolWeek.nextFromNow();

        currentPosition = date.dayOfWeek - DateTimeConstants.MONDAY
        date = getFirstDayOfWeek(date)

        repository = SubstitutesRepository(context)
        repository.substitutesCallback = { date: LocalDate, list: List<Substitute> -> onSubstitutesLoaded(date, list) }
        repository.announcementCallback = { date: LocalDate, text: String -> onAnnouncementLoaded(date, text) }

        for(i in 0..4) {
            repository.loadSubstitutesForDay(date.withFieldAdded(DurationFieldType.days(), i))
            repository.loadAnnouncementForDay(date.withFieldAdded(DurationFieldType.days(), i))
        }

        syncListenerHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE or ContentResolver.SYNC_OBSERVER_TYPE_PENDING or ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, {
            Log.d("SyncObserver", "Observed change in $it");
            if (account != null) {
                activity.runOnUiThread {
                    view?.isRefreshing = ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority)
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(view != null) {
            view!!.currentTab = currentPosition
            view!!.isFloatingActionButtonVisible = false

            view!!.tabTitles = arrayOf(
                    date.withFieldAdded(DurationFieldType.days(), 0).toString("EEE dd.MM.yy", Locale.GERMANY),
                    date.withFieldAdded(DurationFieldType.days(), 1).toString("EEE dd.MM.yy", Locale.GERMANY),
                    date.withFieldAdded(DurationFieldType.days(), 2).toString("EEE dd.MM.yy", Locale.GERMANY),
                    date.withFieldAdded(DurationFieldType.days(), 3).toString("EEE dd.MM.yy", Locale.GERMANY),
                    date.withFieldAdded(DurationFieldType.days(), 4).toString("EEE dd.MM.yy", Locale.GERMANY)
            )

            if (arguments.containsKey(SubstitutesActivity.EXTRA_DATE))
                view!!.isBackButtonVisible = arguments.getBoolean(SubstitutesActivity.EXTRA_BACK, false)

            view!!.isSwipeRefreshEnabled = account != null;
            view!!.currentDrawerId = R.id.substitutes;
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        repository.destroy()
        ContentResolver.removeStatusChangeListener(syncListenerHandle)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if(context is SubstitutesContract.View)
            view = context
    }

    override fun onDetach() {
        super.onDetach()

        view = null;
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
        view?.isFloatingActionButtonVisible = text != ""
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
            val intent = Intent(context, SubstitutesActivity::class.java)
            intent.putExtra(SubstitutesActivity.EXTRA_DATE, date.unixTimeStamp)
            intent.putExtra(SubstitutesActivity.EXTRA_BACK, true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
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
            view!!.isCabVisible = selected[position] != listEntry
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
                extras.putBoolean(SubstitutesSyncService.SyncAdapter.extraSingleDay, true)
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
            view!!.isFloatingActionButtonVisible = announcements[currentPosition] != ""
        }
    }

    override fun onTabCreated(position: Int) {
        view?.populateList(position, substitutes[position] ?: listOf())
    }

    /*fun onAboutClicked() {
        AboutLibs.start(context)
    }*/
}