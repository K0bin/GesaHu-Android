package rhedox.gesahuvertretungsplan.model.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
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