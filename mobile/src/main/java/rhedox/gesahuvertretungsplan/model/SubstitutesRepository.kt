package rhedox.gesahuvertretungsplan.model

import androidx.lifecycle.LiveData
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision

/**
 * Created by robin on 11.03.2018.
 */
interface SubstitutesRepository {
    fun loadSubstitutesForDay(date: LocalDate): LiveData<List<Substitute>>
    fun loadAnnouncementForDay(date: LocalDate): LiveData<Announcement>
    fun loadSupervisionsForDay(date: LocalDate): LiveData<List<Supervision>>
    fun loadSubstitutesForDaySync(date: LocalDate, onlyRelevant: Boolean = false): List<Substitute>
}