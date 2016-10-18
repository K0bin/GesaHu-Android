package rhedox.gesahuvertretungsplan.model

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * Created by robin on 19.10.2016.
 */
class GesaHuiContentProvider : ContentProvider() {
    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor? {
        return null;
    }

    override fun onCreate(): Boolean {
        return true;
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0;
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0;
    }

    override fun getType(uri: Uri?): String? {
        return null;
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri? {
        return null;
    }
}