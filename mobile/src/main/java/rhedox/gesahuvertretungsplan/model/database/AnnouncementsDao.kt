package rhedox.gesahuvertretungsplan.model.database

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import org.joda.time.LocalDate

/**
 * Created by robin on 08.03.2018.
 */
@Dao
interface AnnouncementsDao {
    @Insert
    fun insert(vararg announcements: Announcement)

    @Query("DELETE FROM ${Announcement.tableName} WHERE 1")
    fun clear()

    @Query("SELECT * FROM ${Announcement.tableName} WHERE date = :date")
    fun get(date: LocalDate): List<Announcement>
}