package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.accounts.AccountManager
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.github.salomonbrys.kodein.provider
import com.nhaarman.mockito_kotlin.mock
import org.joda.time.LocalDate
import org.junit.Test
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.model.database.Mark
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.presenter.LessonsPresenter
import rhedox.gesahuvertretungsplan.presenter.MarksPresenter
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState
import rhedox.gesahuvertretungsplan.presenter.state.MarksState
import rhedox.gesahuvertretungsplan.util.PermissionManager

/**
 * Created by robin on 01.02.2017.
 */
class MarksPresenterTest {
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

    private fun simulateOrientationChange(presenter: MarksPresenter): StubMarksView {
        presenter.detachView()
        val view = StubMarksView()
        presenter.attachView(view)
        return view
    }

    @Test
    fun testMark() {
        val presenter = MarksPresenter(kodein, MarksState(0))
        var view = StubMarksView()
        presenter.attachView(view)
        presenter.onBoardLoaded(Board("", "15", "", 15, 2, 99))
        assert(view.mark == "15")
        view = simulateOrientationChange(presenter)
        assert(view.mark == "15")
    }

    @Test
    fun testList() {
        val presenter = MarksPresenter(kodein, MarksState(0))
        var view = StubMarksView()
        presenter.attachView(view)
        presenter.onMarksLoaded(listOf(
                Mark(LocalDate(),"mark1", "12", Mark.KindValues.test, null, Mark.MarkKindValues.groupMark, "", 0.1f),
                Mark(LocalDate(),"mark2", "13", Mark.KindValues.testOrComplexTask, null, Mark.MarkKindValues.mark, "", 0.9f)
        ))
        assert(view.list[0].mark == "12")
        assert(view.list[0].description == "mark1")
        assert(view.list[0].kind == Mark.KindValues.test)
        assert(view.list[0].markKind == Mark.MarkKindValues.groupMark)
        assert(view.list[1].mark == "13")
        assert(view.list[1].description == "mark2")
        assert(view.list[1].markKind == Mark.MarkKindValues.mark)
        view = simulateOrientationChange(presenter)
        assert(view.list[0].mark == "12")
        assert(view.list[0].description == "mark1")
        assert(view.list[0].kind == Mark.KindValues.test)
        assert(view.list[0].markKind == Mark.MarkKindValues.groupMark)
        assert(view.list[1].mark == "13")
        assert(view.list[1].description == "mark2")
        assert(view.list[1].markKind == Mark.MarkKindValues.mark)
    }
}