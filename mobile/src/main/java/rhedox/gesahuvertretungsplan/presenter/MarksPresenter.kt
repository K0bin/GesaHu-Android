package rhedox.gesahuvertretungsplan.presenter

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinInjected
import com.github.salomonbrys.kodein.KodeinInjector
import rhedox.gesahuvertretungsplan.mvp.MarksContract

/**
 * Created by robin on 18.01.2017.
 */
class MarksPresenter(kodein: Kodein, state: MarksContract.State): MarksContract.Presenter, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()
    private var view: MarksContract.View? = null

    init {
        inject(kodein)
    }

    override fun attachView(view: MarksContract.View, isRecreated: Boolean) {
        this.view = view;
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
    }

    override fun saveState(): MarksContract.State {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}