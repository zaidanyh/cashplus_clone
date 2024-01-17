package com.pasukanlangit.id.model

data class LocationRequest(
    val uuid: String,
    val name: String
)

data class LocationVillageRequest(
    val uuid: String,
    val district: String,
    val city: String
)