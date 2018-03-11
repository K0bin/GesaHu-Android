package rhedox.gesahuvertretungsplan.dependencyInjection

import android.content.Context
import dagger.Module
import dagger.Provides
import rhedox.gesahuvertretungsplan.model.SubstituteFormatter
import rhedox.gesahuvertretungsplan.model.SubstitutesRepository
import rhedox.gesahuvertretungsplan.model.SyncObserver
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabase
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabaseRepository
import rhedox.gesahuvertretungsplan.model.database.dao.AnnouncementsDao
import rhedox.gesahuvertretungsplan.model.database.dao.SubstitutesDao
import rhedox.gesahuvertretungsplan.model.database.dao.SupervisionsDao

/**
 * Created by robin on 09.03.2018.
 */
@Module
public class SubstitutesModule {
    @Provides
    @PresenterScope
    internal fun provideSubstitutesDao(substitutesDB: SubstitutesDatabase): SubstitutesDao = substitutesDB.substitutes

    @Provides
    @PresenterScope
    internal fun provideSupervisionsDao(substitutesDB: SubstitutesDatabase): SupervisionsDao = substitutesDB.supervisions

    @Provides
    @PresenterScope
    internal fun provideAnnouncementsDao(substitutesDB: SubstitutesDatabase): AnnouncementsDao = substitutesDB.announcements

    @Provides
    @PresenterScope
    internal fun provideSyncObserver(): SyncObserver = SyncObserver()

    @Provides
    @PresenterScope
    internal fun provideFormatter(context: Context): SubstituteFormatter = SubstituteFormatter(context)

    @Provides
    @PresenterScope
    internal fun provideepository(repository: SubstitutesDatabaseRepository): SubstitutesRepository = repository
}