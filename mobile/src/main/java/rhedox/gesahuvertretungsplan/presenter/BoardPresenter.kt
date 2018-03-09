package rhedox.gesahuvertretungsplan.presenter

import android.arch.lifecycle.Observer
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.presenter.state.BoardState

/**
 * Created by robin on 18.01.2017.
 */
class BoardPresenter(kodein: Kodein, state: BoardState) : BoardContract.Presenter {
    private var view: BoardContract.View? = null
    val boardName = state.boardName;
    private val repository: BoardsRepository = kodein.instance()
    private val board = repository.loadBoard(boardName)

    private val observer = Observer<Board?> {
        it ?: return@Observer
        onBoardLoaded(it)
    }

    init {
        board.observeForever(observer)
    }

    private fun onBoardLoaded(board: Board) {
        view?.title = board.name;
    }

    override fun attachView(view: BoardContract.View) {
        this.view = view;
        this.view?.title = this.board.value?.name ?: "";
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
        board.removeObserver(observer)
    }

    override fun saveState(): BoardState {
        return BoardState(boardName)
    }
}