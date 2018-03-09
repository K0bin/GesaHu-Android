package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.ContentResolver
import android.net.ConnectivityManager
import android.os.Build
import android.support.v4.app.Fragment
import android.util.Log
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.*
import rhedox.gesahuvertretungsplan.model.database.StubSubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.mvp.SupervisionsContract
import rhedox.gesahuvertretungsplan.presenter.state.SupervisionsState
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SupervisionsPresenter(kodeIn: Kodein, state: SupervisionsState?) : SupervisionsContract.Presenter {
    private val date: LocalDate;
    private var view: SupervisionsContract.View? = null
    /**
     * The selected substitute (of the current page); null for none
     */
    private var selected: Int? = null
    private val syncObserver: SyncObserver = kodeIn.instance()
    /**
     * The page (day of week) which is currently visible to the user
     */
    private var currentPage: Int;
    private val repository: SubstitutesRepository = kodeIn.instance()

    private val connectivityManager: ConnectivityManager = kodeIn.instance()
    private val formatter: SubstituteFormatter = kodeIn.instance()

    private val accountManager: AccountManager = kodeIn.instance()
    private var account: Account? = null
        get() {
            if (field == null) {
                field = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType)?.firstOrNull()
            }
            return field;
        }

    private val supervisions = arrayOfNulls<LiveData<List<Supervision>>>(5)
    private val supervisionsObserver = Observer<List<Supervision>> {
        if (it?.isNotEmpty() != true) return@Observer

        onSupervisionsLoaded(it.first().date, it)
    }

    init {
        val _date: LocalDate = state?.date ?: SchoolWeek.nextFromNow()

        Log.d("SupervisionsPresenter", "Date: $_date")

        currentPage = Math.max(0, Math.min(_date.dayOfWeek - DateTimeConstants.MONDAY, 4))
        this.date = getFirstDayOfWeek(_date)

        for(i in 0 until 5) {
            supervisions[i] = repository.loadSupervisionsForDay(this.date.withFieldAdded(DurationFieldType.days(), i))
            supervisions[i]!!.observeForever(supervisionsObserver)
        }

        syncObserver.callback = {
            if (account != null) {
                (view as? Fragment)?.activity?.runOnUiThread {
                    //view?.isRefreshing = ContentResolver.isSyncActive(account, SubstitutesContentProvider.authority)
                }
            }
        }

        selected = state?.selected
    }

    override fun attachView(view: SupervisionsContract.View) {
        Log.d("SupervisionsPresenter", "attachView")

        this.view = view

        view.currentTab = currentPage
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
        view.isSwipeRefreshEnabled = account != null;

        Log.d("SupervisionsPresenter", "viewattached selected $selected")
    }

    override fun detachView() {
        this.view = null;
        Log.d("SupervisionsPresenter", "view detached")
    }

    override fun destroy() {
        repository.destroy()
        syncObserver.destroy()
        supervisions.forEach {
            it?.removeObserver(supervisionsObserver)
        }
    }

    fun onSupervisionsLoaded(date:LocalDate, supervisions: List<Supervision>) {
        Log.d("SubstitutePresenter", "SupervisionContract loaded: $date, ${supervisions.size} items")
        if(date.dayOfWeekIndex > 4) {
            return;
        }

        if (date.weekOfWeekyear == this.date.weekOfWeekyear) {
            val position = date.dayOfWeekIndex
            view?.showList(position, supervisions)
        }

        if(account != null)
            view?.isRefreshing = ContentResolver.isSyncActive(account, StubSubstitutesContentProvider.authority)
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
            view?.openSupervisionsForDay(date)
        } else {
            //Same week => just switch to day-tab
            val dayIndex = Math.max(0, Math.min(date.dayOfWeekIndex, 5))
            view?.currentTab = dayIndex
        }
    }

    override fun onListItemClicked(listEntry: Int) {
        if (currentPage < 0 || listEntry < 0 || listEntry >= supervisions[currentPage]?.value?.size ?: 0) {
            return;
        }

        selected = if(selected == listEntry) null else listEntry
        if(view != null) {
            view!!.setSelected(currentPage, selected)
            view!!.isCabVisible = selected != null
            if(selected != null)
                view!!.isAppBarExpanded = true;
        }
    }

    override fun onRefresh() {
        if (account != null) {
            val singleDay = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager.isActiveNetworkMetered && connectivityManager.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;

            repository.requestUpdate(account!!, date.withFieldAdded(DurationFieldType.days(), currentPage), singleDay)
        } else
            view?.isRefreshing = false
    }

    override fun onActivePageChanged(position: Int) {
        Log.d("SupervisionsPresenter", "onActivePageChanged position: $position, selected: $selected, currentPage: $currentPage")

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
        }
    }

    override fun onPageAttached(position: Int) {
        Log.d("SupervisionsPresenter", "onPageAttached position: $position, selected: $selected, currentPage: $currentPage")

        if(view != null) {
            if (supervisions[position] != null)
                view!!.showList(position, supervisions[position]?.value ?: listOf())

            if (position == currentPage)
                view!!.setSelected(position, selected)
        }
    }

    override fun onCabClosed() {
        view!!.setSelected(currentPage, null)
        selected = null;
        view!!.isCabVisible = false
    }

    override fun onShareButtonClicked() {
        val currentDate = date.withFieldAdded(DurationFieldType.days(), currentPage)
        val substitutesOfDay = supervisions[currentPage]?.value

        if(substitutesOfDay != null && selected != null) {
            val supervision = substitutesOfDay[selected!!]
            view?.share(formatter.makeShareText(currentDate, supervision))
        }
    }

    override fun saveState(): SupervisionsState {
        return SupervisionsState(date, selected)
    }
}