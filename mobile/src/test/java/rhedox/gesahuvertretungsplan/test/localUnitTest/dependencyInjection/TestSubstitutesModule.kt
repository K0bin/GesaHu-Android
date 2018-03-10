package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import dagger.Module
import dagger.Provides
import io.mockk.mockk
import rhedox.gesahuvertretungsplan.model.SubstituteFormatter
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.SubstitutesRepository
import rhedox.gesahuvertretungsplan.model.database.dao.AnnouncementsDao
import rhedox.gesahuvertretungsplan.model.database.dao.SubstitutesDao
import rhedox.gesahuvertretungsplan.model.database.dao.SupervisionsDao

/**
 * Created by robin on 10.03.2018.
 */
@Module
internal open class TestSubstitutesModule {
    @Provides
    internal fun provideRepository() = mockk<SubstitutesRepository>()

    @Provides
    internal fun provideSubstitutesDao(): SubstitutesDao = mockk()

    @Provides
    internal fun provideSupervisionsDao(): SupervisionsDao = mockk()

    @Provides
    internal fun provideAnnouncementsDao(): AnnouncementsDao = mockk()

    @Provides
    internal fun provideSyncObserver(): SyncObserver = mockk()

    @Provides
    internal fun provideFormatter(): SubstituteFormatter = mockk()
}