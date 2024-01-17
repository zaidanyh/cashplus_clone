package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class UserModel (
     @field:SerializedName("rc")
     val rc: String,

     @field:SerializedName("msg")
     val msg: String,

     @field:SerializedName("x_access_token")
     val x_access_token: String?
)
