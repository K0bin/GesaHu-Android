package rhedox.gesahuvertretungsplan.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute

/**
 * Created by robin on 08.03.2018.
 */
@Dao
abstract class SubstitutesDao {
    @Insert
    abstract fun insert(vararg substitutes: Substitute)

    @Query("DELETE FROM ${Substitute.tableName} WHERE date < :olderThan OR date IN (:dates);")
    abstract fun clear(olderThan: LocalDate, vararg dates: LocalDate)

    @Query("SELECT * FROM ${Substitute.tableName} WHERE date = :date ORDER BY isRelevant DESC, lessonBegin ASC, duration ASC, course ASC, subject ASC;")
    abstract fun get(date: LocalDate): LiveData<List<Substitute>>

    @Transaction
    open fun insertAndClear(substitutes: Collection<Substitute>, olderThan: LocalDate, dates: Collection<LocalDate>) {
        clear(olderThan, *dates.toTypedArray())
        insert(*substitutes.toTypedArray())
    }

    @Query("SELECT * FROM ${Substitute.tableName} WHERE date = :date AND isRelevant = 1 ORDER BY lessonBegin ASC, duration ASC, course ASC, subject ASC;")
    abstract fun getRelevantSync(date: LocalDate): List<Substitute>
    @Query("SELECT * FROM ${Substitute.tableName} WHERE date = :date ORDER BY lessonBegin ASC, duration ASC, course ASC, subject ASC;")
    abstract fun getSync(date: LocalDate): List<Substitute>
}