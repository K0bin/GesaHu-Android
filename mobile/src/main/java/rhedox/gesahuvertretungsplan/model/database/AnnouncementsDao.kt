package rhedox.gesahuvertretungsplan.model.database

import android.arch.lifecycle.LiveData
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

    @Query("DELETE FROM ${Announcement.tableName} WHERE date IN (:date);")
    fun delete(vararg date: LocalDate)

    @Query("DELETE FROM ${Announcement.tableName} WHERE date < :olderThan;")
    fun clear(olderThan: LocalDate)

    @Query("DELETE FROM ${Announcement.tableName} WHERE 1;")
    fun clear()

    @Query("SELECT * FROM ${Announcement.tableName} WHERE date = :date LIMIT 0,1;")
    fun get(date: LocalDate): LiveData<Array<Announcement>>
}