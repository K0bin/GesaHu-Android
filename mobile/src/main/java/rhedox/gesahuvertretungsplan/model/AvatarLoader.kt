package rhedox.gesahuvertretungsplan.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import rhedox.gesahuvertretungsplan.util.Open
import java.io.File

/**
 * Created by robin on 07.11.2016.
 */
@Open
class AvatarLoader(context: Context) : AsyncTask<Unit, Unit, Bitmap?>() {
    private val context = context.applicationContext;
    var callback: ((bitmap: Bitmap) -> Unit)? = null

    override fun doInBackground(vararg param: Unit?): Bitmap? {
        val file = context.getFileStreamPath(BoardsContract.avatarFileName)
        if (file == null || !file.exists()) {
        } else {
            val stream = file.inputStream();
            val bitmap = BitmapFactory.decodeStream(stream)
            stream.close()
            return bitmap
        }
        return null;
    }

    override fun onPostExecute(result: Bitmap?) {
        super.onPostExecute(result)
        if(result != null) {
            callback?.invoke(result)
        }
    }
}