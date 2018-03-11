package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import dagger.Module
import dagger.Provides
import io.mockk.mockk
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.database.BoardsDatabase
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabase
import rhedox.gesahuvertretungsplan.util.PermissionManager
import javax.inject.Singleton

/**
 * Created by robin on 10.03.2018.
 */
@Module
internal open class TestAppModule {
    @Provides
    @Singleton
    internal fun provideContext() = mockk<Context>(relaxed = true)

    @Provides
    @Singleton
    internal fun provideSharedPreferences(): SharedPreferences = mockk(relaxed = true)

    @Provides
    @Singleton
    internal fun provideAccountManager(): AccountManager = mockk(relaxed = true)

    @Provides
    @Singleton
    internal fun providePermissionManager(): PermissionManager = mockk(relaxed = true)

    @Provides
    @Singleton
    internal fun provideConnectivityManager(): ConnectivityManager = mockk(relaxed = true)

    @Provides
    @Singleton
    internal fun provideSubstituteDatabase() = mockk<SubstitutesDatabase>(relaxed = true)

    @Provides
    @Singleton
    internal fun provideBoardsDatabase() = mockk<BoardsDatabase>(relaxed = true)

    @Provides
    @Singleton
    internal fun provideApi() = mockk<GesaHu>(relaxed = true)
}