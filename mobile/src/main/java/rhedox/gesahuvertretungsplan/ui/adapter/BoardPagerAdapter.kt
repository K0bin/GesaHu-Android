package rhedox.gesahuvertretungsplan.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import rhedox.gesahuvertretungsplan.ui.fragment.LessonsFragment
import rhedox.gesahuvertretungsplan.ui.fragment.MarksFragment

/**
 * Created by robin on 19.01.2017.
 */
class BoardPagerAdapter(private val boardId: Long, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return LessonsFragment.createInstance(boardId)
            1 -> return MarksFragment.createInstance(boardId)
            else -> return Fragment()
        }
    }

    override fun getCount(): Int {
        return 2;
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> "Stunden"
            1 -> "Noten"
            else -> "";
        }
    }
}