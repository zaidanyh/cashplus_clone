package com.pasukanlangit.id.domain_downline.model

data class NearbyAgentResponse(
    val myLat: Double,
    val myLong: Double,
    val agents: List<AgentNearBy>
)

data class AgentNearBy(
    val name: String,
    val address: String,
    val distance: String,
    val lat: Double,
    val long: Double
)
