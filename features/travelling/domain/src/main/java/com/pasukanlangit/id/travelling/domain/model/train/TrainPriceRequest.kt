package com.pasukanlangit.id.travelling.domain.model.train

data class TrainPriceRequest(
    val uuid: String,
    val session: String,
    val from: String,
    val to: String,
    val date: String,
    val adult: String,
    val infant: String,
    val trainCode: String,
    val trainClass: String,
    val trainSubClass: String
)
