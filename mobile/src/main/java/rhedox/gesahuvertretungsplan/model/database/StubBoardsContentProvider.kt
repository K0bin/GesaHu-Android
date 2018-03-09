package rhedox.gesahuvertretungsplan.model.database

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Created by robin on 08.03.2018.
 */
class StubBoardsContentProvider: ContentProvider() {
    companion object {
        const val authority = "rhedox.gesahuvertretungsplan.boards.stub"
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? = null
    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null
    override fun onCreate(): Boolean = true
    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun getType(uri: Uri?): String? = null
}