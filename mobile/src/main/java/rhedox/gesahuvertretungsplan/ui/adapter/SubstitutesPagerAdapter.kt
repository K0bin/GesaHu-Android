package rhedox.gesahuvertretungsplan.ui.adapter

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract

/**
 * Created by robin on 25.12.2016.
 */
class SubstitutesPagerAdapter(private val presenter: SubstitutesContract.Presenter) : PagerAdapter() {

    var tabTitles: Array<String> = arrayOf("","","","","")

    private val recyclerViews = arrayOfNulls<RecyclerView>(5)

    private val layoutManagerStates = arrayOfNulls<Parcelable>(5)

    private val pool = RecyclerView.RecycledViewPool()

    object State {
        const val layoutManagerStates = "layoutManagerStates"
        const val state = "pagerState"
    }

    fun restoreState(bundle: Bundle) {
        val states = bundle.getParcelableArray(State.layoutManagerStates)
        states?.forEachIndexed { i, parcelable ->
            layoutManagerStates[i] = parcelable
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (recyclerViews[position] == null) {
            val inflater = LayoutInflater.from(container.context)
            val recyclerView = inflater.inflate(R.layout.fragment_main, container, false) as RecyclerView;
            val layoutManager = LinearLayoutManager(container.context, LinearLayoutManager.VERTICAL, false)
            if (layoutManagerStates[position] != null) layoutManager.onRestoreInstanceState(layoutManagerStates[position])
            layoutManager.recycleChildrenOnDetach = true
            recyclerView.layoutManager = layoutManager
            val adapter = SubstitutesAdapter(presenter, container.context)
            recyclerView.adapter = adapter;
            recyclerView.recycledViewPool = pool;
            recyclerView.setHasFixedSize(true)

            container.addView(recyclerView);
            recyclerViews[position] = recyclerView
            presenter.onPageAttached(position)
            return recyclerView
        } else {
            val recyclerView = recyclerViews[position]!!
            container.addView(recyclerView);
            return recyclerView
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, page: Any) {
        val recyclerView = page as RecyclerView
        layoutManagerStates[position] = recyclerView.layoutManager.onSaveInstanceState()

        container.removeView(recyclerView);
    }

    override fun isViewFromObject(view: View, page: Any): Boolean {
        return view == page;
    }

    override fun getCount(): Int {
        return 5;
    }

    override fun getPageTitle(position: Int): CharSequence {
        return tabTitles[position]
    }

    fun getAdapter(position: Int): SubstitutesAdapter? {
        return recyclerViews[position]?.adapter as? SubstitutesAdapter
    }

    fun save(bundle: Bundle) {
        bundle.putParcelableArray(State.layoutManagerStates, layoutManagerStates)
    }
}