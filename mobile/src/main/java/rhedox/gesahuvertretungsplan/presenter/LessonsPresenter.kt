package rhedox.gesahuvertretungsplan.presenter

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.mvp.LessonsContract
import rhedox.gesahuvertretungsplan.mvp.MarksContract

/**
 * Created by robin on 18.01.2017.
 */
class LessonsPresenter(kodein: Kodein, state: LessonsContract.State): LessonsContract.Presenter {
    private var view: LessonsContract.View? = null
    private val repository: BoardsRepository = kodein.instance();
    private val boardId = state.boardId;

    init {
        repository.lessonsCallback = { boardId, lessons -> if (boardId == this.boardId) onLessonsLoaded(lessons) }
        repository.loadLessons(boardId)
    }

    fun onLessonsLoaded(lessons: List<Lesson>) {
        view?.showList(lessons)
    }

    override fun attachView(view: LessonsContract.View, isRecreated: Boolean) {

        this.view = view;
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
    }

    override fun saveState(): LessonsContract.State {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}