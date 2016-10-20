package rhedox.gesahuvertretungsplan.ui.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.joda.time.DurationFieldType
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.ui.fragment.SubstitutesFragment
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPagerAdapter(manager: FragmentManager, private val presenter: SubstitutesContract.Presenter): FragmentPagerAdapter(manager) {

    private var fragments: Array<SubstitutesFragment?> = arrayOf<SubstitutesFragment?>(null, null, null, null, null)

    override fun getItem(position: Int): Fragment {
        val fragment = SubstitutesFragment.createInstance(position)
        fragments[position] = fragment
        return fragment
    }

    override fun getCount(): Int {
        return 5
    }

    override fun getPageTitle(position: Int): CharSequence {
        return presenter.getTabTitle(position)
    }

    fun makeFragmentName(viewPagerId: Int, fragmentPosition: Int): String {
        return "android:switcher:$viewPagerId:$fragmentPosition"
    }

    fun destroy() {
        for (i in this.fragments.indices)
            this.fragments[i] = null
    }

    fun getFragment(manager: FragmentManager, position: Int): SubstitutesFragment? {
        if (position < 0 || position >= fragments.size)
            return null;

        var substitutesFragment: SubstitutesFragment? = fragments[position]
        if (substitutesFragment != null)
            return substitutesFragment
        else {
            val fragment = manager.findFragmentByTag(makeFragmentName(R.id.viewPager, position))
            if (fragment != null && fragment is SubstitutesFragment) {
                substitutesFragment = fragment
                fragments[position] = substitutesFragment
                return substitutesFragment
            } else
                return null
        }
    }
}