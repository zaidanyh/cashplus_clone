package com.pasukanlangit.id.kyc_presentation

import android.app.Activity
import android.content.Intent
import com.pasukanlangit.id.core.utils.createImagePicker

enum class ImagePickerType {
    GALLERY_ONLY,
    CAMERA_ONLY
}

private fun createImagePickerIntent(
    activity: Activity,
    widthRatio: Float, heightRatio: Float,
    widthPixels: Int, heightPixels: Int,
    type: ImagePickerType
): Intent {
    val imagePicker = createImagePicker(
        activity = activity, widthRatio = widthRatio, heightRatio = heightRatio,
        widthPixels = widthPixels, heightPixels = heightPixels
    )
    lateinit var intent: Intent
    when(type){
        ImagePickerType.GALLERY_ONLY -> imagePicker.galleryOnly().createIntent {
            intent = it
        }
        ImagePickerType.CAMERA_ONLY -> imagePicker.cameraOnly().createIntent {
            intent = it
        }
    }
    return intent
}

fun imagePickerIDIntent(activity: Activity, type: ImagePickerType) =
    createImagePickerIntent(
        activity = activity,
        widthRatio = AspectRatioCrop.ID_RATIO.widthRatio, heightRatio = AspectRatioCrop.ID_RATIO.heightRatio,
        widthPixels = AspectRatioCrop.ID_RATIO.widthPixels, heightPixels = AspectRatioCrop.ID_RATIO.heightPixels,
        type = type
    )

//fun imagePickerIDBuilder(activity: Activity) =
//    createImagePicker(activity, WIDTH_CROP_ID, HEIGHT_CROP_ID)
//
//fun imagePickerSelfieBuilder(activity: Activity) =
//    createImagePicker(activity, WIDTH_CROP_SELFIE, HEIGHT_CROP_SELFIE)

fun imagePickerSelfieIntent(activity: Activity, type: ImagePickerType) =
    createImagePickerIntent(
        activity = activity,
        widthRatio = AspectRatioCrop.SELFIE_RATIO.widthRatio, heightRatio = AspectRatioCrop.SELFIE_RATIO.heightRatio,
        widthPixels = AspectRatioCrop.SELFIE_RATIO.widthPixels, AspectRatioCrop.SELFIE_RATIO.heightPixels,
        type = type
    )

enum class EKycUploadType(val value: String) {
    NIK("ekyc_nik"),
    SELFIE("ekyc_selfie")
}
enum class EKycVerifyType(val value: String) {
    OCR("ocr"),
    SELFIE("selfie")
}

enum class AspectRatioCrop(val widthRatio: Float, val heightRatio: Float, val widthPixels: Int, val heightPixels: Int) {
    ID_RATIO(16f, 9f, 1280, 720),
    SELFIE_RATIO(9f, 16f, 720, 1280),
    PROFILE_PICT_RATIO(3f, 3f, 1080, 1080)
}