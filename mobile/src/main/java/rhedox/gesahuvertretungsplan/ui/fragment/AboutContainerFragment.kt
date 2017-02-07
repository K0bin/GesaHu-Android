package rhedox.gesahuvertretungsplan.ui.fragment

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.preference.PreferenceManager
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.ui.activity.DrawerActivity
import kotlinx.android.synthetic.main.fragment_preference_container.toolbar;

/**
 * Created by robin on 07.02.2017.
 */
class AboutContainerFragment : Fragment() {
    companion object {
        const val tag: String = "aboutContainerFragment"

        @JvmStatic
        fun newInstance(): AboutContainerFragment {
            return AboutContainerFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_preference_container, container, false)
        childFragmentManager.beginTransaction().replace(R.id.child_fragment_container, createFragment(context)).commit()
        return view;
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val drawerActivity = activity as? DrawerActivity
        drawerActivity?.setSupportActionBar(toolbar)
        drawerActivity?.supportActionBar?.title = getString(R.string.action_about)
        drawerActivity?.supportActionBar?.setHomeButtonEnabled(true)
        drawerActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        drawerActivity?.syncDrawer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? DrawerActivity)?.setSupportActionBar(null)
    }

    private fun createFragment(context: Context): Fragment {
        val isDarkThemeEnabled = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val isAmoledBlackEnabled = prefs.getBoolean(PreferenceFragment.PREF_AMOLED, false)

        return LibsBuilder()
                .withFields(R.string::class.java.fields)
                .withAboutAppName(context.resources.getString(R.string.app_name))
                .withActivityTitle(context.getString(R.string.title_activity_about))
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAboutDescription(Html.fromHtml(context.getString(R.string.about_text)).toString())
                .withVersionShown(true)
                .withActivityStyle(if (isDarkThemeEnabled) Libs.ActivityStyle.DARK else Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTheme(if (!isAmoledBlackEnabled) R.style.GesahuTheme else R.style.GesahuThemeAmoled)
                .withLibraries("MaterialDesignIcons", "Kodein")
                .withAboutSpecial2(context.getString(R.string.special2))
                .withAboutSpecial2Description(context.getString(R.string.special2_description))
                .withAboutSpecial1(context.getString(R.string.special1))
                .supportFragment()
    }
}