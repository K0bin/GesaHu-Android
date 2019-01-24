package rhedox.gesahuvertretungsplan.test.local_unit_test.repository

import androidx.lifecycle.MutableLiveData
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.model.database.entity.Mark

/**
 * Created by robin on 11.03.2018.
 */
class BoardsTestRepository: BoardsRepository {
    private val boards = MutableLiveData<List<Board>>()
    private val boardsByName = hashMapOf<String, MutableLiveData<Board>>()
    private val lessons = hashMapOf<String, MutableLiveData<List<Lesson>>>()
    private val marks = hashMapOf<String, MutableLiveData<List<Mark>>>()

    override fun loadBoards(): MutableLiveData<List<Board>> = boards

    override fun loadBoard(boardName: String): MutableLiveData<Board> {
        if (boardsByName.containsKey(boardName))
            return boardsByName[boardName]!!

        val liveData = MutableLiveData<Board>()
        boardsByName[boardName] = liveData
        return liveData
    }

    override fun loadMarks(boardName: String): MutableLiveData<List<Mark>> {
        if (marks.containsKey(boardName))
            return marks[boardName]!!

        val liveData = MutableLiveData<List<Mark>>()
        marks[boardName] = liveData
        return liveData
    }

    override fun loadLessons(boardName: String): MutableLiveData<List<Lesson>> {
        if (lessons.containsKey(boardName))
            return lessons[boardName]!!

        val liveData = MutableLiveData<List<Lesson>>()
        lessons[boardName] = liveData
        return liveData
    }
}