package com.pasukanlangit.id.nobu.domain.model

data class SendResultScanRequest(
    val uuid: String,
    val resultScan: String,
    val callBackUrl: String
)