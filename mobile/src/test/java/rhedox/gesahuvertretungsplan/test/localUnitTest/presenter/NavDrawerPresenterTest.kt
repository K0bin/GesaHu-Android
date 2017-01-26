package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.accounts.AccountManager
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.github.salomonbrys.kodein.Kodein
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

    val presenter = NavDrawerPresenter(kodein)
    var view = StubNavDrawerView()

    private fun simulateOrientationChange() {
        presenter.detachView()
        view = StubNavDrawerView()
        presenter.attachView(view)
    }

    @Test
    fun testBoards() {
        presenter.attachView(view)
        presenter.onBoardsLoaded(listOf(Board("Englisch", 15, "irgendwas", 2, 2, 28)))
        assert(view.boards[0].name == "Englisch" && view.boards[0].mark == 15)
        simulateOrientationChange()
        assert(view.boards[0].name == "Englisch" && view.boards[0].mark == 15)
    }

    @Test
    fun testIntro() {
        presenter.attachView(view)
        assert(view.currentView == StubNavDrawerView.ViewValues.intro)
        simulateOrientationChange()
        assert(view.currentView == StubNavDrawerView.ViewValues.intro)
    }

    @Test
    fun testAuth() {
        val kodein = Kodein {
            extend(this@NavDrawerPresenterTest.kodein)
            bind<SharedPreferences>(overrides = true) with instance ( mock<SharedPreferences> {
                on { getBoolean("pref_previously_started", false) } doReturn true
            } )
        }
        val authPresenter = NavDrawerPresenter(kodein)
        authPresenter.attachView(view)
        assert(view.currentView == StubNavDrawerView.ViewValues.auth)
        authPresenter.detachView()
        view = StubNavDrawerView()
        authPresenter.attachView(view)
        assert(view.currentView == StubNavDrawerView.ViewValues.auth)
    }
}