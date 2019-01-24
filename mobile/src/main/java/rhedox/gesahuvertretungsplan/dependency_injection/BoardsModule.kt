package rhedox.gesahuvertretungsplan.dependency_injection

import android.content.Context
import dagger.Module
import dagger.Provides
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.BoardsDatabase
import rhedox.gesahuvertretungsplan.model.database.BoardsDatabaseRepository
import rhedox.gesahuvertretungsplan.model.database.dao.BoardsDao
import rhedox.gesahuvertretungsplan.model.database.dao.LessonsDao
import rhedox.gesahuvertretungsplan.model.database.dao.MarksDao

/**
 * Created by robin on 09.03.2018.
 */
@Module
class BoardsModule {
    @Provides
    @PresenterScope
    internal fun provideBoardsDao(boardsDB: BoardsDatabase): BoardsDao = boardsDB.boards

    @Provides
    @PresenterScope
    internal fun provideLessonsDao(boardsDB: BoardsDatabase): LessonsDao = boardsDB.lessons

    @Provides
    @PresenterScope
    internal fun provideMarksDao(boardsDB: BoardsDatabase): MarksDao = boardsDB.marks

    @Provides
    @PresenterScope
    internal fun provideAvatarLoader(context: Context): AvatarLoader = AvatarLoader(context)

    @Provides
    @PresenterScope
    internal fun provideRepository(repository: BoardsDatabaseRepository): BoardsRepository = repository
}