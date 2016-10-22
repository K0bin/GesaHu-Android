package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import android.content.ContentProvider
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import android.widget.Toast
import rhedox.gesahuvertretungsplan.model.SubstitutesList
import rhedox.gesahuvertretungsplan.model.database.SubstitutesLoaderHelper
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity
import com.pawegio.kandroid.startActivity;
import org.joda.time.*
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentObserver
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity1
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPresenter : Fragment(), SubstitutesContract.Presenter, SubstitutesLoaderHelper.Callback {
    companion object {
        const val tag = "SubstitutesPresenter";
    }

    private var date: LocalDate = LocalDate();
    private var view: SubstitutesContract.View? = null
    private lateinit var helpers: Array<SubstitutesLoaderHelper>
    private var substitutes = arrayOf<List<Substitute>?>(null, null, null, null, null)
    private lateinit var observer: SubstitutesContentObserver;
    private lateinit var syncListenerHandle: Any;
    private var account: Account? = null;
    private var currentPosition: Int = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(SubstitutesActivity.extraDate)) {
            date = DateTime(arguments.getLong(SubstitutesActivity.extraDate)).toLocalDate()

            view?.isBackButtonVisible = arguments.containsKey(SubstitutesActivity.extraBack) && arguments.getBoolean(SubstitutesActivity.extraBack)
        } else
            date = SchoolWeek.next();

        currentPosition = date.dayOfWeek - DateTimeConstants.MONDAY
        view?.currentTab = currentPosition
        date = getFirstDayOfWeek(date)

        helpers = arrayOf(
                SubstitutesLoaderHelper(loaderManager, context, date, this),
                SubstitutesLoaderHelper(loaderManager, context, date.withFieldAdded(DurationFieldType.days(), 1), this),
                SubstitutesLoaderHelper(loaderManager, context, date.withFieldAdded(DurationFieldType.days(), 2), this),
                SubstitutesLoaderHelper(loaderManager, context, date.withFieldAdded(DurationFieldType.days(), 3), this),
                SubstitutesLoaderHelper(loaderManager, context, date.withFieldAdded(DurationFieldType.days(), 4), this)
        );

        helpers.forEach { it.load() }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            val accountManager = AccountManager.get(context)

            val accounts = accountManager.getAccountsByType(App.ACCOUNT_TYPE)
            if (accounts.size > 0)
                account = accounts[0]
        }

        observer = SubstitutesContentObserver(Handler(), {
            val position = it.dayOfWeek - DateTimeConstants.MONDAY
            helpers[position].load()
        })
        context.contentResolver.registerContentObserver(Uri.parse("content://${SubstitutesContentProvider.authority}/${SubstitutesContentProvider.substitutesPath}/date"), true, observer);
        context.contentResolver.registerContentObserver(Uri.parse("content://${SubstitutesContentProvider.authority}/${SubstitutesContentProvider.announcementsPath}/date"), true, observer);
        syncListenerHandle = ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE or ContentResolver.SYNC_OBSERVER_TYPE_PENDING or ContentResolver.SYNC_OBSERVER_TYPE_SETTINGS, {
            if (account != null) {
                activity.runOnUiThread {
                    view?.setIsRefreshing(currentPosition, ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority))
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        context.contentResolver.unregisterContentObserver(observer)
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
        view?.populateList(position, substitutesList.substitutes)
    }

    override fun onDatePickerIconClicked() {
        view?.showDatePicker(date)
    }


    private fun getFirstDayOfWeek(date: LocalDate): LocalDate {
        val index = date.dayOfWeek - DateTimeConstants.MONDAY
        val monday = date.minusDays(index)
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
            val index = date.dayOfWeek - DateTimeConstants.MONDAY
            val dayIndex = Math.max(0, Math.min(index, 5))
            view?.currentTab = dayIndex
        }
    }
    override fun getSubstitutes(position: Int): List<Substitute> {
        return substitutes[position] ?: listOf()
    }
    override fun getTabTitle(position: Int): String {
        return date.withFieldAdded(DurationFieldType.days(), position + 1 - date.dayOfWeek).toString("EEE dd.MM.yy", Locale.GERMANY)
    }
    override fun onFabClicked() {
        //view?.showDialog()
    }

    override fun onListItemSelected(listEntry: Int) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRefresh() {
        view?.setIsRefreshing(currentPosition, false)

        if (account != null && !ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority) && !ContentResolver.isSyncPending(account, SubstitutesContentProvider.authority)) {
            val bundle = Bundle()
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true)
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true)
            ContentResolver.requestSync(account, App.ACCOUNT_TYPE, bundle)
        }
    }
    override fun onActiveTabChanged(position: Int) {
        currentPosition = position
    }
}