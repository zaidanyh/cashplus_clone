package com.pasukanlangit.id.travelling.domain.model.train

data class TrainPriceResponse(
    val rc: String,
    val message: String,
    val trainPrice: TrainPrice?
)

data class TrainPrice(
    val name: String,
    val code: String,
    val id: String,
    val route: String,
    val basicFare: Int,
    val serviceCharge: Int,
    val totalFare: Int
)
