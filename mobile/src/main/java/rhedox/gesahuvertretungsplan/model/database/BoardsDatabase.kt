package rhedox.gesahuvertretungsplan.model.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.arch.persistence.room.migration.Migration
import android.content.Context
import rhedox.gesahuvertretungsplan.model.database.dao.BoardsDao
import rhedox.gesahuvertretungsplan.model.database.dao.LessonsDao
import rhedox.gesahuvertretungsplan.model.database.dao.MarksDao
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.model.database.entity.Mark

/**
 * Created by robin on 08.03.2018.
 */
@Database(entities = [(Lesson::class), (Mark::class), (Board::class)], version = BoardsDatabase.version)
@TypeConverters(LocalDateConverter::class)
abstract class BoardsDatabase: RoomDatabase() {
    public abstract val boards: BoardsDao
    public abstract val lessons: LessonsDao
    public abstract val marks: MarksDao

    companion object {
        const val name = "gesahui_boards.db"
        const val version = 7

        fun build(context: Context): BoardsDatabase = Room.databaseBuilder(context, BoardsDatabase::class.java, BoardsDatabase.name)
                /*.fallbackToDestructiveMigration()*/
                .addMigrations(BoardsDatabase.migration6_7)
                .build()

        private val migration6_7 = object: Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                //Migrate Boards to Room
                db.execSQL("CREATE TABLE ${Board.tableName}_new " +
                        "(name TEXT NOT NULL PRIMARY KEY," +
                        "mark TEXT," +
                        "markRemark TEXT NOT NULL," +
                        "lessonsTotal INTEGER NOT NULL," +
                        "missedLessons INTEGER NOT NULL," +
                        "missedLessonsWithSickNotes INTEGER NOT NULL);");

                db.execSQL("INSERT INTO ${Board.tableName}_new (name, mark, markRemark, lessonsTotal, missedLessons, missedLessonsWithSickNotes) " +
                        "SELECT name, mark, markRemark, lessonsTotal, missedLessons, missedLessonsWithSickNotes " +
                        "FROM ${Board.tableName};")

                //Migrate Lessons to Room
                db.execSQL("ALTER TABLE ${Lesson.tableName} RENAME TO ${Lesson.tableName}_old;")
                db.execSQL("CREATE TABLE ${Lesson.tableName} " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date INTEGER NOT NULL," +
                        "boardName TEXT NOT NULL," +
                        "topic TEXT NOT NULL," +
                        "duration INTEGER NOT NULL," +
                        "status INTEGER NOT NULL," +
                        "homework TEXT," +
                        "homeworkDue INTEGER," +
                        "FOREIGN KEY(boardName) REFERENCES ${Board.tableName}(name) ON DELETE CASCADE ON UPDATE CASCADE);");

                db.execSQL("INSERT INTO ${Lesson.tableName} (id, date, boardName, topic, duration, status, homework, homeworkDue) " +
                        "SELECT rowid, date, (SELECT name FROM ${Board.tableName} WHERE rowid = boardId), topic, duration, status, homework, homeworkDue " +
                        "FROM ${Lesson.tableName}_old;")
                db.execSQL("DROP TABLE ${Lesson.tableName}_old;")


                //Migrate Marks to Room
                db.execSQL("ALTER TABLE ${Mark.tableName} RENAME TO ${Mark.tableName}_old;")
                db.execSQL("CREATE TABLE ${Mark.tableName} " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "date INTEGER NOT NULL, " +
                        "boardName TEXT NOT NULL," +
                        "description TEXT NOT NULL," +
                        "kind TEXT NOT NULL," +
                        "mark TEXT," +
                        "average REAL," +
                        "markKind INTEGER NOT NULL," +
                        "logo TEXT NOT NULL," +
                        "weighting REAL," +
                        "FOREIGN KEY(boardName) REFERENCES ${Board.tableName}(name) ON DELETE CASCADE ON UPDATE CASCADE);");

                db.execSQL("INSERT INTO ${Mark.tableName} (id, date, boardName, description, kind, mark, average, markKind, logo, weighting) " +
                        "SELECT rowid, date, (SELECT name FROM ${Board.tableName} WHERE rowid = boardId), description, kind, mark, average, markKind, logo, weighting " +
                        "FROM ${Mark.tableName}_old;")
                db.execSQL("DROP TABLE ${Mark.tableName}_old;")

                //Finalize boards migration
                db.execSQL("DROP TABLE ${Board.tableName};")
                db.execSQL("ALTER TABLE ${Board.tableName}_new RENAME TO ${Board.tableName};")
            }
        }
    }
}