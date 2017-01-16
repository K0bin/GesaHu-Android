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
import rhedox.gesahuvertretungsplan.model.api.json.BoardName
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment
import rhedox.gesahuvertretungsplan.util.PermissionManager

/**
 * Created by robin on 20.10.2016.
 */
abstract class BasePresenter(private val kodeIn: Kodein) : BaseContract.Presenter, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    private var view: BaseContract.View? = null;
    protected var account: Account? = null;
        private set

    private val accountManager: AccountManager by instance()
    private val permissionManager: PermissionManager by instance()

    private val boardsRepositoryProvider: () -> BoardsRepository by provider()
    protected val boardsRepository: BoardsRepository

    private val prefs: SharedPreferences by instance()

    private var boards: List<String> = listOf();

    init {
        inject(kodeIn)

        boardsRepository = boardsRepositoryProvider.invoke()
        boardsRepository.callback = { onBoardsLoaded(it) }
        boardsRepository.loadBoards()

        checkFirstStart()
    }

    override fun attachView(view: BaseContract.View, isRecreated: Boolean) {
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

    private fun onBoardsLoaded(boards: List<String>) {
        view?.setBoards(boards)
        this.boards = boards;
    }

    override fun onNavigationDrawerItemClicked(drawerId: Int) {
        if(drawerId == R.id.settings) {
            view?.navigateToSettings()
        } else if(drawerId == R.id.about) {
            view?.navigateToAbout()
        } else if(drawerId >= drawerId && drawerId < drawerId + boards.size) {
            //START BOARD
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