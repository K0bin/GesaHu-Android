package rhedox.gesahuvertretungsplan.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import rhedox.gesahuvertretungsplan.model.database.entity.Mark

/**
 * Created by robin on 09.03.2018.
 */
@Dao
abstract class MarksDao {
    @Insert
    abstract fun insert(vararg marks: Mark)

    @Query("DELETE FROM ${Mark.tableName} WHERE 1;")
    abstract fun clear()

    @Query("SELECT * FROM ${Mark.tableName} WHERE boardName = :boardName ORDER BY date DESC;")
    abstract fun get(boardName: String): LiveData<List<Mark>>

    @Transaction
    open fun insertAndClear(vararg marks: Mark) {
        clear()
        insert(*marks)
    }
}