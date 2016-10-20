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
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesAdapter

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesFragment : Fragment() {

    private lateinit var adapter: SubstitutesAdapter
    private var substituteActivity: SubstitutesActivity? = null;

    private var position: Int = -1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(arguments != null && arguments.containsKey(argumentPosition))
            position = arguments.getInt(argumentPosition);
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if(context is SubstitutesActivity)
            substituteActivity = context
    }

    override fun onDetach() {
        super.onDetach()
        substituteActivity = null;
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter = SubstitutesAdapter(activity)

        if(substituteActivity != null) {
            adapter.showList(substituteActivity!!.presenter.getSubstitutes(position))
        }

        recycler.adapter = adapter
    }

    /**
     * Display the loaded list
     */
    fun populateList(substitutes: List<Substitute>) {
        /*
        if(sortImportant)
            substitutes = Collections.unmodifiableList(SubstitutesList.sort(substitutesList.getSubstitutes()));
        else if (filterImportant)
            substitutes = Collections.unmodifiableList(SubstitutesList.filterImportant(substitutesList.getSubstitutes()));
        else*/

        adapter.showList(substitutes)
        recycler.scrollToPosition(0)
    }

    companion object {
        const val argumentPosition = "position";

        fun createInstance(position: Int): SubstitutesFragment {
            val bundle = Bundle();
            bundle.putInt(argumentPosition, position)
            val fragment = SubstitutesFragment()
            fragment.arguments = bundle
            return fragment;
        }
    }
}