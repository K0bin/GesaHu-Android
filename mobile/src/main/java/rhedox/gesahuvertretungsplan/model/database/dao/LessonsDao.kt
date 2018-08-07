package rhedox.gesahuvertretungsplan.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson

/**
 * Created by robin on 09.03.2018.
 */
@Dao
abstract class LessonsDao {
    @Insert
    abstract fun insert(vararg lessons: Lesson)

    @Query("DELETE FROM ${Lesson.tableName} WHERE 1;")
    abstract fun clear()

    @Query("SELECT * FROM ${Lesson.tableName} WHERE boardName = :boardName ORDER BY date DESC;")
    abstract fun get(boardName: String): LiveData<List<Lesson>>

    @Transaction
    open fun insertAndClear(vararg lessons: Lesson) {
        clear()
        insert(*lessons)
    }
}