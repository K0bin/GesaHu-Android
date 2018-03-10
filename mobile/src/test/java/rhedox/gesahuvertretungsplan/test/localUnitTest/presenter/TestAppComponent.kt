package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import dagger.Component
import rhedox.gesahuvertretungsplan.dependencyInjection.AppComponent

/**
 * Created by robin on 10.03.2018.
 */
@Component(modules = [TestAppModule::class])
class TestAppComponent: AppComponent {
}