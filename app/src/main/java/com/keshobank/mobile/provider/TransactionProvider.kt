package com.keshobank.mobile.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.keshobank.mobile.data.local.KeshoDatabase

// Vuln #5: exported with no readPermission (see AndroidManifest.xml), so any
// app on-device can query transaction data via
// content://com.keshobank.mobile.provider.transactions, with no caller check at all.
class TransactionProvider : ContentProvider() {

    override fun onCreate(): Boolean = true

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val context = context ?: return null
        val db = KeshoDatabase.getInstance(context).openHelper.readableDatabase
        return db.query(
            "SELECT * FROM transactions" + (sortOrder?.let { " ORDER BY $it" } ?: "")
        )
    }

    override fun getType(uri: Uri): String = "vnd.android.cursor.dir/com.keshobank.mobile.transaction"

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}
