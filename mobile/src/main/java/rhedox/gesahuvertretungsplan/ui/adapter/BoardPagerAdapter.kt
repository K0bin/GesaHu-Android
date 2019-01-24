package rhedox.gesahuvertretungsplan.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import rhedox.gesahuvertretungsplan.ui.fragment.LessonsFragment
import rhedox.gesahuvertretungsplan.ui.fragment.MarksFragment

/**
 * Created by robin on 19.01.2017.
 */
class BoardPagerAdapter(private val lessonsFragment: LessonsFragment, private val marksFragment: MarksFragment, fragmentManager: FragmentManager) : TaggedFragmentPagerAdapter(fragmentManager) {
    init {
        if (lessonsFragment.isInLayout || marksFragment.isInLayout) {
            throw IllegalStateException("The fragments must not be in the layout!")
        }
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> lessonsFragment
            1 -> marksFragment
            else -> Fragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Stunden"
            1 -> "Noten"
            else -> ""
        }
    }

    override fun getFragmentTag(position: Int): String {
        return when (position) {
            0 -> "marks"
            1 -> "lessons"
            else -> "error"
        }
    }
}