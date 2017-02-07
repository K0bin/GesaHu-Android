package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.activity.DrawerActivity
import kotlinx.android.synthetic.main.fragment_preference_container.toolbar;

/**
 * Created by robin on 07.02.2017.
 */
class PreferenceContainerFragment : Fragment() {
    companion object {
        @JvmStatic
        fun newInstance(): PreferenceContainerFragment {
            return PreferenceContainerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preference_container, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawerActivity = activity as? DrawerActivity
        drawerActivity?.setSupportActionBar(toolbar)
        drawerActivity?.supportActionBar?.title = getString(R.string.action_settings)
        drawerActivity?.supportActionBar?.setHomeButtonEnabled(true)
        drawerActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerActivity?.syncDrawer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? DrawerActivity)?.setSupportActionBar(null)
    }
}