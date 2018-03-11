package rhedox.gesahuvertretungsplan.test.localUnitTest.dependencyInjection

import dagger.Module
import dagger.Provides
import io.mockk.mockk
import rhedox.gesahuvertretungsplan.dependencyInjection.PresenterScope
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.dao.BoardsDao
import rhedox.gesahuvertretungsplan.model.database.dao.LessonsDao
import rhedox.gesahuvertretungsplan.model.database.dao.MarksDao
import rhedox.gesahuvertretungsplan.test.localUnitTest.repository.BoardsTestRepository

/**
 * Created by robin on 10.03.2018.
 */
@Module
internal open class TestBoardModule {
    @PresenterScope
    @Provides
    internal fun provideRepository(): BoardsRepository = BoardsTestRepository()

    @PresenterScope
    @Provides
    internal fun provideAvatarLoader(): AvatarLoader = mockk(relaxed = true)

    @PresenterScope
    @Provides
    internal fun provideBoardsDao(): BoardsDao = mockk(relaxed = true)

    @PresenterScope
    @Provides
    internal fun provideMarksDao(): MarksDao = mockk(relaxed = true)

    @PresenterScope
    @Provides
    internal fun provideLessonsDao(): LessonsDao = mockk(relaxed = true)
}