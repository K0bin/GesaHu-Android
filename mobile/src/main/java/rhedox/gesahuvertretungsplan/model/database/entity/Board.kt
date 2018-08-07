package rhedox.gesahuvertretungsplan.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by robin on 14.01.2017.
 */
@Entity(tableName = Board.tableName)
data class Board(@PrimaryKey(autoGenerate = false) val name: String,
                 val mark: String?,
                 val markRemark: String,
                 val missedLessons: Int,
                 val missedLessonsWithSickNotes: Int,
                 val lessonsTotal: Int) {

    companion object {
        const val tableName = "boards"
        const val avatarFileName = "avatar.jpg";
    }
}
