package rhedox.gesahuvertretungsplan.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import rhedox.gesahuvertretungsplan.util.Open
import java.io.File

/**
 * Created by robin on 07.11.2016.
 */
@Open
class AvatarLoader(context: Context) {
    private val context = context.applicationContext;
    var callback: ((bitmap: Bitmap?) -> Unit)? = null

    fun loadAvatar() {
        doAsync {
            val file = context.getFileStreamPath(BoardsContract.avatarFileName)
            if (file == null || !file.exists()) {
                uiThread {
                    callback?.invoke(null)
                }
            } else {
                val stream = file.inputStream();
                val bitmap = BitmapFactory.decodeStream(stream)
                stream.close()
                uiThread {
                    callback?.invoke(bitmap)
                }
            }
        }
    }
}