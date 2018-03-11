package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.Assert.assertEquals
import org.joda.time.LocalDate
import org.junit.Rule
import org.junit.Test
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.presenter.LessonsPresenter
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState
import rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection.TestAppComponent
import rhedox.gesahuvertretungsplan.test.localUnitTest.repository.BoardsTestRepository

/**
 * Created by robin on 01.02.2017.
 */
class LessonsPresenterTest {
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private fun simulateOrientationChange(presenter: LessonsPresenter): StubLessonsView {
        presenter.detachView()
        val view = StubLessonsView()
        presenter.attachView(view)
        return view
    }

    @Test
    fun testLessons() {
        val appComponent = TestAppComponent.create()
        val presenter = LessonsPresenter(appComponent.boardComponent(), LessonsState("BOARD"))
        val repository = presenter.repository as BoardsTestRepository
        var view = StubLessonsView()
        presenter.attachView(view)
        repository.loadBoard("BOARD").value = Board("BOARD2", null, "", 15, 2, 99)
        assertEquals(15, view.lessonsMissed)
        assertEquals(2, view.lessonsMissedWithSickNote)
        assertEquals(99, view.lessonsTotal)
        view = simulateOrientationChange(presenter)
        assertEquals(15, view.lessonsMissed)
        assertEquals(2, view.lessonsMissedWithSickNote)
        assertEquals(99, view.lessonsTotal)
    }

    @Test
    fun testList() {
        val appComponent = TestAppComponent.create()
        val presenter = LessonsPresenter(appComponent.boardComponent(), LessonsState("BOARD"))
        val repository = presenter.repository as BoardsTestRepository
        var view = StubLessonsView()
        presenter.attachView(view)
        val date = LocalDate(2015, 1, 1)
        repository.loadLessons("BOARD").value = listOf(
                Lesson(date, "Topic1", 2, Lesson.StatusValues.absent, null, null, "BOARD"),
                Lesson(date, "Topic2", 4, Lesson.StatusValues.present, null, null, "BOARD"),
                Lesson(date, "Topic3", 77, Lesson.StatusValues.absent, null, null, "BOARD")
        )
        assertEquals("Topic1", view.list[0].topic )
        assertEquals(Lesson.StatusValues.present, view.list[1].status)
        assertEquals(Lesson.StatusValues.absent, view.list[2].status)
        assertEquals(77, view.list[2].duration)
        view = simulateOrientationChange(presenter)
        assertEquals("Topic1", view.list[0].topic )
        assertEquals(Lesson.StatusValues.present, view.list[1].status)
        assertEquals(Lesson.StatusValues.absent, view.list[2].status)
        assertEquals(77, view.list[2].duration)
    }
}