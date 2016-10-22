package rhedox.gesahuvertretungsplan.ui.activity

import android.annotation.TargetApi
import android.app.ActivityManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.v4.util.Pair
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.presenter.BaseActivityPresenter
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.ui.adapters.PagerAdapter
import rhedox.gesahuvertretungsplan.ui.adapters.SubstitutesPagerAdapter
import rhedox.gesahuvertretungsplan.ui.fragment.DatePickerFragment

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesActivity : BaseActivity(), SubstitutesContract.View, ViewPager.OnPageChangeListener {
    companion object {
        const val extraDate = "date";
        const val extraBack = "back";
    }

    lateinit var presenter: SubstitutesContract.Presenter
    private var canGoBack = false;
    lateinit var pagerAdapter: SubstitutesPagerAdapter
            private set;

    override var isFloatingActionButtonVisible: Boolean
        get() = throw UnsupportedOperationException()
        set(value) {
        }

    override var currentTab: Int
        get() = viewPager?.currentItem ?: -1
        set(value) {
            viewPager?.currentItem = value
        }

    override var isBackButtonVisible: Boolean = false;
        get() = field
        set(value) {
            field = value
            supportActionBar?.setDisplayHomeAsUpEnabled(canGoBack)
        }


    override var isAppBarExpanded: Boolean = true
        get() = field
        set(value) {
            field = value
            appbarLayout.setExpanded(value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Create presenter
        val _presenter = SubstitutesPresenter()
        _presenter.arguments = intent?.extras ?: Bundle.EMPTY
        supportFragmentManager.beginTransaction().add(_presenter, BaseActivityPresenter.tag).commit();
        presenter = _presenter

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setupTaskDescription()

        setContentView(R.layout.activity_main)

        pagerAdapter = SubstitutesPagerAdapter(supportFragmentManager, presenter)
        viewPager.adapter = pagerAdapter
        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(this)

        fab.setOnClickListener { presenter.onFabClicked() }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupTaskDescription() {
        val a = obtainStyledAttributes(intArrayOf(R.attr.colorPrimary))
        val primaryColor = a.getColor(0, 0)
        a.recycle()

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_task)
        val description = ActivityManager.TaskDescription(getString(R.string.app_name), bitmap, primaryColor)
        this.setTaskDescription(description)
    }

    override fun populateList(position: Int, list: List<Substitute>) {
        val fragment = pagerAdapter.getFragment(supportFragmentManager, position)
        fragment?.populateList(list)
    }

    override fun showDatePicker(defaultDate: LocalDate) {
        val picker = DatePickerFragment.newInstance(defaultDate);
        picker.callback = {
            presenter.onDatePicked(it)
        }

        picker.show(supportFragmentManager, "Datepicker")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item == null) return false;

        if (super.onOptionsItemSelected(item))
            return true

        when (item.getItemId()) {
            R.id.action_settings -> {
                val intent = Intent(this, PreferenceActivity::class.java)
                startActivity(intent)
            }

            R.id.action_load -> presenter.onDatePickerIconClicked()

            R.id.action_about -> AboutLibs.start(this, isAmoledBlackEnabled)

            android.R.id.home -> {
                if (canGoBack)
                    this.onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun setIsRefreshing(position: Int, isRefreshing: Boolean) {
        val fragment = pagerAdapter.getFragment(supportFragmentManager, position)
        fragment?.isRefreshing = isRefreshing
    }

    override fun showDialog(text: String) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    override fun onPageScrollStateChanged(state: Int) { }
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
    override fun onPageSelected(position: Int) {
        presenter.onActiveTabChanged(position)
    }
}