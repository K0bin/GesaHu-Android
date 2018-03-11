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
import org.joda.time.DateTimeConstants
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.dependencyInjection.SubstitutesComponent
import rhedox.gesahuvertretungsplan.model.*
import rhedox.gesahuvertretungsplan.model.database.StubSubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import java.util.*
import javax.inject.Inject

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPresenter(component: SubstitutesComponent, state: SubstitutesState?) : SubstitutesContract.Presenter {
    private val date: LocalDate;
    private var view: SubstitutesContract.View? = null
    /**
     * The selected substitute (of the current page); null for none
     */
    private var selected: Int? = null
    @Inject internal lateinit var syncObserver: SyncObserver
    /**
     * The page (day of week) which is currently visible to the user
     */
    private var currentPage: Int;
    @Inject internal lateinit var repository: SubstitutesRepository

    @Inject internal lateinit var connectivityManager: ConnectivityManager
    @Inject internal lateinit var formatter: SubstituteFormatter

    @Inject internal lateinit var accountManager: AccountManager

    private var account: Account? = null
        get() {
            if (field == null) {
                field = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType)?.firstOrNull()
            }
            return field;
        }

    private val substitutes = arrayOfNulls<LiveData<List<Substitute>>>(5)
    private val announcements = arrayOfNulls<LiveData<Announcement>>(5)
    private val substitutesObserver = Observer<List<Substitute>> { it ->
        if (it?.isNotEmpty() != true) return@Observer

        onSubstitutesLoaded(it.first().date, it)
    }
    private val announcementObserver = Observer<Announcement> {
        if (it?.text.isNullOrEmpty()) return@Observer

        onAnnouncementLoaded(it!!.date, it.text)
    }

    init {
        component.inject(this)

        val _date: LocalDate = state?.date ?: SchoolWeek.nextFromNow()

        Log.d("SubstitutesPresenter", "Date: $_date")

        currentPage = Math.max(0, Math.min(_date.dayOfWeek - DateTimeConstants.MONDAY, 4))
        this.date = getFirstDayOfWeek(_date)

        for(i in 0 until 5) {
            substitutes[i] = repository.loadSubstitutesForDay(this.date.withFieldAdded(DurationFieldType.days(), i))
            substitutes[i]!!.observeForever(substitutesObserver)
            announcements[i] = repository.loadAnnouncementForDay(this.date.withFieldAdded(DurationFieldType.days(), i))
            announcements[i]!!.observeForever(announcementObserver)
        }

        syncObserver.callback = {
            if (account != null) {
                (view as? Fragment)?.activity?.runOnUiThread {
                    view?.isRefreshing = ContentResolver.isSyncActive(account, StubSubstitutesContentProvider.authority)
                }
            }
        }

        selected = state?.selected
    }

    override fun attachView(view: SubstitutesContract.View) {
        Log.d("SubstitutesPresenter", "attachView")

        this.view = view

        view.currentTab = currentPage
        view.isFabVisible = selected == null && !announcements[currentPage]?.value?.text.isNullOrEmpty()
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

        Log.d("SubstitutesPresenter", "viewattached selected $selected")
    }

    override fun detachView() {
        this.view = null;
        Log.d("SubstitutesPresenter", "view detached")
    }

    override fun destroy() {
        syncObserver.destroy()
        substitutes.forEach { it?.removeObserver(substitutesObserver) }
        announcements.forEach { it?.removeObserver(announcementObserver) }
    }

    fun onSubstitutesLoaded(date: LocalDate, substitutes: List<Substitute>) {
        Log.d("SubstitutePresenter", "SubstitutesContract loaded: $date, ${substitutes.size} items")
        if(date.dayOfWeekIndex > 4) {
            return;
        }

        if (date.weekOfWeekyear == this.date.weekOfWeekyear) {
            val position = date.dayOfWeekIndex
            view?.showList(position, substitutes)
        }

        if(account != null)
            view?.isRefreshing = ContentResolver.isSyncActive(account, StubSubstitutesContentProvider.authority)
    }

    fun onAnnouncementLoaded(date: LocalDate, text: String) {
        if(date.dayOfWeekIndex > 4) {
            return;
        }

        if (date.weekOfWeekyear == this.date.weekOfWeekyear) {
            val position = date.dayOfWeekIndex
            view?.isFabVisible = selected == null && !announcements[currentPage]!!.value?.text.isNullOrEmpty()
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
            view?.openSubstitutesForDate(date)
        } else {
            //Same week => just switch to day-tab
            val dayIndex = Math.max(0, Math.min(date.dayOfWeekIndex, 5))
            view?.currentTab = dayIndex
        }
    }
    override fun onFabClicked() {
        view?.showDialog(announcements[currentPage]?.value?.text ?: "")
    }

    override fun onListItemClicked(listEntry: Int) {
        if (currentPage < 0 || listEntry < 0 || listEntry >= substitutes[currentPage]?.value?.size ?: 0) {
            return;
        }

        selected = if(selected == listEntry) null else listEntry
        if(view != null) {
            view!!.setSelected(currentPage, selected)
            view!!.isCabVisible = selected != null
            if(selected != null)
                view!!.isAppBarExpanded = true;

            view!!.isFabVisible = selected == null && !announcements[currentPage]?.value?.text.isNullOrEmpty();
        }
    }

    override fun onRefresh() {
        if (account != null) {
            val singleDay = Build.VERSION.SDK_INT < Build.VERSION_CODES.N || connectivityManager.isActiveNetworkMetered && connectivityManager.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED;

            StubSubstitutesContentProvider.requestUpdate(account!!, date.withFieldAdded(DurationFieldType.days(), currentPage), singleDay)
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
            System.out.println("selected: $selected, currentPage: $currentPage, announcement: ${announcements[currentPage]?.value}")
            view!!.isFabVisible = selected == null && !announcements[currentPage]?.value?.text.isNullOrEmpty();
        }
    }

    override fun onPageAttached(position: Int) {
        Log.d("SubstitutesPresenter", "onPageAttached position: $position, selected: $selected, currentPage: $currentPage")

        if(view != null) {
            if (substitutes[position] != null)
                view!!.showList(position, substitutes[position]?.value ?: listOf())

            if (position == currentPage)
                view!!.setSelected(position, selected)
        }
    }

    override fun onCabClosed() {
        view!!.setSelected(currentPage, null)
        selected = null;
        view!!.isCabVisible = false
        view!!.isFabVisible = !announcements[currentPage]?.value?.text.isNullOrEmpty();
    }

    override fun onShareButtonClicked() {
        val currentDate = date.withFieldAdded(DurationFieldType.days(), currentPage)
        val substitutesOfDay = substitutes[currentPage]?.value

        if(substitutesOfDay != null && selected != null) {
            val substitute = substitutesOfDay[selected!!]
            view?.share(formatter.makeShareText(currentDate, substitute))
        }
    }

    override fun saveState(): SubstitutesState {
        return SubstitutesState(date, selected)
    }
}