package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import dagger.Module
import dagger.Provides
import io.mockk.mockk
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.dao.BoardsDao
import rhedox.gesahuvertretungsplan.model.database.dao.LessonsDao
import rhedox.gesahuvertretungsplan.model.database.dao.MarksDao

/**
 * Created by robin on 10.03.2018.
 */
@Module
internal open class TestBoardModule {
    @Provides
    internal fun provideRepository(): BoardsRepository = mockk()

    @Provides
    internal fun provideAvatarLoader(): AvatarLoader = mockk()

    @Provides
    internal fun provideBoardsDao(): BoardsDao = mockk()

    @Provides
    internal fun provideMarksDao(): MarksDao = mockk()

    @Provides
    internal fun provideLessonsDao(): LessonsDao = mockk()
}