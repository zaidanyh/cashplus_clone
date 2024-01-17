package com.pasukanlangit.id.domain_downline.model

data class DownLineRequest(
    val uuid: String,
    val dateStart: String,
    val dateEnd: String,
    val downLinePhone: String,
    val downLineFullName: String,
    val rowStart: String
)
