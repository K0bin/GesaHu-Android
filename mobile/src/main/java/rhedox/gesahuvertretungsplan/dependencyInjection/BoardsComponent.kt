package rhedox.gesahuvertretungsplan.dependencyInjection

import dagger.Subcomponent
import rhedox.gesahuvertretungsplan.model.database.BoardsDatabaseRepository
import rhedox.gesahuvertretungsplan.presenter.BoardPresenter
import rhedox.gesahuvertretungsplan.presenter.LessonsPresenter
import rhedox.gesahuvertretungsplan.presenter.MarksPresenter
import rhedox.gesahuvertretungsplan.presenter.NavDrawerPresenter
import rhedox.gesahuvertretungsplan.service.BoardsSyncService

/**
 * Created by robin on 10.03.2018.
 */
@PresenterScope
@Subcomponent(modules = [BoardsModule::class])
interface BoardsComponent {
    fun repository(): BoardsDatabaseRepository

    fun inject(presenter: NavDrawerPresenter)
    fun inject(presenter: BoardPresenter)
    fun inject(presenter: LessonsPresenter)
    fun inject(presenter: MarksPresenter)
    fun inject(syncAdapter: BoardsSyncService.SyncAdapter)
}