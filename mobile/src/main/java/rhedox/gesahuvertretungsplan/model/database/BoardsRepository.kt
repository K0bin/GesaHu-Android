package rhedox.gesahuvertretungsplan.model.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.content.Context
import com.github.salomonbrys.kodein.android.appKodein
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.model.database.dao.BoardsDao
import rhedox.gesahuvertretungsplan.model.database.dao.LessonsDao
import rhedox.gesahuvertretungsplan.model.database.dao.MarksDao
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.util.Open

/**
 * Created by robin on 29.10.2016.
 */
@Open
class BoardsRepository(context: Context) {
    private val boardsDao = context.appKodein().instance<BoardsDao>()
    private val lessonsDao = context.appKodein().instance<LessonsDao>()
    private val marksDao = context.appKodein().instance<MarksDao>()

    fun loadBoards(): LiveData<List<Board>> = boardsDao.get()
    fun loadBoard(boardName: String): LiveData<Board> = Transformations.map(boardsDao.get()) {
        it.firstOrNull {b -> b.name == boardName}
    }

    fun loadMarks(boardName: String): LiveData<List<Mark>> = marksDao.get(boardName)

    fun loadLessons(boardName: String): LiveData<List<Lesson>> = lessonsDao.get(boardName)
}