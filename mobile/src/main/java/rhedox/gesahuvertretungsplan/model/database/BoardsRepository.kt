package rhedox.gesahuvertretungsplan.model.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import rhedox.gesahuvertretungsplan.dependencyInjection.PresenterScope
import rhedox.gesahuvertretungsplan.model.database.dao.BoardsDao
import rhedox.gesahuvertretungsplan.model.database.dao.LessonsDao
import rhedox.gesahuvertretungsplan.model.database.dao.MarksDao
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.util.Open
import javax.inject.Inject

/**
 * Created by robin on 29.10.2016.
 */
@Open
@PresenterScope
class BoardsRepository @Inject internal constructor(private val boardsDao: BoardsDao, private val lessonsDao: LessonsDao, private val marksDao: MarksDao) {

    fun loadBoards(): LiveData<List<Board>> = boardsDao.get()
    fun loadBoard(boardName: String): LiveData<Board> = Transformations.map(boardsDao.get()) {
        it.firstOrNull {b -> b.name == boardName}
    }

    fun loadMarks(boardName: String): LiveData<List<Mark>> = marksDao.get(boardName)

    fun loadLessons(boardName: String): LiveData<List<Lesson>> = lessonsDao.get(boardName)
}