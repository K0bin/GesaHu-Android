package rhedox.gesahuvertretungsplan.dependency_injection

import dagger.Subcomponent
import rhedox.gesahuvertretungsplan.model.database.SubstitutesDatabaseRepository
import rhedox.gesahuvertretungsplan.presenter.SubstitutesPresenter
import rhedox.gesahuvertretungsplan.presenter.SupervisionsPresenter
import rhedox.gesahuvertretungsplan.service.SubstitutesNotifier
import rhedox.gesahuvertretungsplan.service.SubstitutesSyncService
import rhedox.gesahuvertretungsplan.service.SubstitutesWidgetService

/**
 * Created by robin on 10.03.2018.
 */
@PresenterScope
@Subcomponent(modules = [SubstitutesModule::class])
interface SubstitutesComponent {
    fun repository(): SubstitutesDatabaseRepository

    fun inject(presenter: SubstitutesPresenter)
    fun inject(presenter: SupervisionsPresenter)
    fun inject(syncAdapter: SubstitutesSyncService.SyncAdapter)
    fun inject(notifier: SubstitutesNotifier)
    fun inject(widgetViewFactory: SubstitutesWidgetService.ViewsFactory)
}