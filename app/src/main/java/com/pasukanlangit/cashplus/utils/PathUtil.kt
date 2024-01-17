package com.pasukanlangit.cashplus.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import java.net.URISyntaxException


object PathUtil {
    @SuppressLint("Recycle")
    @Throws(URISyntaxException::class)
    fun getPath(context: Context, uri: Uri?): String? {
        var newUri: Uri? = uri
        var selection: String? = null
        var selectionArgs: Array<String>? = null

        if (DocumentsContract.isDocumentUri(context.applicationContext, newUri)) {
            if (newUri?.let { isExternalStorageDocument(it) } == true) {
                val docId = DocumentsContract.getDocumentId(newUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                return "${Environment.getExternalStorageDirectory()}/${split.last()}"
            } else if (newUri?.let { isDownloadsDocument(it) } == true) {
                val id = DocumentsContract.getDocumentId(newUri)
                newUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), id.toLong()
                )
            } else if (newUri?.let { isMediaDocument(it) } == true) {
                val docId = DocumentsContract.getDocumentId(newUri)
                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                when (split.first()) {
                    "image" -> {
                        newUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        newUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        newUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                selection = "_id=?"
                selectionArgs = arrayOf(split[1])
            }
        }
        if ("content".equals(newUri?.scheme, ignoreCase = true)) {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            try {
                val cursor = newUri?.let {
                    context.contentResolver
                        .query(it, projection, selection, selectionArgs, null)
                }
                val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor?.moveToFirst() == true) {
                    return columnIndex?.let { cursor.getString(it) }
                }
            } catch (e: Exception) {
                Log.e("fetch_path_error", e.message.toString())
            }
        } else if ("file".equals(newUri?.scheme, ignoreCase = true)) {
            return newUri?.path
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority
}