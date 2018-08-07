package rhedox.gesahuvertretungsplan.model.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.dependencyInjection.PresenterScope
import rhedox.gesahuvertretungsplan.model.SubstitutesRepository
import rhedox.gesahuvertretungsplan.model.database.dao.AnnouncementsDao
import rhedox.gesahuvertretungsplan.model.database.dao.SubstitutesDao
import rhedox.gesahuvertretungsplan.model.database.dao.SupervisionsDao
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.util.Open
import javax.inject.Inject

/**
 * Created by robin on 29.10.2016.
 */
@Open
@PresenterScope
class SubstitutesDatabaseRepository @Inject constructor(private val substitutesDao: SubstitutesDao, private val supervisionsDao: SupervisionsDao, private val announcementsDao: AnnouncementsDao): SubstitutesRepository {
    override fun loadSubstitutesForDay(date: LocalDate): LiveData<List<Substitute>> = substitutesDao.get(date);

    override fun loadAnnouncementForDay(date: LocalDate): LiveData<Announcement> = Transformations.map(announcementsDao.get(date),
            { it.firstOrNull() }
    )

    override fun loadSupervisionsForDay(date: LocalDate) = supervisionsDao.get(date)

    override fun loadSubstitutesForDaySync(date: LocalDate, onlyRelevant: Boolean): List<Substitute> = if (onlyRelevant) substitutesDao.getRelevantSync(date) else substitutesDao.getSync(date)
}