package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import rhedox.gesahuvertretungsplan.model.database.BoardsContentProvider
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider

/**
 * Created by robin on 30.10.2016.
 */
@Suppress("unused")
object MarksContract {
    const val path = "marks"

    object Table {
        const val name = "marks";
        const val columnId = "rowid";
        const val columnBoardId = "boardId";
        const val columnDate = "date"
        const val columnDescription = "description"
        const val columnKind = "kind"
        const val columnMark = "mark"
        const val columnAverage = "average"
        const val columnMarkKind = "markKind"
        const val columnLogo = "logo"
        const val columnWeighting = "weighting"

        val columns = setOf(
                columnId, columnBoardId, columnDate, columnDescription, columnKind, columnMark, columnAverage, columnMarkKind, columnLogo, columnWeighting)
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
                ${Table.columnDescription} TEXT,
                ${Table.columnKind} TEXT,
                ${Table.columnMark} TEXT,
                ${Table.columnAverage} REAL,
                ${Table.columnMarkKind} INTEGER,
                ${Table.columnLogo} TEXT,
                ${Table.columnWeighting} REAL,
                FOREIGN KEY(${Table.columnBoardId}) REFERENCES ${BoardsContract.Table.name}(${BoardsContract.Table.columnId})
            );
            """;
        db.execSQL(sql);
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (newVersion >= 6 && oldVersion < 6) {
            db.execSQL("ALTER TABLE ${Table.name} RENAME TO ${Table.name}_old")
            onCreate(db)
            db.execSQL("INSERT INTO ${Table.name} (${Table.columnId}, ${Table.columnBoardId}, ${Table.columnDate}, ${Table.columnDescription}, ${Table.columnKind}, ${Table.columnMark}, ${Table.columnAverage}, ${Table.columnMarkKind}, ${Table.columnLogo}, ${Table.columnWeighting}) " +
                    "SELECT ${Table.columnId}, ${Table.columnBoardId}, ${Table.columnDate}, ${Table.columnDescription}, ${Table.columnKind}, ${Table.columnMark}, ${Table.columnAverage}, ${Table.columnMarkKind}, ${Table.columnLogo}, ${Table.columnWeighting} " +
                    "FROM ${Table.name}_old")

            db.execSQL("DROP TABLE ${Table.name}_old")
        }
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
