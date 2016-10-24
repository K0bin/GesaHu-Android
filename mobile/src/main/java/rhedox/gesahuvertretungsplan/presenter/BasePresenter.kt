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
import rhedox.gesahuvertretungsplan.App
import rhedox.gesahuvertretungsplan.model.api.GesaHuApi
import rhedox.gesahuvertretungsplan.mvp.BaseContract
import rhedox.gesahuvertretungsplan.ui.activity.WelcomeActivity
import rhedox.gesahuvertretungsplan.ui.fragment.PreferenceFragment

/**
 * Created by robin on 20.10.2016.
 */
abstract class BasePresenter() : Fragment(), BaseContract.Presenter {
    private lateinit var gesahu: GesaHuApi
    private var view: BaseContract.View? = null;
    protected var account: Account? = null;
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true;

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            val accountManager = AccountManager.get(context)

            val accounts = accountManager.getAccountsByType(App.ACCOUNT_TYPE)
            if (accounts.size > 0)
                account = accounts[0]
        }
        gesahu = GesaHuApi.create(context)
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

    override fun onResume() {
        super.onResume()

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val previouslyStarted = prefs.getBoolean(PreferenceFragment.PREF_PREVIOUSLY_STARTED, false)
        if (!previouslyStarted) {
            val intent = Intent(context, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else if (account == null) {
            //FIX ACCOUNT
        }
    }
}