package rhedox.gesahuvertretungsplan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder
import com.github.paolorotolo.appintro.ISlideSelectionListener
import rhedox.gesahuvertretungsplan.R

class PrivacyFragment : Fragment(), ISlideBackgroundColorHolder, ISlideSelectionListener {

    companion object {
        @JvmStatic
        fun newInstance(): PrivacyFragment {
            return PrivacyFragment()
        }
    }

    private var bgColor: Int = 0xFFFFFF
    private var analyticsSwitch: Switch? = null
    private var crashReportingSwitch: Switch? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analyticsSwitch = view.findViewById(R.id.crash_reporting_toggle)
        crashReportingSwitch = view.findViewById(R.id.analytics_toggle)
        view.setBackgroundColor(bgColor)
    }

    override fun onDestroyView() {
        analyticsSwitch = null
        crashReportingSwitch = null
        super.onDestroyView()
    }

    override fun getDefaultBackgroundColor(): Int {
        return bgColor
    }

    override fun setBackgroundColor(backgroundColor: Int) {
        bgColor = backgroundColor
        view?.setBackgroundColor(bgColor)
    }


    override fun onSlideSelected() {}

    override fun onSlideDeselected() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = prefs.edit()
        editor.putBoolean(PreferenceFragment.PREF_ANALYTICS, analyticsSwitch?.isChecked == true)
        editor.putBoolean(PreferenceFragment.PREF_CRASH_REPORTS, crashReportingSwitch?.isChecked == true)
        editor.apply()

        PreferenceFragment.applyPrivacy(requireContext(), prefs)
    }
}