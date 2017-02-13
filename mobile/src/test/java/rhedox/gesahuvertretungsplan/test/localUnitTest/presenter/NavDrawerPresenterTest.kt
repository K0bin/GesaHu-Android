package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.accounts.AccountManager
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.SubstituteFormatter
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.util.PermissionManager

/**
 * Created by robin on 26.01.2017.
 */
class NavDrawerPresenterTest {
    val kodein = Kodein {
        bind<SharedPreferences>() with instance ( mock<SharedPreferences> {} )
        bind<BoardsRepository>() with provider { mock<BoardsRepository> {} }
        bind<SubstitutesRepository>() with provider { mock<SubstitutesRepository> {} }
        bind<SyncObserver>() with provider { mock<SyncObserver> {} }
        bind<AccountManager>() with instance ( mock<AccountManager> {} )
        bind<AvatarLoader>() with provider { mock<AvatarLoader> {} }
        bind<PermissionManager>() with provider { mock<PermissionManager> {} }
        bind<ConnectivityManager>() with provider { mock<ConnectivityManager> {} }
    }

    private fun simulateOrientationChange(presenter: NavDrawerPresenter): StubNavDrawerView {
        presenter.detachView()
        val view = StubNavDrawerView()
        presenter.attachView(view)
        return view
    }

    @Test
    fun testBoards() {
        val presenter = NavDrawerPresenter(kodein)
        var view = StubNavDrawerView()
        presenter.attachView(view)
        presenter.onBoardsLoaded(listOf(Board("Englisch", "15", "irgendwas", 2, 2, 28)))
        assert(view.boards[0].name == "Englisch" && view.boards[0].mark == "15")
        view = simulateOrientationChange(presenter)
        assert(view.boards[0].name == "Englisch" && view.boards[0].mark == "15")
        presenter.detachView()
    }

    @Test
    fun testIntro() {
        val presenter = NavDrawerPresenter(kodein)
        var view = StubNavDrawerView()
        presenter.attachView(view)
        assert(view.currentView == StubNavDrawerView.ViewValues.intro)
        view = simulateOrientationChange(presenter)
        assert(view.currentView == StubNavDrawerView.ViewValues.intro)
        presenter.detachView()
    }

    @Test
    fun testAuth() {
        val kodein = Kodein {
            extend(this@NavDrawerPresenterTest.kodein)
            bind<SharedPreferences>(overrides = true) with instance ( mock<SharedPreferences> {
                on { getBoolean("pref_previously_started", false) } doReturn true
            } )
        }
        val presenter = NavDrawerPresenter(kodein)
        var view = StubNavDrawerView()
        presenter.attachView(view)
        assert(view.currentView == StubNavDrawerView.ViewValues.auth)
        view = simulateOrientationChange(presenter)
        assert(view.currentView == StubNavDrawerView.ViewValues.auth)
    }

    @Test
    fun testBoard() {
        val presenter = NavDrawerPresenter(kodein)
        val view = StubNavDrawerView()
        presenter.onBoardsLoaded(listOf(Board("Englisch", "15", "irgendwas", 2, 2, 28, 1)))
        presenter.attachView(view)
        presenter.onNavigationDrawerItemClicked(14)
        assert(view.currentView == StubNavDrawerView.ViewValues.board)
    }
}