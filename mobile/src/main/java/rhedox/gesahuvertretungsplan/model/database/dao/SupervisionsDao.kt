package rhedox.gesahuvertretungsplan.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision

/**
 * Created by robin on 08.03.2018.
 */
@Dao
abstract class SupervisionsDao {
    @Insert
    abstract fun insert(vararg supervisions: Supervision)

    @Query("DELETE FROM ${Supervision.tableName} WHERE date < :olderThan OR date IN (:dates);")
    abstract fun clear(olderThan: LocalDate, vararg dates: LocalDate)

    @Query("SELECT * FROM ${Supervision.tableName} WHERE date = :date;")
    abstract fun get(date: LocalDate): LiveData<List<Supervision>>

    @Transaction
    open fun insertAndClear(supervisions: Collection<Supervision>, olderThan: LocalDate, dates: Collection<LocalDate>) {
        clear(olderThan, *dates.toTypedArray())
        insert(*supervisions.toTypedArray())
    }
}