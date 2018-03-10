package rhedox.gesahuvertretungsplan.dependencyInjection

import dagger.BindsInstance
import dagger.Component
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.ui.activity.MainActivity
import javax.inject.Singleton

/**
 * Created by robin on 09.03.2018.
 */
@Singleton
@Component(modules = [AppModule::class])
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: App): Builder

        fun build(): AppComponent
    }

    fun inject(application: App)
    fun inject(activity: MainActivity)

    fun plusSubstitutes(): SubstitutesComponent
    fun plusBoards(): BoardsComponent
}