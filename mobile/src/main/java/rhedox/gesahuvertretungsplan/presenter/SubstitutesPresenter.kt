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
import android.widget.Toast
import rhedox.gesahuvertretungsplan.model.database.SubstitutesLoaderHelper
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity
import com.pawegio.kandroid.startActivity;
import org.joda.time.*
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.*
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentObserver
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity1
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPresenter : BasePresenter(), SubstitutesContract.Presenter, SubstitutesLoaderHelper.Callback {
    companion object {
        const val tag = "SubstitutesPresenter";
    }

    private var date: LocalDate = LocalDate();
    private var view: SubstitutesContract.View? = null
    private lateinit var helpers: Array<SubstitutesLoaderHelper>
    private var substitutes = arrayOf<List<Substitute>?>(null, null, null, null, null)
    private var announcements = arrayOf("","","","","")
    private lateinit var observer: SubstitutesContentObserver;
    private lateinit var syncListenerHandle: Any;
    private var currentPosition: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(SubstitutesActivity.extraDate)) {
            date = DateTime(arguments.getLong(SubstitutesActivity.extraDate)).toLocalDate()

            view?.isBackButtonVisible = arguments.containsKey(SubstitutesActivity.extraBack) && arguments.getBoolean(SubstitutesActivity.extraBack)
        } else
            date = SchoolWeek.nextFromNow();

        currentPosition = date.dayOfWeek - DateTimeConstants.MONDAY
        date = getFirstDayOfWeek(date)

        helpers = arrayOf(
                SubstitutesLoaderHelper(loaderManager, context.applicationContext, date, this),
                SubstitutesLoaderHelper(loaderManager, context.applicationContext, date.withFieldAdded(DurationFieldType.days(), 1), this),
                SubstitutesLoaderHelper(loaderManager, context.applicationContext, date.withFieldAdded(DurationFieldType.days(), 2), this),
                SubstitutesLoaderHelper(loaderManager, context.applicationContext, date.withFieldAdded(DurationFieldType.days(), 3), this),
                SubstitutesLoaderHelper(loaderManager, context.applicationContext, date.withFieldAdded(DurationFieldType.days(), 4), this)
        );

        helpers.forEach { it.load() }

        observer = SubstitutesContentObserver(Handler(), {
            helpers[it.dayOfWeekIndex].load()
        })
        context.contentResolver.registerContentObserver(Uri.parse("content://${SubstitutesContentProvider.authority}/${SubstitutesContentProvider.substitutesPath}/date"), true, observer);
        context.contentResolver.registerContentObserver(Uri.parse("content://${SubstitutesContentProvider.authority}/${SubstitutesContentProvider.announcementsPath}/date"), true, observer);
        syncListenerHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE or ContentResolver.SYNC_OBSERVER_TYPE_PENDING or ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, {
            if (account != null) {
                activity.runOnUiThread {
                    for(i in 0..4)
                    view?.setIsRefreshing(i, ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority))
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.currentTab = currentPosition
        view?.isFloatingActionButtonVisible = false

        view?.tabTitles = arrayOf(
                date.withFieldAdded(DurationFieldType.days(), 0).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 1).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 2).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 3).toString("EEE dd.MM.yy", Locale.GERMANY),
                date.withFieldAdded(DurationFieldType.days(), 4).toString("EEE dd.MM.yy", Locale.GERMANY)
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        context.contentResolver.unregisterContentObserver(observer)
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

    override fun onSubstitutesLoaded(substitutesList: SubstitutesList) {
        val position = substitutesList.date.dayOfWeek - DateTimeConstants.MONDAY;
        substitutes[position] = substitutesList.substitutes
        announcements[position] = substitutesList.announcement
        view?.populateList(position, substitutesList.substitutes)
        view?.setIsRefreshing(position, false)
        view?.isFloatingActionButtonVisible = substitutesList.announcement != ""
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
            intent.putExtra(SubstitutesActivity.extraDate, date.toDateTime(LocalTime(0)).millis)
            intent.putExtra(SubstitutesActivity.extraBack, true)
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

    override fun onListItemSelected(listEntry: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRefresh(position: Int) {
        if (account != null) {
            if(!ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority) && !ContentResolver.isSyncPending(account, SubstitutesContentProvider.authority)) {
                val extras = Bundle()
                extras.putLong(SyncAdapter.extraDate, date.withFieldAdded(DurationFieldType.days(), position).toDateTime(LocalTime(0)).millis)

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
            view?.setIsRefreshing(currentPosition, false)
    }

    override fun onActiveTabChanged(position: Int) {
        currentPosition = position
        view?.isFloatingActionButtonVisible = announcements[currentPosition] != ""
    }

    override fun onTabCreated(position: Int) {
        view?.populateList(position, substitutes[position] ?: listOf())
    }
}