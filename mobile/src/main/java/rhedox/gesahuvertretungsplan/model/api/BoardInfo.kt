package rhedox.gesahuvertretungsplan.model.api

import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.model.database.entity.Mark

/**
 * Created by robin on 11.10.2016.
 */
data class BoardInfo(val board: Board, val lessons: List<Lesson>, val marks: List<Mark>)
