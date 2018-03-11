package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import rhedox.gesahuvertretungsplan.dependencyInjection.PresenterScope
import rhedox.gesahuvertretungsplan.model.SubstituteFormatter
import rhedox.gesahuvertretungsplan.model.SubstitutesRepository
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.dao.AnnouncementsDao
import rhedox.gesahuvertretungsplan.model.database.dao.SubstitutesDao
import rhedox.gesahuvertretungsplan.model.database.dao.SupervisionsDao
import rhedox.gesahuvertretungsplan.test.localUnitTest.repository.SubstitutesTestRepository

/**
 * Created by robin on 10.03.2018.
 */
@Module
internal open class TestSubstitutesModule {
    @PresenterScope
    @Provides
    internal fun provideRepository(): SubstitutesRepository = SubstitutesTestRepository()

    @PresenterScope
    @Provides
    internal fun provideSubstitutesDao() = mock(SubstitutesDao::class.java)

    @PresenterScope
    @Provides
    internal fun provideSupervisionsDao() = mock(SupervisionsDao::class.java)

    @PresenterScope
    @Provides
    internal fun provideAnnouncementsDao() = mock(AnnouncementsDao::class.java)

    @PresenterScope
    @Provides
    internal fun provideSyncObserver() = mock(SyncObserver::class.java)

    @PresenterScope
    @Provides
    internal fun provideFormatter() = mock(SubstituteFormatter::class.java)
}