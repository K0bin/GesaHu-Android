package rhedox.gesahuvertretungsplan.test.local_unit_test.dependency_injection

import dagger.Subcomponent
import rhedox.gesahuvertretungsplan.dependency_injection.BoardsComponent
import rhedox.gesahuvertretungsplan.dependency_injection.PresenterScope

/**
 * Created by robin on 10.03.2018.
 */
@PresenterScope
@Subcomponent(modules = [TestBoardModule::class])
internal interface TestBoardComponent: BoardsComponent
