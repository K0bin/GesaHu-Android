package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.content.edit
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.dependencyInjection.BoardsComponent
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.presenter.state.NavDrawerState
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import javax.inject.Inject

/**
 * Created by robin on 20.10.2016.
 */
class NavDrawerPresenter(component: BoardsComponent, state: NavDrawerState) : NavDrawerContract.Presenter {
    private var view: NavDrawerContract.View? = null;
    private var account: Account? = null
    private var avatar: Bitmap? = null;

    private var drawerId: Int? = null;

    @Inject internal lateinit var accountManager: AccountManager
    @Inject internal lateinit var prefs: SharedPreferences
    @Inject internal lateinit var boardsRepository: BoardsRepository
    @Inject internal lateinit var avatarLoader: AvatarLoader

    private var boards: LiveData<List<Board>>

    private val observer = Observer<List<Board>?> {
        if (it?.isNotEmpty() != true) return@Observer
        onBoardsLoaded(it)
    }


    init {
        component.inject(this)
        boards = boardsRepository.loadBoards()

        avatarLoader.callback = {
            this.avatar = it
            view?.avatar = it
        }

        this.drawerId = state.selectedDrawerId
        checkFirstStart()
    }

    override fun attachView(view: NavDrawerContract.View) {
        boards.observeForever(observer)

        this.view = view;
        view.userName = account?.name ?: ""
        view.showBoards(this.boards.value ?: listOf())
        view.avatar = this.avatar

        if (drawerId != null) {
            view.currentDrawerId = drawerId!!
        }

        //Check first start again, might have returned from AuthActivity
        checkFirstStart()

        //Introduce new features to the user (needs View)
        updateApp()
    }

    override fun saveState(): NavDrawerState {
        return NavDrawerState(drawerId)
    }

    private fun checkFirstStart() {
        val previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        if (!previouslyStarted) {
            view?.navigateToIntro()
        } else if (account == null) {
            loadAccount()
        }
    }

    private fun updateApp() {
        val version = prefs.getInt(PreferenceFragment.PREF_VERSION, 0)
        if (version < BuildConfig.VERSION_CODE) {
            prefs.edit {
                putInt(PreferenceFragment.PREF_VERSION, BuildConfig.VERSION_CODE)
            }
        }

        if (version < 4012) {
            //Show intro again due to major update & permissions
            view?.navigateToIntro()
        }
    }

    override fun detachView() {
        this.view = null;
    }

    override fun destroy() {
        boards.removeObserver(observer)
    }

    private fun onBoardsLoaded(boards: List<Board>) {
        view?.showBoards(boards)
    }

    override fun onNavigationDrawerItemClicked(drawerId: Int) {
        when (drawerId) {
            this.drawerId -> return;

            NavDrawerContract.DrawerIds.substitutes -> view?.navigateToSubstitutes(null)
            NavDrawerContract.DrawerIds.about -> view?.navigateToAbout()
            NavDrawerContract.DrawerIds.settings -> view?.navigateToSettings()
            NavDrawerContract.DrawerIds.supervisions -> view?.navigateToSupervisions()
            else -> {
                if (boards.value != null) {
                    for (board in boards.value!!) {
                        if (board.name.hashCode() == drawerId - NavDrawerContract.DrawerIds.board) {
                            view?.navigateToBoard(board.name)
                        }
                    }
                }
            }
        }
        this.drawerId = drawerId
    }

    override fun onCalendarPermissionResult(isGranted: Boolean) {
        account ?: return
        view?.updateCalendarSync(account!!)
    }

    /**
     * Tries to load the account and avatar. If that fails, it'll start the AuthActivity to add an Account
     */
    private fun loadAccount() {
        if(account != null)
            return;

        val accounts = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType) ?: arrayOf<Account>()
        if (accounts.isNotEmpty()) {
            account = accounts[0]
            view?.userName = account!!.name ?: ""
        }

        if(account != null) {
            //load avatar
            avatarLoader.loadAvatar();

            CalendarSyncService.setIsPeriodicSyncEnabled(account!!, true)
        } else if (view != null) {
            view!!.navigateToAuth()
        }
    }
}