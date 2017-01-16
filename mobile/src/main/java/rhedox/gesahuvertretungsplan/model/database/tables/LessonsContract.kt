package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider

/**
 * Created by robin on 30.10.2016.
 */
object LessonsContract {
    const val path = "lessons"
    const val boardPath = "board";

    object Table {
        const val name = "lessons";
        const val columnId = "ROWID";
        const val columnBoardId = "boardId";
        const val columnDate = "date"
        const val columnTopic = "topic"
        const val columnDuration = "duration"
        const val columnHomework = "homework"
        const val columnHomeworkDue = "homeworkDue"

        val columns = setOf(
                columnId, columnDate, columnTopic, columnDuration, columnHomework, columnHomeworkDue)
    }

    val uri: Uri = Uri.Builder()
            .scheme("content")
            .authority(BoardsContentProvider.authority)
            .path(path)
            .build();
    val boardUri = Uri.withAppendedPath(uri, boardPath)

    fun onCreate(db: SQLiteDatabase) {
        val sql = """CREATE TABLE ${Table.name}
            (
                ${Table.columnDate} INTEGER,
                ${Table.columnBoardId} INTEGER,
                ${Table.columnTopic} TEXT,
                ${Table.columnDuration} INTEGER,
                ${Table.columnHomework} TEXT,
                ${Table.columnHomeworkDue} INTEGER,
                FOREIGN KEY(${Table.columnBoardId}) REFERENCES ${BoardsContract.Table.name}(${BoardsContract.Table.columnId})
            );
            """;
        db.execSQL(sql);
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    fun clear(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM ${BoardsContract.Table.name} WHERE 1;");
    }
}
