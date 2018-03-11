package rhedox.gesahuvertretungsplan.model

import android.arch.lifecycle.LiveData
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.model.database.entity.Mark

/**
 * Created by robin on 11.03.2018.
 */
interface BoardsRepository {
    fun loadBoards(): LiveData<List<Board>>
    fun loadBoard(boardName: String): LiveData<Board>
    fun loadMarks(boardName: String): LiveData<List<Mark>>
    fun loadLessons(boardName: String): LiveData<List<Lesson>>
}