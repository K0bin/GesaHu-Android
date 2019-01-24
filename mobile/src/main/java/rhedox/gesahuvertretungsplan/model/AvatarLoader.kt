package rhedox.gesahuvertretungsplan.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import rhedox.gesahuvertretungsplan.model.database.entity.Board
import rhedox.gesahuvertretungsplan.util.Open

/**
 * Created by robin on 07.11.2016.
 */
@Open
class AvatarLoader(context: Context) {
    private val context = context.applicationContext
    var callback: ((bitmap: Bitmap?) -> Unit)? = null

    fun loadAvatar() {
        doAsync {
            val file = context.getFileStreamPath(Board.avatarFileName)
            if (file == null || !file.exists()) {
                uiThread {
                    callback?.invoke(null)
                }
            } else {
                val stream = file.inputStream()
                val bitmap = BitmapFactory.decodeStream(stream)
                stream.close()
                uiThread {
                    callback?.invoke(bitmap)
                }
            }
        }
    }
}