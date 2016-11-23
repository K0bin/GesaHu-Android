package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import org.jetbrains.anko.accountManager
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment

/**
 * Created by robin on 20.10.2016.
 */
abstract class BasePresenter(context: Context, state: Bundle? = null) : BaseContract.Presenter {
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

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        if (previouslyStarted) {
            loadAccount();
        }
    }

    override fun onViewAttached(view: BaseContract.View) {
        this.view = view;
        view.userName = account?.name ?: ""

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        if (!previouslyStarted) {
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        } else if (account == null) {
            //Try reloading account (might have just returned from AuthActivity)
            loadAccount()
        }
    }

    override fun onViewDetached() {
        this.view = null;
    }

    private fun onBoardsLoaded(boards: List<Board>) {
        view?.setBoards(boards)
        this.boards = boards;
    }

    override fun onNavigationDrawerItemClicked(drawerId: Int) {
        if(drawerId == R.id.settings) {
            view?.openSettings()
        } else if(drawerId == R.id.about) {
            view?.openAbout()
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
        } else if (view != null) {
            context.accountManager.addAccount(GesaHuAccountService.GesaHuAuthenticator.accountType,
                    null, null, null, view as Activity, null, null);
        }
    }
}