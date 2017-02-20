package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.android.appKodein
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.model.database.Mark
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.MarksPresenter
import rhedox.gesahuvertretungsplan.presenter.state.MarksState
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import rhedox.gesahuvertretungsplan.ui.adapter.LessonsAdapter
import rhedox.gesahuvertretungsplan.ui.adapter.MarksAdapter

/**
 * Created by robin on 19.01.2017.
 */
class MarksFragment : Fragment(), MarksContract.View {
    lateinit var presenter: MarksContract.Presenter;

    private lateinit var layoutManager: LinearLayoutManager;
    private lateinit var adapter: MarksAdapter;

    object Extra {
        const val boardId = "boardId"
    }

    companion object {
        const val stateBundleName = "state"
        const val layoutManagerBundleName = "layoutManager"

        @JvmStatic
        fun newInstance(boardId: Long): MarksFragment {
            val args = Bundle()
            args.putLong(Extra.boardId, boardId)
            val fragment = MarksFragment()
            fragment.arguments = args
            return fragment;
        }
    }

    override var mark: String
        get() = adapter.mark
        set(value) { adapter.mark = value }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        val state: MarksContract.State;
        if (savedInstanceState != null) {
            state = savedInstanceState.getParcelable<MarksState>(stateBundleName)
        } else {
            val id = arguments?.getLong(Extra.boardId, -1) ?: -1
            state = MarksState(id)
        }
        presenter = MarksPresenter(appKodein(), state)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lessons, container, false)
        val recycler = view.findViewById(R.id.recycler) as RecyclerView
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        if (savedInstanceState != null) {
            layoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(LessonsFragment.layoutManagerBundleName))
        }
        recycler.layoutManager = layoutManager
        recycler.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        adapter = MarksAdapter(context);
        recycler.adapter = adapter;

        val cardHeight = context.resources.getDimension(R.dimen.topCardHeight);
        recycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalScroll = recycler.computeVerticalScrollOffset();
                (parentFragment as? AppBarFragment)?.hasAppBarElevation = totalScroll >= cardHeight
            }
        })

        presenter.attachView(this)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.detachView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelable(stateBundleName, presenter.saveState() as MarksState)
        outState.putParcelable(layoutManagerBundleName, layoutManager.onSaveInstanceState())
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun showList(list: List<Mark>) {
        adapter.list = list;
    }
}