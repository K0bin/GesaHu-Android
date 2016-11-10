package rhedox.gesahuvertretungsplan.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract
import java.io.File

/**
 * Created by robin on 07.11.2016.
 */
class AvatarLoader(context: Context) : AsyncTask<Unit, Unit, Bitmap?>() {
    private val context = context.applicationContext;
    var callback: ((bitmap: Bitmap) -> Unit)? = null

    override fun doInBackground(vararg param: Unit?): Bitmap? {
        if(fileExists(context, BoardsContract.avatarFileName)) {
            val fileStream = context.openFileInput(BoardsContract.avatarFileName)
            val bitmap = BitmapFactory.decodeStream(fileStream)
            fileStream.close()

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

    private fun fileExists(context: Context, filename: String): Boolean {
        val file = context.getFileStreamPath(filename)
        if (file == null || !file.exists()) {
            return false
        }
        return true
    }
}