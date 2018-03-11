package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.state.BoardState
import rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection.TestAppComponent
import rhedox.gesahuvertretungsplan.test.localUnitTest.repository.BoardsTestRepository

/**
 * Created by robin on 01.02.2017.
 */
class BoardPresenterTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private fun simulateOrientationChange(presenter: BoardPresenter): StubBoardView {
        presenter.detachView()
        val view = StubBoardView()
        presenter.attachView(view)
        return view
    }

    @Test
    fun testTitle() {
        val appComponent = TestAppComponent.create()
        val presenter = BoardPresenter(appComponent.boardComponent(), BoardState("BOARD"));
        val repository = presenter.repository as BoardsTestRepository
        var view = StubBoardView()
        presenter.attachView(view)
        repository.loadBoard("BOARD").value = Board("Mathe LK", "15", "", 15, 2, 99)
        assertEquals("Mathe LK", view.title)
        view = simulateOrientationChange(presenter)
        assertEquals("Mathe LK", view.title)
    }
}