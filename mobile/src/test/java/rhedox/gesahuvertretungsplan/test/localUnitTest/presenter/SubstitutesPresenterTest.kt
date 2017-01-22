package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.nhaarman.mockito_kotlin.mock
import net.danlew.android.joda.JodaTimeAndroid
import org.jetbrains.anko.accountManager
import org.joda.time.LocalDate
import org.junit.Test
import org.junit.runner.RunWith
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepositoryOld
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.util.PermissionManager

/**
 * Created by robin on 22.12.2016.
 */
class SubstitutesPresenterTest {
    val kodein = Kodein {
        bind<SharedPreferences>() with instance ( mock<SharedPreferences> {} )
        bind<BoardsRepository>() with provider { mock<BoardsRepository> {} }
        bind<SubstitutesRepositoryOld>() with provider { mock<SubstitutesRepositoryOld> {} }
        bind<SyncObserver>() with provider { mock<SyncObserver> {} }
        bind<AccountManager>() with instance ( mock<AccountManager> {} )
        bind<AvatarLoader>() with provider { mock<AvatarLoader> {} }
        bind<PermissionManager>() with provider { mock<PermissionManager> {} }
        bind<ConnectivityManager>() with provider { mock<ConnectivityManager> {} }
    }

    var view = StubSubstitutesView()
    val date: LocalDate = LocalDate(2016,5,12)
    val presenter = SubstitutesPresenter(kodein, SubstitutesState(date, false))
    val list = listOf(Substitute(0,0, "", "", "", "", "", "", false), Substitute(0,0, "", "", "", "", "", "", false), Substitute(0,0, "", "", "", "", "", "", false))
    val announcement = "hi"

    private fun simulateOrientationChange() {
        presenter.detachView()
        view = StubSubstitutesView()
        presenter.attachView(view)
        presenter.onActivePageChanged(3)
        presenter.onPageAttached(1)
        presenter.onPageAttached(2)
        presenter.onPageAttached(3)
    }

    @Test
    fun testFab() {
        presenter.attachView(view)
        assert(!view.isFabVisible)
        simulateOrientationChange()
        assert(!view.isFabVisible)

        //Simulate loading empty announcement
        presenter.onAnnouncementLoaded(date, "")
        assert(!view.isFabVisible)
        simulateOrientationChange()
        assert(!view.isFabVisible)

        //Simulate loading announcement
        presenter.onAnnouncementLoaded(date, announcement)
        assert(view.isFabVisible)
        simulateOrientationChange()
        assert(view.isFabVisible)

        //Simulate selecting a substitute
        presenter.onSubstitutesLoaded(date, list)
        presenter.onListItemClicked(2)
        assert(!view.isFabVisible)
        simulateOrientationChange()
        assert(!view.isFabVisible)
        presenter.onListItemClicked(2)
        assert(view.isFabVisible)

        //Simulate swiping
        presenter.onActivePageChanged(1)
        assert(!view.isFabVisible)
        presenter.onActivePageChanged(3)
        assert(view.isFabVisible)
    }

    @Test
    fun testCab() {
        presenter.attachView(view)
        assert(!view.isCabVisible)
        simulateOrientationChange()
        assert(!view.isCabVisible)

        //Simulate selecting and deselecting
        presenter.onSubstitutesLoaded(date, list)
        presenter.onListItemClicked(2)
        assert(view.isCabVisible)
        simulateOrientationChange()
        assert(view.isCabVisible)
        presenter.onListItemClicked(2)
        assert(!view.isCabVisible)
        simulateOrientationChange()
        assert(!view.isCabVisible)

        //Simulate swiping
        presenter.onListItemClicked(2)
        assert(view.isCabVisible)
        presenter.onActivePageChanged(2)
        assert(!view.isCabVisible)
    }

    @Test
    fun testTabs() {
        presenter.attachView(view)
        assert(view.currentTab == 3)
        assert(view.tabTitles[0] == "Mo. 09.05.16")
    }

    @Test
    fun testList() {
        presenter.attachView(view)
        presenter.onSubstitutesLoaded(date, list)
        assert(view.listShown[3])
        simulateOrientationChange()
        assert(view.listShown[3])
    }

    @Test
    fun testSelection() {
        presenter.attachView(view)
        presenter.onSubstitutesLoaded(date, list)
        presenter.onListItemClicked(2)
        assert(view.selected[3] == 2)
        simulateOrientationChange()
        assert(view.selected[3] == 2)
        presenter.onActivePageChanged(2)
        assert(view.selected[3] == null)
    }

    @Test
    fun testStateRestoring() {
        val restoredPresenter = SubstitutesPresenter(kodein, SubstitutesState(date, true, 2))
        restoredPresenter.attachView(view)
        restoredPresenter.onActivePageChanged(3)
        restoredPresenter.onPageAttached(1)
        restoredPresenter.onPageAttached(2)
        restoredPresenter.onPageAttached(3)
        assert(view.isBackButtonVisible)
        assert(view.currentTab == 3)
        assert(view.tabTitles[0] == "Mo. 09.05.16")
        assert(view.selected[3] == 2)
        assert(!view.isFabVisible)
        assert(view.isCabVisible)
    }

    @Test
    fun testDrawer() {
        presenter.attachView(view)
        assert(view.currentDrawerId == R.id.substitutes)
    }
}
