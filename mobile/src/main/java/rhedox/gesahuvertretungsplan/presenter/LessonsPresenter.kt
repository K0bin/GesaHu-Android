package rhedox.gesahuvertretungsplan.presenter

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.mvp.LessonsContract
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState

/**
 * Created by robin on 18.01.2017.
 */
class LessonsPresenter(kodein: Kodein, state: LessonsContract.State): LessonsContract.Presenter {
    private var view: LessonsContract.View? = null
    private val repository: BoardsRepository = kodein.instance();
    private val boardId = state.boardId;
    private var board: Board? = null
    private var lessons: List<Lesson> = listOf()

    init {
        repository.lessonsCallback = { boardId, lessons -> if (boardId == this.boardId) onLessonsLoaded(lessons) }
        repository.boardsCallback = { boards ->
            val board = boards.find { it.id == boardId }
            if (board != null) {
                onBoardLoaded(board)
            }
        }
        repository.loadLessons(boardId)
        repository.loadBoards()
    }

    fun onLessonsLoaded(lessons: List<Lesson>) {
        view?.showList(lessons)
        this.lessons = lessons;
    }

    fun onBoardLoaded(board: Board) {
        this.board = board;

        view?.lessonsMissed = board.missedLessons
        view?.lessonsMissedWithSickNote = board.missedLessonsWithSickNotes
        view?.lessonsTotal = board.lessonsTotal
    }

    override fun attachView(view: LessonsContract.View) {
        this.view = view;

        view.lessonsMissed = board?.missedLessons ?: 0
        view.lessonsMissedWithSickNote = board?.missedLessonsWithSickNotes ?: 0
        view.lessonsTotal = board?.lessonsTotal ?: 0

        view.showList(lessons)
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
        repository.destroy()
    }

    override fun saveState(): LessonsContract.State {
        return LessonsState(boardId)
    }

}