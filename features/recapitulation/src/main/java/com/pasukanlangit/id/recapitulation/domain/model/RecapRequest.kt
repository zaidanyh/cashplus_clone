package com.pasukanlangit.id.recapitulation.domain.model

data class RecapRequest(
    val uuid: String,
    val dateStart: String,
    val dateEnd: String,
    val rowStart: String? = null,
    val isSummary: String? = null
)
