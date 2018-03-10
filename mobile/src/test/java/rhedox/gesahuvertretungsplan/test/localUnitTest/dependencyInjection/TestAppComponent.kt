package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import dagger.Component
import javax.inject.Singleton

/**
 * Created by robin on 10.03.2018.
 */
@Singleton
@Component(modules = [TestAppModule::class])
internal interface TestAppComponent {
    fun boardComponent(): TestBoardComponent
    fun substitutesComponent(): TestSubstitutesComponent

    companion object {
        fun create(): TestAppComponent = DaggerTestAppComponent.create()
    }
}