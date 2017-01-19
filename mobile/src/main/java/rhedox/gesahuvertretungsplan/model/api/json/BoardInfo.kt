package rhedox.gesahuvertretungsplan.model.api.json

import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.Lesson
import rhedox.gesahuvertretungsplan.model.database.Mark

/**
 * Created by robin on 11.10.2016.
 */
data class BoardInfo(val board: Board, val lessons: List<Lesson>, val marks: List<Mark>)
