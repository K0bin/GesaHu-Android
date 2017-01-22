package rhedox.gesahuvertretungsplan.ui.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.TabLayout
import com.github.salomonbrys.kodein.android.appKodein
import kotlinx.android.synthetic.main.activity_main.*
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.mvp.BoardContract
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.presenter.state.BoardState
import rhedox.gesahuvertretungsplan.presenter.state.SubstitutesState
import rhedox.gesahuvertretungsplan.ui.adapters.BoardPagerAdapter
import rhedox.gesahuvertretungsplan.util.localDateFromUnix

/**
 * Created by robin on 18.01.2017.
 */
class BoardActivity : NavDrawerActivity(), BoardContract.View {
    override lateinit var presenter: NavDrawerContract.Presenter
    private var isRecreated: Boolean = false

    object Extra {
        const val boardId = "boardId"
    }

    companion object {
        const val stateBundleName = "state"
    }

    override var title: String = ""
        get() = field
        set(value) {
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create presenter
        if(lastCustomNonConfigurationInstance != null) {
            presenter = lastCustomNonConfigurationInstance as BoardContract.Presenter
            isRecreated = true
        } else {
            val state: BoardState?;
            if(savedInstanceState != null) {
                state = savedInstanceState.getParcelable<BoardState>(BoardActivity.stateBundleName)
                isRecreated = true
            } else {
                val boardId = intent?.extras?.getLong(Extra.boardId, 0) ?: 0
                state = BoardState(boardId)
            }

            presenter = BoardPresenter(appKodein(), state)
        }

        setContentView(R.layout.activity_board)
        val boardId = intent?.extras?.getLong(Extra.boardId) ?: 0L;

        viewPager.adapter = BoardPagerAdapter(boardId, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.tabMode = TabLayout.MODE_FIXED
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE)
    }
}