package rhedox.gesahuvertretungsplan.test.localUnitTest.presenter

import android.content.ContentResolver
import android.os.Bundle
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import com.nhaarman.mockito_kotlin.mock
import org.junit.Test
import org.junit.runner.RunWith
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter

/**
 * Created by robin on 22.12.2016.
 */
//@RunWith(RobolectricTestRunner::class)
//@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
//@RunWith(PowerMockRunner::class)
//@PowerMockRunnerDelegate(RobolectricTestRunner::class)
//@PowerMockIgnore("org.mockito.*", "org.robolectric.*", "android.*")
//@PrepareForTest(ContentResolver::class)
class SubstitutesPresenterTest {
    val kodein = Kodein {
        bind<SubstitutesPresenter>() with instance(
            mock<SubstitutesPresenter> {

            }
        )
    }

    @Test
    fun test() {
        //PowerMockito.mockStatic(ContentResolver::class.java);
        //`when`(ContentResolver.addStatusChangeListener(ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE, {})).then {  }

        //val presenter = SubstitutesPresenter(RuntimeEnvironment.application, Bundle.EMPTY)


    }
}