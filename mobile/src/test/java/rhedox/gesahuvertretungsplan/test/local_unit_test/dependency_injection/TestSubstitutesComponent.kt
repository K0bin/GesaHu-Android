package rhedox.gesahuvertretungsplan.test.local_unit_test.dependency_injection

import dagger.Subcomponent
import rhedox.gesahuvertretungsplan.dependency_injection.PresenterScope
import rhedox.gesahuvertretungsplan.dependency_injection.SubstitutesComponent

/**
 * Created by robin on 10.03.2018.
 */
@PresenterScope
@Subcomponent(modules = [TestSubstitutesModule::class])
internal interface TestSubstitutesComponent: SubstitutesComponent
