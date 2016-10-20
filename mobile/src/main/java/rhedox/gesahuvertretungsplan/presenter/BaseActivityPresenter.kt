package rhedox.gesahuvertretungsplan.presenter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.mvp.BaseActivityContract

/**
 * Created by robin on 20.10.2016.
 */
class BaseActivityPresenter() : Fragment(), BaseActivityContract.Presenter {

    companion object {
        const val tag = "BaseActivityPresenter";
    }
    private lateinit var gesahu: GesaHuApi
    private var view: BaseActivityContract.View? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true;
        gesahu = GesaHuApi.create(context)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if(context is BaseActivityContract.View)
            view = context
    }

    override fun onDetach() {
        super.onDetach()

        view = null;
    }
}