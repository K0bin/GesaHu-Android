package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.accounts.AccountManager
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.state.BoardState
import rhedox.gesahuvertretungsplan.util.PermissionManager

/**
 * Created by robin on 01.02.2017.
 */
class BoardPresenterTest {
    val kodein = Kodein {
        bind<SharedPreferences>() with instance(mock<SharedPreferences> {})
        bind<BoardsRepository>() with provider { mock<BoardsRepository> {} }
        bind<SubstitutesRepository>() with provider { mock<SubstitutesRepository> {} }
        bind<SyncObserver>() with provider { mock<SyncObserver> {} }
        bind<AccountManager>() with instance(mock<AccountManager> {})
        bind<AvatarLoader>() with provider { mock<AvatarLoader> {} }
        bind<PermissionManager>() with provider { mock<PermissionManager> {} }
        bind<ConnectivityManager>() with provider { mock<ConnectivityManager> {} }
    }

    private fun simulateOrientationChange(presenter: BoardPresenter): StubBoardView {
        presenter.detachView()
        val view = StubBoardView()
        presenter.attachView(view)
        return view
    }

    @Test
    fun testTitle() {
        val presenter = BoardPresenter(kodein, BoardState(0L));
        var view = StubBoardView()
        presenter.attachView(view)
        presenter.onBoardsLoaded(listOf(
                Board("Mathe LK", "15", "", 15, 2, 99, 0L),
                Board("Deutsch LK", "13", "", 78, 55, 66, 1L)
        ))
        assert(view.title == "Mathe LK")
        view = simulateOrientationChange(presenter)
        assert(view.title == "Mathe LK")
    }
}