package rhedox.gesahuvertretungsplan.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.salomonbrys.kodein.android.appKodein
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.mvp.LessonsContract
import rhedox.gesahuvertretungsplan.mvp.MarksContract
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.LessonsPresenter
import rhedox.gesahuvertretungsplan.presenter.MarksPresenter
import rhedox.gesahuvertretungsplan.presenter.state.LessonsState
import rhedox.gesahuvertretungsplan.presenter.state.MarksState
import rhedox.gesahuvertretungsplan.ui.adapters.LessonsAdapter

/**
 * Created by robin on 19.01.2017.
 */
class LessonsFragment : Fragment(), LessonsContract.View {
    lateinit var presenter: LessonsContract.Presenter;

    private lateinit var layoutManager: LinearLayoutManager;
    private lateinit var adapter: LessonsAdapter;

    object Extra {
        const val boardId = "boardId"
    }

    companion object {
        @JvmStatic
        fun createInstance(boardId: Long): LessonsFragment {
            val args = Bundle()
            args.putLong(Extra.boardId, boardId)
            val fragment = LessonsFragment()
            fragment.arguments = args
            return fragment;
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true

        var id = savedInstanceState?.getLong(Extra.boardId, -1) ?: -1;
        if (id == -1L) {
            id = arguments?.getLong(Extra.boardId, -1) ?: -1
        }
        val state = LessonsState(id)
        presenter = LessonsPresenter(appKodein(), state)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_lessons, container, false) as RecyclerView
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        view.layoutManager = layoutManager
        adapter = LessonsAdapter();
        view.adapter = adapter;

        return view
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this, false)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.destroy()
    }

    override fun showList(list: List<Lesson>) {
        adapter.list = list;
    }
}