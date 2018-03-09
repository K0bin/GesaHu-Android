package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import android.arch.lifecycle.Observer
import android.content.ContentResolver
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.provider.CalendarContract
import androidx.content.edit
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.instance
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.presenter.state.NavDrawerState
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import rhedox.gesahuvertretungsplan.util.PermissionManager

/**
 * Created by robin on 20.10.2016.
 */
class NavDrawerPresenter(private val kodeIn: Kodein, state: NavDrawerState) : NavDrawerContract.Presenter {
    private var view: NavDrawerContract.View? = null;
    private var account: Account? = null
    private var avatar: Bitmap? = null;

    private val accountManager: AccountManager = kodeIn.instance()
    private val permissionManager: PermissionManager = kodeIn.instance()

    private val boardsRepository: BoardsRepository = kodeIn.instance()

    private val prefs: SharedPreferences = kodeIn.instance()

    private var drawerId: Int? = null;

    private var boards = boardsRepository.loadBoards()

    private val observer = Observer<List<Board>?> {
        if (it?.isNotEmpty() != true) return@Observer
        onBoardsLoaded(it)
    }


    init {
        boardsRepository.loadBoards()

        drawerId = state.selectedDrawerId

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

    fun onBoardsLoaded(boards: List<Board>) {
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

    /**
     * Tries to load the account and avatar. If that fails, it'll start the AuthActivity to add an Account
     */
    private fun loadAccount() {
        if(account != null)
            return;

        val accounts = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType) ?: arrayOf<Account>()
        if (accounts.isNotEmpty()) {
            account = accounts[0]
            view?.userName = account!!.name
        }

        if(account != null) {
            //load avatar
            val avatarLoader: AvatarLoader = kodeIn.instance();
            avatarLoader.callback = {
                this.avatar = it
                view?.avatar = it
            }
            avatarLoader.loadAvatar();

            if(ContentResolver.getIsSyncable(account!!, CalendarContract.AUTHORITY) == 0 && permissionManager.isCalendarWritingPermissionGranted) {
                CalendarSyncService.setIsSyncEnabled(account!!, true)
            }
        } else if (view != null) {
            view!!.navigateToAuth()
        }
    }
}