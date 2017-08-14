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
        const val version = 7
    }

    override fun onCreate(db: SQLiteDatabase) {
        SubstitutesContract.onCreate(db)
        AnnouncementsContract.onCreate(db)
        SupervisionsContract.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(newVersion >= 1 && oldVersion < 1) {
            SubstitutesContract.onCreate(db);
            AnnouncementsContract.onCreate(db)
        } else if (newVersion >= 7 && oldVersion < 7) {
            SupervisionsContract.onCreate(db)
        } else {
            SubstitutesContract.onUpgrade(db, oldVersion, newVersion);
            AnnouncementsContract.onUpgrade(db, oldVersion, newVersion)
            SupervisionsContract.onUpgrade(db, oldVersion, newVersion)
        }
    }
}