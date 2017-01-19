package rhedox.gesahuvertretungsplan.ui.activity

import android.os.Bundle
import android.os.Parcelable
import com.github.salomonbrys.kodein.android.appKodein
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.presenter.state.BoardState
import rhedox.gesahuvertretungsplan.util.localDateFromUnix

/**
 * Created by robin on 18.01.2017.
 */
class BoardActivity : NavDrawerActivity(), NavDrawerContract.View {

    override lateinit var presenter: NavDrawerContract.Presenter
    private var isRecreated: Boolean = false

    object Extra {
        const val boardId = "boardId"
    }

    companion object {
        const val stateBundleName = "state"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create presenter
        if(lastCustomNonConfigurationInstance != null) {
            presenter = lastCustomNonConfigurationInstance as BoardContract.Presenter
            isRecreated = true
        } else {
            presenter = NavDrawerPresenter(appKodein())
        }

        setContentView(R.layout.activity_board)
    }
}