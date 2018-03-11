package rhedox.gesahuvertretungsplan.model.database.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import rhedox.gesahuvertretungsplan.model.database.entity.Board

/**
 * Created by robin on 09.03.2018.
 */
@Dao
abstract class BoardsDao {
    @Insert
    abstract fun insert(vararg boards: Board)

    @Query("DELETE FROM ${Board.tableName} WHERE 1;")
    abstract fun clear()

    @Query("SELECT * FROM ${Board.tableName} WHERE 1;")
    abstract fun get(): LiveData<List<Board>>

    @Transaction
    open fun insertAndClear(vararg boards: Board) {
        clear()
        insert(*boards)
    }
}