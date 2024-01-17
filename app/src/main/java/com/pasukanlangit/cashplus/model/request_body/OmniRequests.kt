package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class OmniRequests(
    @field:SerializedName("uuid")
    val uuid: String,

    @field:SerializedName("dest")
    val dest: String,

    @field:SerializedName("mlid")
    val mlid: String? = null,

    @field:SerializedName("packageId")
    val packageId: String? = null
)
