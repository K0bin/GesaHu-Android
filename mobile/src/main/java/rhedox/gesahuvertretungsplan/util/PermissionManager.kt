package rhedox.gesahuvertretungsplan.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat

/**
 * Abstraction around the static ContextCompat class to keep the presenter testable
 */
@Open
class PermissionManager(context: Context) {
    private val context = context.applicationContext

    val isCalendarReadingPermissionGranted: Boolean
        get() = checkPermission(android.Manifest.permission.READ_CALENDAR)

    val isCalendarWritingPermissionGranted: Boolean
        get() = checkPermission(android.Manifest.permission.READ_CALENDAR)

    private fun checkPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}