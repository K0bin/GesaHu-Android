package rhedox.gesahuvertretungsplan.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement

/**
 * Created by robin on 08.03.2018.
 */
@Dao
abstract class AnnouncementsDao {
    @Insert
    abstract fun insert(vararg announcements: Announcement)

    @Query("DELETE FROM ${Announcement.tableName} WHERE date < :olderThan OR date IN (:dates);")
    abstract fun clear(olderThan: LocalDate, vararg dates: LocalDate)

    @Query("SELECT * FROM ${Announcement.tableName} WHERE date = :date LIMIT 0,1;")
    abstract fun get(date: LocalDate): LiveData<Array<Announcement>>

    @Transaction
    open fun insertAndClear(announcements: Collection<Announcement>, olderThan: LocalDate, dates: Collection<LocalDate>) {
        clear(olderThan, *dates.toTypedArray())
        insert(*announcements.toTypedArray())
    }
}