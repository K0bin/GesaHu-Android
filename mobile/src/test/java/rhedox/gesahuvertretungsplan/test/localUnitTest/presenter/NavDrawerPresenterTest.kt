package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.doReturn
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.presenter.state.NavDrawerState
import rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection.TestAppComponent
import rhedox.gesahuvertretungsplan.test.localUnitTest.repository.BoardsTestRepository
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment

/**
 * Created by robin on 26.01.2017.
 */
class NavDrawerPresenterTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private fun simulateOrientationChange(presenter: NavDrawerPresenter): StubNavDrawerView {
        presenter.detachView()
        val view = StubNavDrawerView()
        presenter.attachView(view)
        return view
    }

    @Test
    fun testBoards() {
        val appComponent = TestAppComponent.create()
        val presenter = NavDrawerPresenter(appComponent.boardComponent(), NavDrawerState(null))
        val repository = presenter.boardsRepository as BoardsTestRepository
        var view = StubNavDrawerView()
        presenter.attachView(view)
        repository.loadBoards().value = listOf(Board("Englisch", "15", "irgendwas", 2, 2, 28))
        assertEquals("Englisch", view.boards[0].name)
        assertEquals("15", view.boards[0].mark)
        view = simulateOrientationChange(presenter)
        assertEquals("Englisch", view.boards[0].name)
        assertEquals("15", view.boards[0].mark)
        presenter.detachView()
    }

    @Test
    fun testIntro() {
        val appComponent = TestAppComponent.create()
        val presenter = NavDrawerPresenter(appComponent.boardComponent(), NavDrawerState(null))
        var view = StubNavDrawerView()
        presenter.attachView(view)
        assertEquals(StubNavDrawerView.ViewValues.intro, view.currentView)
        view = simulateOrientationChange(presenter)
        assertEquals(StubNavDrawerView.ViewValues.intro, view.currentView)
        presenter.detachView()
    }

    @Test
    fun testAuth() {
        val appComponent = TestAppComponent.create()
        val presenter = NavDrawerPresenter(appComponent.boardComponent(), NavDrawerState(null))

        val prefs = appComponent.prefs()
        doReturn(true).`when`(prefs).getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)

        assertTrue("Mocking didn't work.", prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false))

        var view = StubNavDrawerView()
        presenter.attachView(view)

        val started = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        System.out.println("STARTED: $started")

        assertEquals(StubNavDrawerView.ViewValues.auth, view.currentView)
        view = simulateOrientationChange(presenter)
        assertEquals(StubNavDrawerView.ViewValues.auth, view.currentView)
    }

    @Test
    fun testBoard() {
        val appComponent = TestAppComponent.create()
        val presenter = NavDrawerPresenter(appComponent.boardComponent(), NavDrawerState(null))
        val repository = presenter.boardsRepository as BoardsTestRepository
        val view = StubNavDrawerView()
        repository.loadBoards().value = listOf(Board("Englisch", "15", "irgendwas", 2, 2, 28))
        presenter.attachView(view)
        presenter.onNavigationDrawerItemClicked(13 + "English".hashCode())
        assertEquals(StubNavDrawerView.ViewValues.board, view.currentView)
    }
}