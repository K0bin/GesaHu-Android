package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.database.tables.*

/**
 * Created by robin on 18.10.2016.
 */
class SubstitutesOpenHelper(context: Context) : SQLiteOpenHelper(context, name, null, version) {

    companion object {
        const val name = "gesahui_substitutes.db"
        const val version = 6
    }

    override fun onCreate(db: SQLiteDatabase) {
        SubstitutesContract.onCreate(db)
        AnnouncementsContract.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(newVersion >= 1 && oldVersion < 1) {
            SubstitutesContract.onCreate(db);
            AnnouncementsContract.onCreate(db)
        } else {
            SubstitutesContract.onUpgrade(db, oldVersion, newVersion);
            AnnouncementsContract.onUpgrade(db, oldVersion, newVersion)
        }
    }
}