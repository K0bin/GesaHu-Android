package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.Assert.assertEquals
import org.joda.time.LocalDate
import org.junit.Rule
import org.junit.Test
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.presenter.MarksPresenter
import rhedox.gesahuvertretungsplan.presenter.state.MarksState
import rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection.TestAppComponent
import rhedox.gesahuvertretungsplan.test.localUnitTest.repository.BoardsTestRepository

/**
 * Created by robin on 01.02.2017.
 */
class MarksPresenterTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private fun simulateOrientationChange(presenter: MarksPresenter): StubMarksView {
        presenter.detachView()
        val view = StubMarksView()
        presenter.attachView(view)
        return view
    }

    @Test
    fun testMark() {
        val appComponent = TestAppComponent.create()
        val presenter = MarksPresenter(appComponent.boardComponent(), MarksState("BOARD"))
        val repository = presenter.repository as BoardsTestRepository
        var view = StubMarksView()
        presenter.attachView(view)
        repository.loadBoard("BOARD").value = Board("Board2", "15", "", 15, 2, 99)
        assertEquals("15", view.mark)
        view = simulateOrientationChange(presenter)
        assertEquals("15", view.mark)
    }

    @Test
    fun testList() {
        val appComponent = TestAppComponent.create()
        val presenter = MarksPresenter(appComponent.boardComponent(), MarksState("BOARD"))
        val repository = presenter.repository as BoardsTestRepository
        var view = StubMarksView()
        presenter.attachView(view)
        val date = LocalDate(2010, 10, 5)
        repository.loadMarks("BOARD").value = listOf(
                Mark(date, "mark1", "12", Mark.KindValues.test, null, Mark.MarkKindValues.groupMark, "", 0.1f, "BOARD"),
                Mark(date, "mark2", "13", Mark.KindValues.testOrComplexTask, null, Mark.MarkKindValues.mark, "", 0.9f, "BOARD")
        )
        assertEquals("12", view.list[0].mark)
        assertEquals("mark1", view.list[0].description)
        assertEquals(Mark.KindValues.test, view.list[0].kind)
        assertEquals(Mark.MarkKindValues.groupMark, view.list[0].markKind)
        assertEquals("13", view.list[1].mark)
        assertEquals("mark2", view.list[1].description)
        assertEquals(Mark.MarkKindValues.mark, view.list[1].markKind)
        view = simulateOrientationChange(presenter)
        assertEquals("12", view.list[0].mark)
        assertEquals("mark1", view.list[0].description)
        assertEquals(Mark.KindValues.test, view.list[0].kind)
        assertEquals(Mark.MarkKindValues.groupMark, view.list[0].markKind)
        assertEquals("13", view.list[1].mark)
        assertEquals("mark2", view.list[1].description)
        assertEquals(Mark.MarkKindValues.mark, view.list[1].markKind)
    }
}