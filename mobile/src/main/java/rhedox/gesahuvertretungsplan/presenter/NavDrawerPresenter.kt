package rhedox.gesahuvertretungsplan.presenter

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.preference.PreferenceManager
import android.provider.CalendarContract
import android.support.v4.content.ContextCompat
import android.content.SharedPreferences
import android.os.Bundle
import com.github.salomonbrys.kodein.*
import org.jetbrains.anko.accountManager
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.mvp.NavDrawerContract
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import rhedox.gesahuvertretungsplan.util.PermissionManager

/**
 * Created by robin on 20.10.2016.
 */
open class NavDrawerPresenter(private val kodeIn: Kodein) : NavDrawerContract.Presenter {
    private var view: NavDrawerContract.View? = null;
    protected var account: Account? = null;
        private set

    private val accountManager: AccountManager = kodeIn.instance()
    private val permissionManager: PermissionManager = kodeIn.instance()

    protected val boardsRepository: BoardsRepository = kodeIn.instance()

    private val prefs: SharedPreferences = kodeIn.instance()

    private var boards: List<Board> = listOf();

    init {
        boardsRepository.boardsCallback = { onBoardsLoaded(it) }
        boardsRepository.loadBoards()

        checkFirstStart()
    }

    override fun attachView(view: NavDrawerContract.View, isRecreated: Boolean) {
        this.view = view;
        view.userName = account?.name ?: ""

        //Check first start again, might have returned from AuthActivity
        checkFirstStart()

        //Introduce new features to the user (needs View)
        updateApp()
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
            val editor = prefs.edit() ?: return

            editor.putInt(PreferenceFragment.PREF_VERSION, BuildConfig.VERSION_CODE)
            editor.apply()
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
        boardsRepository.destroy()
    }

    private fun onBoardsLoaded(boards: List<Board>) {
        view?.setBoards(boards)
        this.boards = boards;
    }

    override fun onNavigationDrawerItemClicked(drawerId: Int) {
        if(drawerId == R.id.settings) {
            view?.navigateToSettings()
        } else if(drawerId == R.id.about) {
            view?.navigateToAbout()
        } else {
            for (board in boards) {
                if (board.id == drawerId - 13L) {
                    view?.navigateToBoard(board.id)
                }
            }
        }
    }

    /**
     * Tries to load the account and avatar. If that fails, it'll start the AuthActivity to add an Account
     */
    private fun loadAccount() {
        if(account != null)
            return;

        val accounts = accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType) ?: arrayOf<Account>()
        if (accounts.isNotEmpty())
            account = accounts[0]

        if(account != null) {
            //load avatar
            val avatarLoaderProvider: () -> AvatarLoader = kodeIn.provider();
            val avatarLoader = avatarLoaderProvider.invoke()
            avatarLoader.callback = {
                view?.setAvatar(it)
            }
            avatarLoader.execute();

            if(ContentResolver.getIsSyncable(account!!, CalendarContract.AUTHORITY) == 0 && permissionManager.isCalendarWritingPermissionGranted) {
                CalendarSyncService.setIsSyncEnabled(account!!, true)
            }
        } else if (view != null) {
            view!!.navigateToAuth()
        }
    }
}