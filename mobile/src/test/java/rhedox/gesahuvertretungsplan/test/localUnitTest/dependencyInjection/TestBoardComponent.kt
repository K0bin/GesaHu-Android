package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import dagger.Subcomponent
import rhedox.gesahuvertretungsplan.dependencyInjection.BoardsComponent

/**
 * Created by robin on 10.03.2018.
 */
@Subcomponent(modules = [TestBoardModule::class])
internal interface TestBoardComponent: BoardsComponent {
}