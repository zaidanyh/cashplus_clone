package com.pasukanlangit.id.domain_downline.model

data class DownLineSumDetRequest(
    val uuid: String,
    val dateStart: String,
    val dateEnd: String,
    val downlinePhone: String?
)
