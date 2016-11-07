package rhedox.gesahuvertretungsplan.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import rhedox.gesahuvertretungsplan.model.database.tables.BoardsContract

/**
 * Created by robin on 07.11.2016.
 */
class AvatarLoader(context: Context) : AsyncTask<Unit, Unit, Bitmap>() {
    private val context = context.applicationContext;
    var callback: ((bitmap: Bitmap) -> Unit)? = null

    override fun doInBackground(vararg param: Unit?): Bitmap {
        val fileStream = context.openFileInput(BoardsContract.avatarFileName)
        return BitmapFactory.decodeStream(fileStream)
    }

    override fun onPostExecute(result: Bitmap) {
        super.onPostExecute(result)
        callback?.invoke(result)
    }
}