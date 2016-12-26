package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import com.github.salomonbrys.kodein.*
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
abstract class BasePresenter(private val kodeIn: Kodein) : BaseContract.Presenter, KodeinInjected {
    override val injector: KodeinInjector = KodeinInjector()

    private var view: BaseContract.View? = null;
    protected var account: Account? = null;
        private set

    private val accountManager: AccountManager by instance()

    private val boardsRepositoryProvider: () -> BoardsRepository by provider()
    protected val boardsRepository: BoardsRepository

    private val prefs: SharedPreferences by instance()

    private var boards: List<Board> = listOf();

    init {
        inject(kodeIn)

        boardsRepository = boardsRepositoryProvider.invoke()
        boardsRepository.callback = { onBoardsLoaded(it) }
        boardsRepository.loadBoards()

        val previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        if (previouslyStarted) {
            loadAccount();
        }
    }

    override fun attachView(view: BaseContract.View, isRecreated: Boolean) {
        this.view = view;
        view.userName = account?.name ?: ""

        val previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        if (!previouslyStarted) {
            view.openIntro()
        } else if (account == null) {
            //Try reloading account (might have just returned from AuthActivity)
            loadAccount()
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
        } else if (view != null) {
            accountManager.addAccount(GesaHuAccountService.GesaHuAuthenticator.accountType,
                    null, null, null, view as Activity, null, null);
        }
    }
}