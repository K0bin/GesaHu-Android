package rhedox.gesahuvertretungsplan.test.local_unit_test.presenter

import rhedox.gesahuvertretungsplan.model.database.entity.Lesson
import rhedox.gesahuvertretungsplan.mvp.LessonsContract

/**
 * Created by robin on 01.02.2017.
 */
class StubLessonsView : LessonsContract.View {
    var list = listOf<Lesson>()
    private set

    override fun showList(list: List<Lesson>) {
        this.list = list
    }

    override var lessonsTotal: Int = 0
    override var lessonsMissed: Int = 0
    override var lessonsMissedWithSickNote: Int = 0
}