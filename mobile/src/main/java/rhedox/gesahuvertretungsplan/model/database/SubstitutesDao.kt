package rhedox.gesahuvertretungsplan.model.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.Substitute

/**
 * Created by robin on 08.03.2018.
 */
@Dao
interface SubstitutesDao {
    @Insert
    fun insert(vararg substitutes: Substitute)

    @Query("DELETE FROM ${Substitute.tableName} WHERE date IN (:date);")
    fun delete(vararg date: LocalDate)

    @Query("DELETE FROM ${Substitute.tableName} WHERE date < :olderThan;")
    fun clear(olderThan: LocalDate)

    @Query("DELETE FROM ${Substitute.tableName} WHERE 1;")
    fun clear()

    @Query("SELECT * FROM ${Substitute.tableName} WHERE date = :date AND isRelevant = :isRelevant ORDER BY lessonBegin ASC, duration ASC, course ASC, subject ASC;")
    fun get(date: LocalDate, isRelevant: Boolean = false): LiveData<List<Substitute>>

    @Query("SELECT * FROM ${Substitute.tableName} WHERE date = :date AND isRelevant = :isRelevant ORDER BY lessonBegin ASC, duration ASC, course ASC, subject ASC;")
    fun getSync(date: LocalDate, isRelevant: Boolean = false): List<Substitute>
}