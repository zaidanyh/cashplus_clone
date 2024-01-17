package com.pasukanlangit.id.nobu.domain.model

data class HistoryTrxRequest(
    val uuid: String,
    val dateStart: String,
    val dateEnd: String,
    val pageNumber: String,
    val pageSize: String
)
