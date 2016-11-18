package rhedox.gesahuvertretungsplan.presenter

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import org.jetbrains.anko.accountManager
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.R
import rhedox.gesahuvertretungsplan.model.AvatarLoader
import rhedox.gesahuvertretungsplan.model.Board
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.model.database.BoardsRepository
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.service.GesaHuAccountService
import rhedox.gesahuvertretungsplan.ui.activity.AuthActivity
import rhedox.gesahuvertretungsplan.ui.activity.PreferenceActivity
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment

/**
 * Created by robin on 20.10.2016.
 */
abstract class BasePresenter() : Fragment(), BaseContract.Presenter {
    private var view: BaseContract.View? = null;
    protected var account: Account? = null;
        private set

    protected lateinit var boardsRepository: BoardsRepository
        private set

    protected lateinit var avatarLoader: AvatarLoader;
        private set;

    private var boards: List<Board> = listOf();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true;

        boardsRepository = BoardsRepository(context)
        boardsRepository.callback = { onBoardsLoaded(it) }
        boardsRepository.loadBoards()

        loadAccount();
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.userName = account?.name ?: ""
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if(context is BaseContract.View)
            view = context
    }

    override fun onDetach() {
        super.onDetach()

        view = null;
    }

    private fun onBoardsLoaded(boards: List<Board>) {
        view?.setBoards(boards)
        this.boards = boards;
    }

    override fun onNavigationDrawerItemClicked(drawerId: Int) {
        if(drawerId == R.id.settings) {
            val intent = Intent(context, PreferenceActivity::class.java)
            startActivity(intent)
        }
        if(drawerId >= drawerId && drawerId < drawerId + boards.size) {
            //START BOARD
        }
    }

    override fun onResume() {
        super.onResume()

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        if (!previouslyStarted) {
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (account == null) {
            //Try reloading account (might have just returned from AuthActivity)
            loadAccount()
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
        } else {
            context.accountManager.addAccount(GesaHuAccountService.GesaHuAuthenticator.accountType,
                    null, null, null, activity, null, null);
        }
    }
}