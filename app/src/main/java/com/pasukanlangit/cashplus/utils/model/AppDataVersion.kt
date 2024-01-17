package com.pasukanlangit.cashplus.utils.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AppDataVersion(
    @field:SerializedName("app_version")
    val appVersion: String,
    @field:SerializedName("app_code_version")
    val appCodeVersion: Int,
    @field:SerializedName("new_features")
    val newFeatures: List<String>? = null,
    @field:SerializedName("improvements")
    val improvements: List<String>? = null,
    @field:SerializedName("bug_fixings")
    val bugFixings: List<String>? = null
): Parcelable
