package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.content.ContentResolver
import android.os.Bundle
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.api.mockito.PowerMockito.`when`
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareEverythingForTest
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter

/**
 * Created by robin on 22.12.2016.
 */
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
//@RunWith(PowerMockRunner::class)
@PowerMockRunnerDelegate(RobolectricTestRunner::class)
@PowerMockIgnore("org.mockito.*", "org.robolectric.*", "android.*")
@PrepareForTest(ContentResolver::class)
class SubstitutesPresenterTest {
    @Test
    fun test() {
        //PowerMockito.mockStatic(ContentResolver::class.java);
        //`when`(ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, {})).then {  }

        val presenter = SubstitutesPresenter(RuntimeEnvironment.application, Bundle.EMPTY)


    }
}