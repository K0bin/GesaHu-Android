package rhedox.gesahuvertretungsplan.dependency_injection

import dagger.BindsInstance
import dagger.Component
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.ui.activity.AuthActivity
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import javax.inject.Singleton

/**
 * Created by robin on 09.03.2018.
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(application: App)
    fun inject(activity: MainActivity)
    fun inject(activity: AuthActivity)
    fun inject(calendarSyncAdapter: CalendarSyncService.SyncAdapter)

    fun plusSubstitutes(): SubstitutesComponent
    fun plusBoards(): BoardsComponent
}