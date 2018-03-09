package rhedox.gesahuvertretungsplan.model.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
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