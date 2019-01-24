package rhedox.gesahuvertretungsplan.presenter

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import rhedox.gesahuvertretungsplan.dependency_injection.BoardsComponent
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.mvp.LessonsContract
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState
import javax.inject.Inject

/**
 * Created by robin on 18.01.2017.
 */
class LessonsPresenter(component: BoardsComponent, state: LessonsState): LessonsContract.Presenter {
    private var view: LessonsContract.View? = null
    @Inject internal lateinit var repository: BoardsRepository
    private val boardName = state.boardName
    private val board: LiveData<Board>
    private var lessons: LiveData<List<Lesson>>

    private val boardObserver = Observer<Board?> {
        it ?: return@Observer
        onBoardLoaded(it)
    }
    private val lessonsObserver = Observer<List<Lesson>?> {
        if (it?.isNotEmpty() != true) return@Observer
        onLessonsLoaded(it)
    }

    init {
        component.inject(this)

        board = repository.loadBoard(boardName)
        lessons = repository.loadLessons(boardName)
        board.observeForever(boardObserver)
        lessons.observeForever(lessonsObserver)
    }

    private fun onLessonsLoaded(lessons: List<Lesson>) {
        view?.showList(lessons)
    }

    private fun onBoardLoaded(board: Board) {
        view?.lessonsMissed = board.missedLessons
        view?.lessonsMissedWithSickNote = board.missedLessonsWithSickNotes
        view?.lessonsTotal = board.lessonsTotal
    }

    override fun attachView(view: LessonsContract.View) {
        this.view = view

        view.lessonsMissed = board.value?.missedLessons ?: 0
        view.lessonsMissedWithSickNote = board.value?.missedLessonsWithSickNotes ?: 0
        view.lessonsTotal = board.value?.lessonsTotal ?: 0

        view.showList(lessons.value ?: listOf())
    }

    override fun detachView() {
        this.view = null
    }

    override fun destroy() {
        board.removeObserver(boardObserver)
        lessons.removeObserver(lessonsObserver)
    }

    override fun saveState(): LessonsState {
        return LessonsState(boardName)
    }

}