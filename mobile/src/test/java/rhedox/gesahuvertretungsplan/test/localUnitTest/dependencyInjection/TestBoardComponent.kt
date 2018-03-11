package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import dagger.Subcomponent
import rhedox.gesahuvertretungsplan.dependencyInjection.BoardsComponent
import rhedox.gesahuvertretungsplan.dependencyInjection.PresenterScope

/**
 * Created by robin on 10.03.2018.
 */
@PresenterScope
@Subcomponent(modules = [TestBoardModule::class])
internal interface TestBoardComponent: BoardsComponent {
}