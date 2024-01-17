package com.pasukanlangit.id.downline_nearby_agent

sealed class ClosestAgentEvent {
    object GetClosestAgent: ClosestAgentEvent()
    data class UpdateLatLong(val lat: Double, val long: Double): ClosestAgentEvent()
    object RemoveHeadMessage: ClosestAgentEvent()
}