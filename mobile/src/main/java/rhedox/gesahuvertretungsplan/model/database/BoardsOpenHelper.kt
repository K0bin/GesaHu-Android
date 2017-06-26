package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.database.tables.*

/**
 * Created by robin on 18.10.2016.
 */
class BoardsOpenHelper(context: Context) : SQLiteOpenHelper(context, name, null, version) {

    companion object {
        const val name = "gesahui_boards.db"
        const val version = 6
    }

    override fun onCreate(db: SQLiteDatabase) {
        BoardsContract.onCreate(db)
        LessonsContract.onCreate(db)
        MarksContract.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if(newVersion >= 5 && oldVersion < 4) {
            LessonsContract.onCreate(db)
            MarksContract.onCreate(db)
        } else {
            LessonsContract.onUpgrade(db, oldVersion, newVersion)
            MarksContract.onUpgrade(db, oldVersion, newVersion)
        }

        if(newVersion >= 2 && oldVersion < 2) {
            BoardsContract.onCreate(db)
        } else {
            BoardsContract.onUpgrade(db, oldVersion, newVersion)
        }
    }
}