package rhedox.gesahuvertretungsplan.presenter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.util.Pair
import rhedox.gesahuvertretungsplan.model.SubstitutesList
import rhedox.gesahuvertretungsplan.model.database.SubstitutesLoaderHelper
import rhedox.gesahuvertretungsplan.mvp.SubstitutesContract
import rhedox.gesahuvertretungsplan.ui.activity.SubstitutesActivity
import com.pawegio.kandroid.startActivity;
import org.joda.time.*
import rhedox.gesahuvertretungsplan.model.SchoolWeek
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity1
import java.util.*

/**
 * Created by robin on 20.10.2016.
 */
class SubstitutesPresenter : Fragment(), SubstitutesContract.Presenter, SubstitutesLoaderHelper.Callback {
    companion object {
        const val tag = "SubstitutesPresenter";
    }

    private var date: LocalDate = LocalDate();
    private var view: SubstitutesContract.View? = null
    private lateinit var helpers: Array<SubstitutesLoaderHelper>
    private var substitutes = arrayOf<List<Substitute>?>(null, null, null, null, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments.containsKey(SubstitutesActivity.extraDate)) {
            date = DateTime(arguments.getLong(SubstitutesActivity.extraDate)).toLocalDate()

            view?.isBackButtonVisible = arguments.containsKey(SubstitutesActivity.extraBack) && arguments.getBoolean(SubstitutesActivity.extraBack)
        } else
            date = SchoolWeek.next();

        view?.currentTab = date.dayOfWeek - DateTimeConstants.MONDAY
        date = getFirstDayOfWeek(date)

        helpers = arrayOf(
                SubstitutesLoaderHelper(loaderManager, context, date, this),
                SubstitutesLoaderHelper(loaderManager, context, date.withFieldAdded(DurationFieldType.days(), 1), this),
                SubstitutesLoaderHelper(loaderManager, context, date.withFieldAdded(DurationFieldType.days(), 2), this),
                SubstitutesLoaderHelper(loaderManager, context, date.withFieldAdded(DurationFieldType.days(), 3), this),
                SubstitutesLoaderHelper(loaderManager, context, date.withFieldAdded(DurationFieldType.days(), 4), this)
        );

        helpers.forEach { it.load() }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if(context is SubstitutesContract.View)
            view = context
    }

    override fun onDetach() {
        super.onDetach()

        view = null;
    }

    override fun onSubstitutesLoaded(substitutesList: SubstitutesList) {
        val position = substitutesList.date.dayOfWeek - DateTimeConstants.MONDAY;
        substitutes[position] = substitutesList.substitutes
        view?.populateList(position, substitutesList.substitutes)
    }

    override fun onDatePickerIconClicked() {
        view?.showDatePicker(date)
    }


    private fun getFirstDayOfWeek(date: LocalDate): LocalDate {
        val index = date.dayOfWeek - DateTimeConstants.MONDAY
        val monday = date.minusDays(index)
        return monday
    }

    override fun onDatePicked(date: LocalDate) {
        if (date.weekOfWeekyear != this.date.weekOfWeekyear) {
            //Launch a new activity with that week
            val intent = Intent(context, SubstitutesActivity::class.java)
            intent.putExtra(SubstitutesActivity.extraDate, date.toDateTime(LocalTime(0)).millis)
            intent.putExtra(SubstitutesActivity.extraBack, true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            //Same week => just switch to day-tab
            val index = date.dayOfWeek - DateTimeConstants.MONDAY
            val dayIndex = Math.max(0, Math.min(index, 5))
            view?.currentTab = dayIndex
        }
    }
    override fun getSubstitutes(position: Int): List<Substitute> {
        return substitutes[position] ?: listOf()
    }
    override fun getTabTitle(position: Int): String {
        return date.withFieldAdded(DurationFieldType.days(), position + 1 - date.dayOfWeek).toString("EEE dd.MM.yy", Locale.GERMANY)
    }

}