package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main.*
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.SubstitutesList
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesFragment : Fragment() {

    private var adapter: SubstitutesAdapter? = null
    private var presenter: SubstitutesContract.Presenter? = null;

    private var position: Int = -1;
    private var items: List<Substitute> = listOf()

    object State {
        const val list = "list";
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null && arguments.containsKey(argumentPosition))
            position = arguments.getInt(argumentPosition);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        if(savedInstanceState != null && savedInstanceState.containsKey(State.list))
            recycler.layoutManager!!.onRestoreInstanceState(savedInstanceState.getParcelable(State.list))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(activity is SubstitutesActivity)
            presenter = (activity as SubstitutesActivity).presenter
        adapter = SubstitutesAdapter(position, presenter, activity)

        recycler.adapter = adapter
        presenter?.onTabCreated(position)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()

        presenter = null;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(State.list, recycler?.layoutManager?.onSaveInstanceState())
    }

    /**
     * Display the loaded list
     */
    fun populateList(substitutes: List<Substitute>) {
        Log.d("SubstitutesFragment", "Showing list: $position, ${substitutes.size} items")
        items = substitutes
        adapter?.showList(items)
        recycler?.scrollToPosition(0)
    }

    fun setSelected(listPosition: Int) {
        adapter?.setSelected(listPosition)
    }

    companion object {
        const val argumentPosition = "position";

        @JvmStatic
        fun createInstance(position: Int): SubstitutesFragment {
            val bundle = Bundle();
            bundle.putInt(argumentPosition, position)
            val fragment = SubstitutesFragment()
            fragment.arguments = bundle
            return fragment;
        }
    }
}