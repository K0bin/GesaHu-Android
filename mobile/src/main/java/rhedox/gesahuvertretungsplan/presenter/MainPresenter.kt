package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.core.content.edit
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.dependency_injection.BoardsComponent
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.mvp.MainContract
import rhedox.gesahuvertretungsplan.presenter.state.MainState
import rhedox.gesahuvertretungsplan.security.EncryptionHelper
import rhedox.gesahuvertretungsplan.security.encryptExistingPassword
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import javax.inject.Inject

/**
 * Created by robin on 20.10.2016.
 */
class MainPresenter(component: BoardsComponent, state: MainState) : MainContract.Presenter {
    private var view: MainContract.View? = null
    private var account: Account? = null
    private var avatar: Bitmap? = null

    private var drawerId: Int? = null

    @Inject internal lateinit var accountManager: AccountManager
    @Inject internal lateinit var prefs: SharedPreferences
    @Inject internal lateinit var boardsRepository: BoardsRepository
    @Inject internal lateinit var avatarLoader: AvatarLoader
    @Inject internal lateinit var encryptionHelper: EncryptionHelper

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

    override fun attachView(view: MainContract.View) {
        boards.observeForever(observer)

        this.view = view
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

    override fun saveState(): MainState {
        return MainState(drawerId)
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
        if (version < 4045) {
            //Encrypt password
            if (account != null) {
                accountManager.encryptExistingPassword(account!!, encryptionHelper)
            }
        }
    }

    override fun detachView() {
        this.view = null
    }

    override fun destroy() {
        boards.removeObserver(observer)
    }

    private fun onBoardsLoaded(boards: List<Board>) {
        view?.showBoards(boards)
    }

    override fun onNavigationDrawerItemClicked(drawerId: Int) {
        when (drawerId) {
            this.drawerId -> return

            MainContract.DrawerIds.substitutes -> view?.navigateToSubstitutes(null)
            MainContract.DrawerIds.about -> view?.navigateToAbout()
            MainContract.DrawerIds.settings -> view?.navigateToSettings()
            MainContract.DrawerIds.supervisions -> view?.navigateToSupervisions()
            else -> {
                if (boards.value != null) {
                    for (board in boards.value!!) {
                        if (board.name.hashCode() == drawerId - MainContract.DrawerIds.board) {
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
            return

        val accounts = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType) ?: arrayOf<Account>()
        if (accounts.isNotEmpty()) {
            account = accounts[0]
            view?.userName = account!!.name ?: ""
        }

        if(account != null) {
            //load avatar
            avatarLoader.loadAvatar()

            CalendarSyncService.setIsPeriodicSyncEnabled(account!!, true)
        } else if (view != null) {
            view!!.navigateToAuth()
        }
    }
}