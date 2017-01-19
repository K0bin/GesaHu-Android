package rhedox.gesahuvertretungsplan.presenter

import com.github.salomonbrys.kodein.Kodein
import rhedox.gesahuvertretungsplan.mvp.BoardContract

/**
 * Created by robin on 18.01.2017.
 */
class BoardPresenter(kodein: Kodein, state: BoardContract.State) : NavDrawerPresenter(kodein), BoardContract.Presenter {
    override fun saveState(): BoardContract.State {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}