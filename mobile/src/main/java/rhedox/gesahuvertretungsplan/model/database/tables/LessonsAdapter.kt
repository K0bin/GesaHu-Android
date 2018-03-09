package rhedox.gesahuvertretungsplan.model.database.tables

import android.content.ContentValues
import android.database.Cursor
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.util.localDateFromUnix
import rhedox.gesahuvertretungsplan.util.unixTimeStamp

/**
 * Created by robin on 19.01.2017.
 */
object LessonsAdapter {
    fun toContentValues(lesson: Lesson, boardId: Long? = null): ContentValues {
        val values = ContentValues();
        values.put(LessonsContract.Table.columnBoardId, boardId ?: lesson.boardId)
        values.put(LessonsContract.Table.columnId, lesson.id)
        values.put(LessonsContract.Table.columnDate, lesson.date.unixTimeStamp)
        values.put(LessonsContract.Table.columnTopic, lesson.topic)
        values.put(LessonsContract.Table.columnDuration, lesson.duration)
        values.put(LessonsContract.Table.columnStatus, lesson.status)
        values.put(LessonsContract.Table.columnHomework, if (lesson.homeWork != "-") lesson.homeWork else "")
        values.put(LessonsContract.Table.columnHomeworkDue, lesson.homeWorkDue?.unixTimeStamp)
        return values;
    }

    fun lessonsFromCursor(cursor: Cursor): List<Lesson> {
        val list = mutableListOf<Lesson>();
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

    fun lessonFromCursor(cursor: Cursor): Lesson? {
        if (cursor.count == 0 || cursor.columnCount < 1 || cursor.isClosed)
            return null

        val date = localDateFromUnix(cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnDate)))
        val topic = cursor.getString(cursor.getColumnIndex(LessonsContract.Table.columnTopic))
        val duration = cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnDuration))
        val status = cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnStatus))
        val homework = cursor.getString(cursor.getColumnIndex(LessonsContract.Table.columnHomework))
        val homeworkDueInt = cursor.getInt(cursor.getColumnIndex(LessonsContract.Table.columnHomeworkDue))
        val homeworkDue = if (homeworkDueInt != 0) localDateFromUnix(homeworkDueInt) else null
        val id = cursor.getLong(cursor.getColumnIndex(LessonsContract.Table.columnBoardId))
        val boardId = cursor.getLong(cursor.getColumnIndex(MarksContract.Table.columnBoardId))

        return Lesson(date, topic, duration, status, homework, homeworkDue, id = id, boardId = boardId)
    }
}