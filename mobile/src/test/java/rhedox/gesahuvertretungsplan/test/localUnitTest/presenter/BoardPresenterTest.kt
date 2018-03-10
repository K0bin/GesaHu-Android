package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import org.junit.Test
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.state.BoardState

/**
 * Created by robin on 01.02.2017.
 */
class BoardPresenterTest {
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