package com.pasukanlangit.id.library_core.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @field:SerializedName("rc")
    val rc: String?,
    @field:SerializedName("msg")
    val msg: String?,
    @field:SerializedName("message")
    val message: String?
)