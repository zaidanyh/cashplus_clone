package com.pasukanlangit.id.domain_downline.model

data class GetSubDownLineRequest(
    val uuid: String,
    val dateStart: String,
    val dateEnd: String,
    val downLineNumber: String,
    val downLineName: String,
    val rowStart: String
)
