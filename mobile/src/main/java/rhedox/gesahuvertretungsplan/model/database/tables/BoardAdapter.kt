package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.api.json.BoardName
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 19.10.2016.
 */
object BoardAdapter {
    fun toContentValues(board: BoardName): ContentValues {
        val values = ContentValues();
        values.put(BoardsContract.Table.columnName, board.name)
        return values;
    }

    fun toContentValues(board: Board): ContentValues {
        val values = ContentValues();
        values.put(BoardsContract.Table.columnName, board.name)
        values.put(BoardsContract.Table.columnMark, board.mark)
        values.put(BoardsContract.Table.columnMarkRemark, board.markRemark)
        values.put(BoardsContract.Table.columnLessonsTotal, board.lessonsTotal)
        values.put(BoardsContract.Table.columnMissedLessons, board.missedLessons)
        values.put(BoardsContract.Table.columnMissedLessonsWithSickNotes, board.missedLessonsWithSickNotes)
        return values;
    }

    fun toContentValues(lesson: Board.Lesson): ContentValues {
        val values = ContentValues();
        values.put(LessonsContract.Table.columnBoardId, lesson.boardId)
        values.put(LessonsContract.Table.columnId, lesson.id)
        values.put(LessonsContract.Table.columnDate, lesson.date.unixTimeStamp)
        values.put(LessonsContract.Table.columnTopic, lesson.topic)
        values.put(LessonsContract.Table.columnDuration, lesson.duration)
        values.put(LessonsContract.Table.columnStatus, lesson.status)
        values.put(LessonsContract.Table.columnHomework, lesson.homeWork)
        values.put(LessonsContract.Table.columnHomeworkDue, lesson.homeWorkDue?.unixTimeStamp)
        return values;
    }

    fun toContentValues(lesson: Board.Mark): ContentValues {
        val values = ContentValues();
        values.put(MarksContract.Table.columnBoardId, lesson.boardId)
        values.put(MarksContract.Table.columnId, lesson.id)
        values.put(MarksContract.Table.columnDate, lesson.date.unixTimeStamp)
        values.put(MarksContract.Table.columnDescription, lesson.description)
        values.put(MarksContract.Table.columnMark, lesson.mark)
        values.put(MarksContract.Table.columnMarkKind, lesson.markKind)
        values.put(MarksContract.Table.columnKind, lesson.kind)
        values.put(MarksContract.Table.columnAverage, lesson.average)
        values.put(MarksContract.Table.columnLogo, lesson.logo)
        values.put(MarksContract.Table.columnWeighting, lesson.weighting)
        return values;
    }

    fun nameFromCursor(cursor: Cursor): String? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        return cursor.getString(cursor.getColumnIndex(BoardsContract.Table.columnName));
    }

    fun namesFromCursor(cursor: Cursor): List<String> {
        val list = mutableListOf<String>();
        if (cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val board = BoardAdapter.nameFromCursor(cursor)

            if (board != null)
                list.add(board);

            cursor.moveToNext()
        }
        return list;
    }

    fun boardFromCursors(boardCursor: Cursor, lessonsCursor: Cursor, marksCursor: Cursor): Board? {
        if (boardCursor.count == 0 || boardCursor.columnCount < 1 || boardCursor.isClosed)
            return null

        val name = boardCursor.getString(boardCursor.getColumnIndex(BoardsContract.Table.columnName))
        val mark = boardCursor.getInt(boardCursor.getColumnIndex(BoardsContract.Table.columnMark))
        val markRemark = boardCursor.getString(boardCursor.getColumnIndex(BoardsContract.Table.columnMarkRemark))
        val missedLessons = boardCursor.getInt(boardCursor.getColumnIndex(BoardsContract.Table.columnMissedLessons))
        val missedLessonsWithSickNotes = boardCursor.getInt(boardCursor.getColumnIndex(BoardsContract.Table.columnMissedLessonsWithSickNotes))
        val lessonsTotal = boardCursor.getInt(boardCursor.getColumnIndex(BoardsContract.Table.columnLessonsTotal))
        val id = boardCursor.getLong(boardCursor.getColumnIndex(BoardsContract.Table.columnId))

        val marks = marksFromCursor(marksCursor)
        val lessons = lessonsFromCursor(lessonsCursor)

        return Board(name, mark, markRemark, missedLessons, missedLessonsWithSickNotes, lessonsTotal, marks, lessons, id)
    }

    fun marksFromCursor(cursor: Cursor): List<Board.Mark> {
        val list = mutableListOf<Board.Mark>();
        if (cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val mark = markFromCursor(cursor)

            if (mark != null)
                list.add(mark);

            cursor.moveToNext()
        }
        return list;
    }

    fun markFromCursor(cursor: Cursor): Board.Mark? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        val date = localDateFromUnix(cursor.getInt(cursor.getColumnIndex(MarksContract.Table.columnDate)))
        val description = cursor.getString(cursor.getColumnIndex(MarksContract.Table.columnDescription))
        val mark = cursor.getInt(cursor.getColumnIndex(MarksContract.Table.columnMark))
        val kind = cursor.getString(cursor.getColumnIndex(MarksContract.Table.columnKind))
        val average = cursor.getFloat(cursor.getColumnIndex(MarksContract.Table.columnAverage))
        val markKind = cursor.getLong(cursor.getColumnIndex(MarksContract.Table.columnMarkKind))
        val logo = cursor.getString(cursor.getColumnIndex(MarksContract.Table.columnLogo))
        val weighting = cursor.getFloat(cursor.getColumnIndex(MarksContract.Table.columnWeighting))
        val id = cursor.getLong(cursor.getColumnIndex(MarksContract.Table.columnBoardId))
        val boardsId = cursor.getLong(cursor.getColumnIndex(MarksContract.Table.columnBoardId))

        return Board.Mark(date, description, mark, kind, average, markKind, logo, weighting, id = id, boardId = boardsId)
    }

    fun lessonsFromCursor(cursor: Cursor): List<Board.Lesson> {
        val list = mutableListOf<Board.Lesson>();
        if (cursor.count == 0 || cursor.isClosed)
            return list;

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val lesson = lessonFromCursor(cursor)

            if (lesson != null)
                list.add(lesson);

            cursor.moveToNext()
        }
        return list;
    }

    fun lessonFromCursor(cursor: Cursor): Board.Lesson? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        val date = localDateFromUnix(cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnDate)))
        val topic = cursor.getString(cursor.getColumnIndex(LessonsContract.Table.columnTopic))
        val duration = cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnDuration))
        val status = cursor.getLong(cursor.getColumnIndex(LessonsContract.Table.columnStatus))
        val homework = cursor.getString(cursor.getColumnIndex(LessonsContract.Table.columnHomework))
        val homeworkDue = localDateFromUnix(cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnHomeworkDue)))
        val id = cursor.getLong(cursor.getColumnIndex(LessonsContract.Table.columnBoardId))
        val boardsId = cursor.getLong(cursor.getColumnIndex(MarksContract.Table.columnBoardId))

        return Board.Lesson(date, topic, duration, status, homework, homeworkDue, id = id, boardId = boardsId)
    }
}