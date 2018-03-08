package rhedox.gesahuvertretungsplan.model.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import rhedox.gesahuvertretungsplan.model.Substitute
import rhedox.gesahuvertretungsplan.model.Supervision


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
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }
    }
}