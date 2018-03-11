package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import android.content.SharedPreferences
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

    fun prefs(): SharedPreferences

    companion object {
        fun create(): TestAppComponent = DaggerTestAppComponent.create()
    }
}