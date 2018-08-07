package rhedox.gesahuvertretungsplan.model.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
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