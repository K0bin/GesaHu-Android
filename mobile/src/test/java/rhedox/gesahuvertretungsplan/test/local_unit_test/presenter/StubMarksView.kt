package rhedox.gesahuvertretungsplan.test.local_unit_test.presenter

import rhedox.gesahuvertretungsplan.model.database.entity.Mark
import rhedox.gesahuvertretungsplan.mvp.MarksContract

/**
 * Created by robin on 01.02.2017.
 */
class StubMarksView : MarksContract.View {
    var list = listOf<Mark>()
    private set

    override fun showList(list: List<Mark>) {
        this.list = list
    }
    override var mark: String = ""
}