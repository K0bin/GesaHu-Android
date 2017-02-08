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

    object Table {
        const val name = "lessons";
        const val columnId = "rowid";
        const val columnBoardId = "boardId";
        const val columnDate = "date"
        const val columnTopic = "topic"
        const val columnDuration = "duration"
        const val columnStatus = "status"
        const val columnHomework = "homework"
        const val columnHomeworkDue = "homeworkDue"

        val columns = setOf(
                columnId, columnBoardId, columnDate, columnTopic, columnDuration, columnStatus, columnHomework, columnHomeworkDue)
    }

    val uri: Uri = Uri.Builder()
            .scheme("content")
            .authority(BoardsContentProvider.authority)
            .path(path)
            .build();

    fun onCreate(db: SQLiteDatabase) {
        val sql = """CREATE TABLE ${Table.name}
            (
                ${Table.columnDate} INTEGER,
                ${Table.columnBoardId} INTEGER,
                ${Table.columnTopic} TEXT,
                ${Table.columnDuration} INTEGER,
                ${Table.columnStatus} INTEGER,
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

    fun uriWithBoard(id: Long): Uri {
        return Uri.withAppendedPath(BoardsContract.uriWithId(id), path)
    }
    fun uriWithId(id: Long): Uri {
        return Uri.withAppendedPath(uri, id.toString())
    }
}
