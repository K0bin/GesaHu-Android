package rhedox.gesahuvertretungsplan.util

import android.accounts.AccountManager
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.view.WindowManager

/**
 * Created by robin on 09.03.2018.
 */
val Context.windowManager: WindowManager
    inline get() = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager

val Context.accountManager: AccountManager
    inline get() = this.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager

val Context.connectivityManager: ConnectivityManager
    inline get() = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

val Context.notificationManager: NotificationManager
    inline get() = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

val Context.alarmManager: AlarmManager
    inline get() = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager