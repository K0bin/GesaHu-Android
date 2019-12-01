package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import rhedox.gesahuvertretungsplan.model.database.dao.AnnouncementsDao
import rhedox.gesahuvertretungsplan.model.database.dao.SubstitutesDao
import rhedox.gesahuvertretungsplan.model.database.dao.SupervisionsDao
import rhedox.gesahuvertretungsplan.model.database.entity.Announcement
import rhedox.gesahuvertretungsplan.model.database.entity.Substitute
import rhedox.gesahuvertretungsplan.model.database.entity.Supervision


/**
 * Created by robin on 08.03.2018.
 */
@Database(entities = [(Substitute::class), (Supervision::class), (Announcement::class)], version = SubstitutesDatabase.version)
@TypeConverters(LocalDateConverter::class)
abstract class SubstitutesDatabase: RoomDatabase() {
    abstract val substitutes: SubstitutesDao
    abstract val supervisions: SupervisionsDao
    abstract val announcements: AnnouncementsDao

    companion object {
        const val name = "gesahui_substitutes.db"
        const val version = 9

        fun build(context: Context): SubstitutesDatabase = Room.databaseBuilder(context, SubstitutesDatabase::class.java, name)
                    .fallbackToDestructiveMigration()
                    .addMigrations(migration7_8, migration8_9)
                    .build()

        private val migration7_8 = object: Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //Migrate Substitutes to Room
                db.execSQL("ALTER TABLE ${Substitute.tableName} RENAME TO ${Substitute.tableName}_old;")
                db.execSQL("CREATE TABLE ${Substitute.tableName} " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date INTEGER NOT NULL," +
                        "lessonBegin INTEGER NOT NULL," +
                        "duration INTEGER NOT NULL," +
                        "subject TEXT NOT NULL," +
                        "course TEXT NOT NULL," +
                        "teacher TEXT NOT NULL," +
                        "substitute TEXT NOT NULL," +
                        "room TEXT NOT NULL," +
                        "hint TEXT NOT NULL," +
                        "isRelevant INTEGER NOT NULL);")

                db.execSQL("INSERT INTO ${Substitute.tableName} (id, date, lessonBegin, duration, subject, course, teacher, substitute, room, hint, isRelevant) " +
                        "SELECT rowid, date, lessonBegin, duration, subject, course, teacher, substitute, room, hint, isRelevant " +
                        "FROM ${Substitute.tableName}_old;")
                db.execSQL("DROP TABLE ${Substitute.tableName}_old;")

                //Migrate Announcements to Room
                db.execSQL("ALTER TABLE ${Announcement.tableName} RENAME TO ${Announcement.tableName}_old;")
                db.execSQL("CREATE TABLE ${Announcement.tableName} " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date INTEGER NOT NULL," +
                        "text Text NOT NULL);")

                db.execSQL("INSERT INTO ${Announcement.tableName} (id, date, text) " +
                        "SELECT rowid, date, text " +
                        "FROM ${Announcement.tableName}_old;")
                db.execSQL("DROP TABLE ${Announcement.tableName}_old;")

                //Migrate Supervisions to Room
                db.execSQL("ALTER TABLE ${Supervision.tableName} RENAME TO ${Supervision.tableName}_old;")
                db.execSQL("CREATE TABLE ${Supervision.tableName} " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date INTEGER NOT NULL," +
                        "time TEXT NOT NULL," +
                        "teacher TEXT NOT NULL," +
                        "substitute TEXT NOT NULL," +
                        "location TEXT NOT NULL," +
                        "isRelevant INTEGER NOT NULL);")

                db.execSQL("INSERT INTO ${Supervision.tableName} (id, date, time, teacher, substitute, location, isRelevant) " +
                        "SELECT rowid, date, time, teacher, substitute, location, isRelevant " +
                        "FROM ${Supervision.tableName}_old;")
                db.execSQL("DROP TABLE ${Supervision.tableName}_old;")
            }
        }

        private val migration8_9 = object: Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // I dont think anything changed but Room is acting up
            }
        }
    }
}