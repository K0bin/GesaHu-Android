package rhedox.gesahuvertretungsplan.model.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
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