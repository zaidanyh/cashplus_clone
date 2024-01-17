package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppsVersionResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("ANDROID_VERSION_CODE")
	val androidVersionCode: String,

	@field:SerializedName("ANDROID_UPDATE_NOTE")
	val androidUpdateNote: String,

	@field:SerializedName("ANDROID_VERSION_NAME")
	val androidVersionName: String
) : Parcelable
