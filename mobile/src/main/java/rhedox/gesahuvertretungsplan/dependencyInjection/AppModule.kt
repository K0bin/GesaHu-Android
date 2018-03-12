package rhedox.gesahuvertretungsplan.dependencyInjection

import android.accounts.AccountManager
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Build
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.api.GesaHu
import rhedox.gesahuvertretungsplan.model.database.BoardsDatabase
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabase
import rhedox.gesahuvertretungsplan.security.EncryptionHelper
import rhedox.gesahuvertretungsplan.security.EncryptionHelperJellyBean
import rhedox.gesahuvertretungsplan.security.EncryptionHelperMarshmallow
import rhedox.gesahuvertretungsplan.security.NoEncryptionHelper
import rhedox.gesahuvertretungsplan.util.accountManager
import rhedox.gesahuvertretungsplan.util.connectivityManager
import javax.inject.Singleton

/**
 * Created by robin on 09.03.2018.
 */
@Module()
internal open class AppModule {
    @Provides
    @Singleton
    internal fun provideContext(application: App): Context = application

    @Provides
    @Singleton
    internal fun provideSharedPreferences(context: Context): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @Singleton
    internal fun provideAccountManager(context: Context): AccountManager = context.accountManager

    @Provides
    @Singleton
    internal fun provideConnectivityManager(context: Context): ConnectivityManager = context.connectivityManager

    @Provides
    @Singleton
    internal fun provideApiClient(context: Context): GesaHu = GesaHu(context)

    @Provides
    @Singleton
    internal fun provideSubstitutesDatabase(context: Context): SubstitutesDatabase = SubstitutesDatabase.build(context)

    @Provides
    @Singleton
    internal fun provideBoardsDatabase(context: Context): BoardsDatabase = BoardsDatabase.build(context)

    @Provides
    @Singleton
    internal fun provideEncryption(context: Context): EncryptionHelper = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> EncryptionHelperMarshmallow()
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 -> EncryptionHelperJellyBean(context)
        else -> NoEncryptionHelper()
    }
}