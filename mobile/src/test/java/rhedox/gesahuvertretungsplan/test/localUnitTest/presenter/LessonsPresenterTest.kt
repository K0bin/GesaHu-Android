package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.accounts.AccountManager
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.nhaarman.mockito_kotlin.mock
import org.joda.time.LocalDate
import org.junit.Test
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.presenter.LessonsPresenter
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState
import rhedox.gesahuvertretungsplan.util.PermissionManager

/**
 * Created by robin on 01.02.2017.
 */
class LessonsPresenterTest {
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

    private fun simulateOrientationChange(presenter: LessonsPresenter): StubLessonsView {
        presenter.detachView()
        val view = StubLessonsView()
        presenter.attachView(view)
        return view
    }

    @Test
    fun testLessons() {
        val presenter = LessonsPresenter(kodein, LessonsState(0))
        var view = StubLessonsView()
        presenter.attachView(view)
        presenter.onBoardLoaded(Board("", null, "", 15, 2, 99))
        assert(view.lessonsMissed == 15)
        assert(view.lessonsMissedWithSickNote == 2)
        assert(view.lessonsTotal == 99)
        view = simulateOrientationChange(presenter)
        assert(view.lessonsMissed == 15)
        assert(view.lessonsMissedWithSickNote == 2)
        assert(view.lessonsTotal == 99)
    }

    @Test
    fun testList() {
        val presenter = LessonsPresenter(kodein, LessonsState(0))
        var view = StubLessonsView()
        presenter.attachView(view)
        presenter.onLessonsLoaded(listOf(
                Lesson(LocalDate(), "Topic1", 2, Lesson.StatusValues.absent, null, null),
                Lesson(LocalDate(), "Topic2", 4, Lesson.StatusValues.present, null, null),
                Lesson(LocalDate(), "Topic3", 77, Lesson.StatusValues.absent, null, null)
        ))
        assert(view.list[0].topic == "Topic1")
        assert(view.list[1].status == Lesson.StatusValues.present)
        assert(view.list[2].status == Lesson.StatusValues.absent)
        assert(view.list[2].duration == 77)
        view = simulateOrientationChange(presenter)
        assert(view.list[0].topic == "Topic1")
        assert(view.list[1].status == Lesson.StatusValues.present)
        assert(view.list[2].status == Lesson.StatusValues.absent)
        assert(view.list[2].duration == 77)
    }
}