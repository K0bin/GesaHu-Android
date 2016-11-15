package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.database.tables.AnnouncementsContract
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import rhedox.gesahuvertretungsplan.model.database.tables.SubstitutesContract

/**
 * Created by robin on 18.10.2016.
 */
class SqLiteHelper(context: Context) : SQLiteOpenHelper(context, name, null, version) {

    companion object {
        const val name = "gesahui.db"
        const val version = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        SubstitutesContract.onCreate(db);
        AnnouncementsContract.onCreate(db)
        BoardsContract.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(newVersion >= 2 && oldVersion < 2)
            BoardsContract.onCreate(db)

        if(newVersion >= 1 && oldVersion < 1) {
            SubstitutesContract.onCreate(db);
            AnnouncementsContract.onCreate(db)
        }

        SubstitutesContract.onUpgrade(db, oldVersion, newVersion);
        AnnouncementsContract.onUpgrade(db, oldVersion, newVersion)
        BoardsContract.onUpgrade(db, oldVersion, newVersion)
    }
}