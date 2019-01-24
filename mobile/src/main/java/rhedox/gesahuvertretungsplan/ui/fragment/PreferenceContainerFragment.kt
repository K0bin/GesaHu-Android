package rhedox.gesahuvertretungsplan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_preference_container.*
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.activity.DrawerActivity

/**
 * Created by robin on 07.02.2017.
 */
class PreferenceContainerFragment : AnimationFragment() {
    companion object {
        @JvmStatic
        fun newInstance(): PreferenceContainerFragment {
            return PreferenceContainerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preference_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawerActivity = activity as? DrawerActivity
        drawerActivity?.setSupportActionBar(toolbar)
        drawerActivity?.supportActionBar?.title = getString(R.string.action_settings)
        if (drawerActivity?.isPermanentDrawer == false) {
            drawerActivity.supportActionBar!!.setHomeButtonEnabled(true)
            drawerActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            drawerActivity.syncDrawer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? DrawerActivity)?.setSupportActionBar(null)
    }
}