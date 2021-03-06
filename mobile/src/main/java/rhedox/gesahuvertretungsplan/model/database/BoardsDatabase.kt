package rhedox.gesahuvertretungsplan.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    abstract val boards: BoardsDao
    abstract val lessons: LessonsDao
    abstract val marks: MarksDao

    companion object {
        const val name = "gesahui_boards.db"
        const val version = 8

        fun build(context: Context): BoardsDatabase = Room.databaseBuilder(context, BoardsDatabase::class.java, name)
                .fallbackToDestructiveMigration()
                .addMigrations(migration6_7, migration7_8)
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
                        "missedLessonsWithSickNotes INTEGER NOT NULL);")

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
                        "FOREIGN KEY(boardName) REFERENCES ${Board.tableName}(name) ON DELETE CASCADE ON UPDATE CASCADE);")
                db.execSQL("CREATE INDEX lessonBoardName ON ${Lesson.tableName} (boardName)")

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
                        "FOREIGN KEY(boardName) REFERENCES ${Board.tableName}(name) ON DELETE CASCADE ON UPDATE CASCADE);")
                db.execSQL("CREATE INDEX markBoardName ON ${Mark.tableName} (boardName)")

                db.execSQL("INSERT INTO ${Mark.tableName} (id, date, boardName, description, kind, mark, average, markKind, logo, weighting) " +
                        "SELECT rowid, date, (SELECT name FROM ${Board.tableName} WHERE rowid = boardId), description, kind, mark, average, markKind, logo, weighting " +
                        "FROM ${Mark.tableName}_old;")
                db.execSQL("DROP TABLE ${Mark.tableName}_old;")

                //Finalize boards migration
                db.execSQL("DROP TABLE ${Board.tableName};")
                db.execSQL("ALTER TABLE ${Board.tableName}_new RENAME TO ${Board.tableName};")
            }
        }

        private val migration7_8 = object: Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // I dont think anything changed but Room is acting up
            }
        }
    }
}