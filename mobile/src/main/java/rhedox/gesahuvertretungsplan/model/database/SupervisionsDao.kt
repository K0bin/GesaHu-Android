package rhedox.gesahuvertretungsplan.model.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Supervision

/**
 * Created by robin on 08.03.2018.
 */
@Dao
interface SupervisionsDao {
    @Insert
    fun insert(vararg supervisions: Supervision)

    @Query("DELETE FROM ${Supervision.tableName} WHERE date IN (:date);")
    fun delete(vararg date: LocalDate)

    @Query("DELETE FROM ${Supervision.tableName} WHERE date < :olderThan;")
    fun clear(olderThan: LocalDate)

    @Query("DELETE FROM ${Supervision.tableName} WHERE 1;")
    fun clear()

    @Query("SELECT * FROM ${Supervision.tableName} WHERE date = :date;")
    fun get(date: LocalDate): LiveData<List<Supervision>>
}