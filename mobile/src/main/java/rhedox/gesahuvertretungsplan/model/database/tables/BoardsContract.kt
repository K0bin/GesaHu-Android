package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider

/**
 * Created by robin on 30.10.2016.
 */
object BoardsContract {
    const val avatarFileName = "avatar.jpg";
    const val namePath = "name"

    object Table {
        const val name = "boards";
        const val columnId = "ROWID";
        const val columnName = "name"
        const val columnMark = "mark"
        const val columnMarkRemark = "markRemark"
        const val columnLessonsTotal = "lessonsTotal"
        const val columnMissedLessons = "missedLessons"
        const val columnMissedLessonsWithSickNotes = "missedLessonsWithSickNotes"

        val columns = setOf(
                columnId, columnName, columnMark, columnMarkRemark, columnLessonsTotal, columnMissedLessons, columnMissedLessonsWithSickNotes)
    }

    val uri: Uri = Uri.Builder()
            .scheme("content")
            .authority(BoardsContentProvider.authority)
            .build();
    val nameUri: Uri = Uri.withAppendedPath(uri, namePath)

    fun onCreate(db: SQLiteDatabase) {
        val sql = """CREATE TABLE ${Table.name}
            (
                ${Table.columnName} TEXT UNIQUE,
                ${Table.columnMark} INTEGER,
                ${Table.columnMarkRemark} TEXT,
                ${Table.columnLessonsTotal} INTEGER,
                ${Table.columnMissedLessons} INTEGER,
                ${Table.columnMissedLessonsWithSickNotes} INTEGER
            );
            """;
        db.execSQL(sql);
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(newVersion >= 4 && oldVersion < 3) {
            db.execSQL("ALTER TABLE ${Table.name} RENAME TO ${Table.name}_old")
            onCreate(db)
            db.execSQL("INSERT INTO ${Table.name} (${Table.columnId}, ${Table.columnName}) " +
                    "SELECT ${Table.columnId}, ${Table.columnName} " +
                    "FROM ${Table.name}_old")

            db.execSQL("DROP TABLE ${Table.name}_old")
        }
    }

    fun clear(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM ${BoardsContract.Table.name} WHERE 1;");
    }

    fun uriWithId(id: Long): Uri {
        return Uri.withAppendedPath(uri, id.toString())
    }
    fun uriWithName(name: String): Uri {
        return Uri.withAppendedPath(uri, name)
    }
}
