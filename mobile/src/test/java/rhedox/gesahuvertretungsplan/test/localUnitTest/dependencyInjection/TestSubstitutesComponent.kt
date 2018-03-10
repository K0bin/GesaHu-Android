package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import dagger.Subcomponent
import rhedox.gesahuvertretungsplan.dependencyInjection.SubstitutesComponent

/**
 * Created by robin on 10.03.2018.
 */
@Subcomponent(modules = [TestSubstitutesModule::class])
internal interface TestSubstitutesComponent: SubstitutesComponent {
}