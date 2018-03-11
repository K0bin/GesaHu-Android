package rhedox.gesahuvertretungsplan.test.localUnitTest.repository

import android.arch.lifecycle.MutableLiveData
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.SubstitutesRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision

/**
 * Created by robin on 11.03.2018.
 */
class SubstitutesTestRepository: SubstitutesRepository {
    private val substitutes = hashMapOf<LocalDate, MutableLiveData<List<Substitute>>>()
    private val supervisions = hashMapOf<LocalDate, MutableLiveData<List<Supervision>>>()
    private val announcements = hashMapOf<LocalDate, MutableLiveData<Announcement>>()

    override fun loadSubstitutesForDay(date: LocalDate): MutableLiveData<List<Substitute>> {
        if (substitutes.containsKey(date))
            return substitutes[date]!!

        val liveData = MutableLiveData<List<Substitute>>()
        substitutes[date] = liveData
        return liveData
    }

    override fun loadAnnouncementForDay(date: LocalDate): MutableLiveData<Announcement> {
        if (announcements.containsKey(date))
            return announcements[date]!!

        val liveData = MutableLiveData<Announcement>()
        announcements[date] = liveData
        return liveData
    }

    override fun loadSupervisionsForDay(date: LocalDate): MutableLiveData<List<Supervision>> {
        if (supervisions.containsKey(date))
            return supervisions[date]!!

        val liveData = MutableLiveData<List<Supervision>>()
        supervisions[date] = liveData
        return liveData
    }

    override fun loadSubstitutesForDaySync(date: LocalDate, onlyRelevant: Boolean): List<Substitute> = listOf()
}