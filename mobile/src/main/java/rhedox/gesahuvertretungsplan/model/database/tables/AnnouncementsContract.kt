package rhedox.gesahuvertretungsplan.model.database.tables

import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import org.joda.time.LocalDate
import rhedox.gesahuvertretungsplan.model.database.SubstitutesContentProvider
import rhedox.gesahuvertretungsplan.model.unixTimeStamp

/**
 * Created by robin on 19.10.2016.
 */
object AnnouncementsContract {
    const val datePath = "date"
    const val path = "announcements"
    val uri: Uri = Uri.Builder()
            .scheme("content")
            .authority(SubstitutesContentProvider.authority)
            .path(path)
            .build();
    val dateUri: Uri = Uri.withAppendedPath(uri, datePath)

    object Table {
        const val name = "announcements";
        const val columnId = "id";
        const val columnDate = "date"
        const val columnText = "text";

        val columns = setOf(
                columnId, columnDate, columnText)
    }

    fun onCreate(db: SQLiteDatabase) {
        val sql = """CREATE TABLE ${Table.name}
        (
            ${Table.columnId} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${Table.columnDate} INTEGER,
            ${Table.columnText} TEXT
        );
        """;
        db.execSQL(sql);
    }

    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }

    fun clear(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM ${Table.name} WHERE 1;");
    }

    fun uriWithDate(date: LocalDate): Uri {
        return Uri.withAppendedPath(dateUri, date.unixTimeStamp.toString())
    }
    fun uriWithSeconds(seconds: Int): Uri {
        return Uri.withAppendedPath(dateUri, seconds.toString())
    }
    fun uriWithId(id: Long): Uri {
        return Uri.withAppendedPath(uri, id.toString())
    }
}
