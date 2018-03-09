package rhedox.gesahuvertretungsplan.model.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.Supervision
import rhedox.gesahuvertretungsplan.model.database.tables.SubstitutesContract


/**
 * Created by robin on 08.03.2018.
 */
@Database(entities = [(Substitute::class), (Supervision::class), (Announcement::class)], version = SubstitutesDatabase.version)
@TypeConverters(rhedox.gesahuvertretungsplan.model.database.TypeConverters::class)
abstract class SubstitutesDatabase: RoomDatabase() {
    public abstract val substitutes: SubstitutesDao
    public abstract val supervisions: SupervisionsDao
    public abstract val announcements: AnnouncementsDao

    companion object {
        const val name = "gesahui_substitutes.db"
        const val version = 8

        val migration7_8 = object: Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //Migrate Substitutes to Room
                db.execSQL("ALTER TABLE ${Substitute.tableName} RENAME TO ${Substitute.tableName}_old;")
                db.execSQL("CREATE TABLE ${Substitute.tableName} " +
                        "(id LONG PRIMARY KEY," +
                        "date INTEGER NOT NULL, " +
                        "lesson_begin INTEGER NOT NULL," +
                        "duration INTEGER NOT NULL," +
                        "subject TEXT NOT NULL," +
                        "course TEXT NOT NULL," +
                        "teacher TEXT NOT NULL," +
                        "substitute TEXT NOT NULL," +
                        "room TEXT NOT NULL," +
                        "hint TEXT NOT NULL" +
                        "isRelevant INTEGER NOT NULL);");

                db.execSQL("INSERT INTO ${Substitute.tableName} (id, date, lessonBegin, duration, subject, course, teacher, substitute, room, hint, isRelevant) " +
                        "SELECT id, date, lessonBegin, duration, subject, course, teacher, substitute, room, hint, isRelevant " +
                        "FROM ${Substitute.tableName}_old;")
                db.execSQL("DROP TABLE ${Substitute.tableName}_old;")

                //Migrate Announcements to Room
                db.execSQL("ALTER TABLE ${Announcement.tableName} RENAME TO ${Announcement.tableName}_old;")
                db.execSQL("CREATE TABLE ${Announcement.tableName} " +
                        "(id LONG PRIMARY KEY," +
                        "date INTEGER NOT NULL, " +
                        "text Text NOT NULL);");

                db.execSQL("INSERT INTO ${Announcement.tableName} (id, date, text) " +
                        "SELECT id, date, text " +
                        "FROM ${Announcement.tableName}_old;")
                db.execSQL("DROP TABLE ${Announcement.tableName}_old;")

                //Migrate Supervisions to Room
                db.execSQL("ALTER TABLE ${Supervision.tableName} RENAME TO ${Supervision.tableName}_old;")
                db.execSQL("CREATE TABLE ${Supervision.tableName} " +
                        "(id LONG PRIMARY KEY," +
                        "date INTEGER NOT NULL, " +
                        "teacher TEXT NOT NULL," +
                        "substitute TEXT NOT NULL," +
                        "location TEXT NOT NULL," +
                        "isRelevant INTEGER NOT NULL);");

                db.execSQL("INSERT INTO ${Supervision.tableName} (id, date, teacher, substitute, location, isRelevant) " +
                        "SELECT id, date, teacher, substitute, location, isRelevant " +
                        "FROM ${Supervision.tableName}_old;")
                db.execSQL("DROP TABLE ${Supervision.tableName}_old;")
            }
        }
    }
}