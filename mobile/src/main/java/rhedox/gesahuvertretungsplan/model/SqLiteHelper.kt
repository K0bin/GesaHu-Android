package rhedox.gesahuvertretungsplan.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import rhedox.gesahuvertretungsplan.App

/**
 * Created by robin on 18.10.2016.
 */
class SqLiteHelper(context: Context) : SQLiteOpenHelper(context, App.DB_NAME, null, 1) {

    companion object {
        private const val TABLE_SUBSTITUTES = "substitutes";
    }

    override fun onCreate(db: SQLiteDatabase) {
        val sql = """CREATE TABLE $TABLE_SUBSTITUTES
        (
            lessonBegin INTEGER,
            lessonEnd INTEGER,
            subject TEXT,
            course TEXT,
            teacher TEXT,
            substitute TEXT,
            room TEXT,
            hint TEXT,
            isRelevant INTEGER
        );
        """;
        db.execSQL(sql);
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}