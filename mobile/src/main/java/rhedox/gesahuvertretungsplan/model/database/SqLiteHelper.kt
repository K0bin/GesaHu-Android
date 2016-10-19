package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.database.tables.Announcements
import rhedox.gesahuvertretungsplan.model.database.tables.Substitutes

/**
 * Created by robin on 18.10.2016.
 */
class SqLiteHelper(context: Context) : SQLiteOpenHelper(context, name, null, version) {

    companion object {
        const val name = "gesahui.db"
        const val version = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        Substitutes.onCreate(db);
        Announcements.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Substitutes.onUpgrade(db, oldVersion, newVersion);
        Announcements.onUpgrade(db, oldVersion, newVersion)
    }
}