package rhedox.gesahuvertretungsplan.presenter

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract

/**
 * Created by robin on 18.01.2017.
 */
class BoardPresenter(kodein: Kodein, state: BoardContract.State?) : NavDrawerPresenter(kodein), BoardContract.Presenter {
    private var view: BoardContract.View? = null
    val boardId = state?.boardId ?: 0L;
    private val repository: BoardsRepository = kodein.instance()

    init {
        repository.boardsCallback = { onBoardsLoaded(it) }
        repository.loadBoards()
    }

    fun onBoardsLoaded(boards: List<Board>) {
        val board = boards.find({ it.id == boardId })
        view?.title = board?.name ?: "";
    }

    override fun attachView(view: NavDrawerContract.View, isRecreated: Boolean) {
        super.attachView(view, isRecreated)

        this.view = view as BoardContract.View;
    }

    override fun detachView() {
        super.detachView()

        this.view = null;
    }

    override fun saveState(): BoardContract.State {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}