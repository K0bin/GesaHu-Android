package rhedox.gesahuvertretungsplan.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Abstraction around the static ContextCompat class to keep the presenter testable
 */

val Context.isCalendarReadingPermissionGranted: Boolean
    get() = checkPermission(this, android.Manifest.permission.READ_CALENDAR)
val Context. isCalendarWritingPermissionGranted: Boolean
    get() = checkPermission(this, android.Manifest.permission.WRITE_CALENDAR)

fun checkPermission(context: Context, permission: String): Boolean {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}