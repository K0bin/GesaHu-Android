package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.database.BoardsDatabase
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabase
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import rhedox.gesahuvertretungsplan.util.PermissionManager
import javax.inject.Singleton

/**
 * Created by robin on 10.03.2018.
 */
@Module
internal open class TestAppModule {
    @Provides
    @Singleton
    internal fun provideContext() = mock(Context::class.java)

    @Provides
    @Singleton
    internal fun provideSharedPreferences(): SharedPreferences {
        val prefs = mock(SharedPreferences::class.java)
        `when`(prefs.getInt(PreferenceFragment.PREF_VERSION, 0)).thenReturn(BuildConfig.VERSION_CODE)
        return prefs
    }

    @Provides
    @Singleton
    internal fun provideAccountManager() = mock(AccountManager::class.java)

    @Provides
    @Singleton
    internal fun providePermissionManager() = mock(PermissionManager::class.java)

    @Provides
    @Singleton
    internal fun provideConnectivityManager() = mock(ConnectivityManager::class.java)

    @Provides
    @Singleton
    internal fun provideSubstituteDatabase() = mock(SubstitutesDatabase::class.java)

    @Provides
    @Singleton
    internal fun provideBoardsDatabase() = mock(BoardsDatabase::class.java)

    @Provides
    @Singleton
    internal fun provideApi() = mock(GesaHu::class.java)
}