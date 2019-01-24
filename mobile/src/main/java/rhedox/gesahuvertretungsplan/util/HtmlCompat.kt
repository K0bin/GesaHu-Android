package rhedox.gesahuvertretungsplan.util

import android.os.Build
import android.text.Spanned

/**
 * Created by robin on 07.10.2016.
 */

@Suppress("DEPRECATION")
class Html {
    companion object {
        fun fromHtml(text: String): Spanned {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) android.text.Html.fromHtml(text, 0) else android.text.Html.fromHtml(text)
        }

        fun decode(text: String): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) android.text.Html.fromHtml(text, 0).toString() else android.text.Html.fromHtml(text).toString()

        }
    }
}