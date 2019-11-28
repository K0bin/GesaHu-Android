package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.content.ContentResolver
import android.net.ConnectivityManager
import android.os.Build
import androidx.fragment.app.Fragment
import android.util.Log
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.dependency_injection.SubstitutesComponent
import rhedox.gesahuvertretungsplan.model.*
import rhedox.gesahuvertretungsplan.model.database.StubSubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision
import rhedox.gesahuvertretungsplan.mvp.SupervisionsContract
import rhedox.gesahuvertretungsplan.presenter.state.SupervisionsState
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import java.util.*
import javax.inject.Inject

/**
 * Created by robin on 20.10.2016.
 */
class SupervisionsPresenter(substitutesComponent: SubstitutesComponent, state: SupervisionsState) : SupervisionsContract.Presenter {
    private val date: LocalDate
    private var view: SupervisionsContract.View? = null
    /**
     * The selected substitute (of the current page); null for none
     */
    private var selected: Int? = null
    @Inject internal lateinit var syncObserver: SyncObserver
    /**
     * The page (day of week) which is currently visible to the user
     */
    private var currentPage: Int
    @Inject internal lateinit var repository: SubstitutesRepository

    @Inject internal lateinit var connectivityManager: ConnectivityManager
    @Inject internal lateinit var formatter: SubstituteFormatter

    @Inject internal lateinit var accountManager: AccountManager
    private var account: Account? = null
        get() {
            if (field == null) {
                field = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType).firstOrNull()
            }
            return field
        }

    private val supervisions = arrayOfNulls<LiveData<List<Supervision>>>(5)
    private val supervisionsObserver = Observer<List<Supervision>> {
        if (it?.isNotEmpty() != true) return@Observer

        onSupervisionsLoaded(it.first().date, it)
    }

    init {
        substitutesComponent.inject(this)

        val dayDate: LocalDate = state.date ?: SchoolWeek.nextFromNow()

        Log.d("SupervisionsPresenter", "Date: $dayDate")

        currentPage = Math.max(0, Math.min(dayDate.dayOfWeek - DateTimeConstants.MONDAY, 4))
        this.date = getFirstDayOfWeek(dayDate)

        for(i in 0 until 5) {
            supervisions[i] = repository.loadSupervisionsForDay(this.date.withFieldAdded(DurationFieldType.days(), i))
            supervisions[i]!!.observeForever(supervisionsObserver)
        }

        syncObserver.callback = {
            if (account != null) {
                (view as? Fragment)?.activity?.runOnUiThread {
                    view?.isRefreshing = ContentResolver.isSyncActive(account, StubSubstitutesContentProvider.authority)
                }
            }
        }

        selected = state.selected
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
        view.isSwipeRefreshEnabled = account != null

        Log.d("SupervisionsPresenter", "viewattached selected $selected")
    }

    override fun detachView() {
        this.view = null
        Log.d("SupervisionsPresenter", "view detached")
    }

    override fun destroy() {
        syncObserver.destroy()
        supervisions.forEach {
            it?.removeObserver(supervisionsObserver)
        }
    }

    private fun onSupervisionsLoaded(date:LocalDate, supervisions: List<Supervision>) {
        Log.d("SubstitutePresenter", "SupervisionContract loaded: $date, ${supervisions.size} items")
        if(date.dayOfWeekIndex > 4) {
            return
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
        return date.minusDays(date.dayOfWeekIndex)
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
            return
        }

        selected = if(selected == listEntry) null else listEntry
        if(view != null) {
            view!!.setSelected(currentPage, selected)
            view!!.isCabVisible = selected != null
            if(selected != null)
                view!!.isAppBarExpanded = true
        }
    }

    override fun onRefresh() {
        if (account != null) {
            val singleDay = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager.isActiveNetworkMetered && connectivityManager.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED

            StubSubstitutesContentProvider.requestUpdate(account!!, date.withFieldAdded(DurationFieldType.days(), currentPage), singleDay)
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
        selected = null
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