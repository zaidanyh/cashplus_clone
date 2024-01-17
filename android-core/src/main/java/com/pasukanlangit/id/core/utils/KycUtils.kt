package com.pasukanlangit.id.core.utils

import android.app.Activity
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.ByteArrayOutputStream
import java.io.InputStream

fun createImagePicker(
    activity: Activity,
    widthRatio: Float, heightRatio: Float,
    widthPixels: Int, heightPixels: Int
): ImagePicker.Builder {
    return ImagePicker.with(activity)
        .cameraOnly() // <- for now KYC just can pick from camera only. so you can ignore another function handle by gallery
        .crop(widthRatio, heightRatio)	//Crop image(Optional), Check Customization for more option
        .compress(3072)			//Final image size will be less than 3 MB(Optional)
        .maxResultSize(widthPixels, heightPixels)
}

private fun encodeImage(bm: Bitmap): String {
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val b: ByteArray = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun getBase64FromUri(uri: Uri?, contentResolver: ContentResolver): String =
    uri?.let { uri_not_null ->
        val imageStream: InputStream? = contentResolver.openInputStream(uri_not_null)
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        encodeImage(selectedImage)
    }.toString()