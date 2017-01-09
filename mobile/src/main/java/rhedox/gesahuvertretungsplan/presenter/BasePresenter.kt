package rhedox.gesahuvertretungsplan.presenter

import android.Manifest
import android.accounts.Account
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.CalendarContract
import android.support.v4.content.ContextCompat
import org.jetbrains.anko.accountManager
import rhedox.gesahuvertretungsplan.BuildConfig
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.service.CalendarSyncService
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment

/**
 * Created by robin on 20.10.2016.
 */
abstract class BasePresenter(context: Context) : BaseContract.Presenter {
    protected val context: Context = context.applicationContext
    private var view: BaseContract.View? = null;
    protected var account: Account? = null;
        private set

    protected var boardsRepository: BoardsRepository
        private set

    protected lateinit var avatarLoader: AvatarLoader;
        private set;

    private var boards: List<Board> = listOf();

    init {
        boardsRepository = BoardsRepository(context)
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
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        if (!previouslyStarted) {
            view?.navigateToIntro()
        } else if (account == null) {
            loadAccount()
        }
    }

    private fun updateApp() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val version = prefs.getInt(PreferenceFragment.PREF_VERSION, 0)
        if (version < 4012) {
            //Show intro again due to major update & permissions
            view?.navigateToIntro()
        }
        if (version < BuildConfig.VERSION_CODE) {
            val editor = prefs.edit()
            editor.putInt(PreferenceFragment.PREF_VERSION, BuildConfig.VERSION_CODE)
            editor.apply()
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

        val accounts = context.accountManager.getAccountsByType(GesaHuAccountService.GesaHuAuthenticator.accountType) ?: arrayOf<Account>()
        if (accounts.isNotEmpty())
            account = accounts[0]

        if(account != null) {
            //load avatar
            avatarLoader = AvatarLoader(context)
            avatarLoader.callback = {
                view?.setAvatar(it)
            }
            avatarLoader.execute();

            if(ContentResolver.getIsSyncable(account!!, CalendarContract.AUTHORITY) == 0 && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                CalendarSyncService.setIsSyncEnabled(account!!, true)
            }
        } else if (view != null) {
            view!!.navigateToAuth()
        }
    }
}