package rhedox.gesahuvertretungsplan.presenter

import android.arch.lifecycle.Observer
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.mvp.LessonsContract
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState

/**
 * Created by robin on 18.01.2017.
 */
class LessonsPresenter(kodein: Kodein, state: LessonsState): LessonsContract.Presenter {
    private var view: LessonsContract.View? = null
    private val repository: BoardsRepository = kodein.instance();
    private val boardName = state.boardName;
    private val board = repository.loadBoard(boardName)
    private var lessons = repository.loadLessons(boardName)

    private val boardObserver = Observer<Board?> {
        it ?: return@Observer
        onBoardLoaded(it)
    }
    private val lessonsObserver = Observer<List<Lesson>?> {
        if (it?.isNotEmpty() != true) return@Observer
        onLessonsLoaded(it)
    }

    init {
        board.observeForever(boardObserver)
        lessons.observeForever(lessonsObserver)
    }

    fun onLessonsLoaded(lessons: List<Lesson>) {
        view?.showList(lessons)
    }

    fun onBoardLoaded(board: Board) {
        view?.lessonsMissed = board.missedLessons
        view?.lessonsMissedWithSickNote = board.missedLessonsWithSickNotes
        view?.lessonsTotal = board.lessonsTotal
    }

    override fun attachView(view: LessonsContract.View) {
        this.view = view;

        view.lessonsMissed = board.value?.missedLessons ?: 0
        view.lessonsMissedWithSickNote = board.value?.missedLessonsWithSickNotes ?: 0
        view.lessonsTotal = board.value?.lessonsTotal ?: 0

        view.showList(lessons.value ?: listOf())
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
        board.removeObserver(boardObserver)
        lessons.removeObserver(lessonsObserver)
    }

    override fun saveState(): LessonsState {
        return LessonsState(boardName)
    }

}