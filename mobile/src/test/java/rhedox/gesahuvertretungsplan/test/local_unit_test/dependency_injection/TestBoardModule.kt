package rhedox.gesahuvertretungsplan.test.local_unit_test.dependency_injection

import dagger.Module
import dagger.Provides
import org.mockito.Mockito.mock
import rhedox.gesahuvertretungsplan.dependency_injection.PresenterScope
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.dao.BoardsDao
import rhedox.gesahuvertretungsplan.model.database.dao.LessonsDao
import rhedox.gesahuvertretungsplan.model.database.dao.MarksDao
import rhedox.gesahuvertretungsplan.test.local_unit_test.repository.BoardsTestRepository

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
    internal fun provideAvatarLoader() = mock(AvatarLoader::class.java)

    @PresenterScope
    @Provides
    internal fun provideBoardsDao() = mock(BoardsDao::class.java)

    @PresenterScope
    @Provides
    internal fun provideMarksDao() = mock(MarksDao::class.java)

    @PresenterScope
    @Provides
    internal fun provideLessonsDao() = mock(LessonsDao::class.java)
}