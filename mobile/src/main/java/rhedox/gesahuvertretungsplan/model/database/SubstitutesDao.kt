package rhedox.gesahuvertretungsplan.model.database

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

    @Query("DELETE FROM ${Substitute.tableName} WHERE 1")
    fun clear()

    @Query("SELECT * FROM ${Substitute.tableName} WHERE date = :date")
    fun get(date: LocalDate): List<Substitute>
}