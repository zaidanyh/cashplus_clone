package com.pasukanlangit.id.travelling.ship.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.ship.HarborScheduleResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class ShipScheduleViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _shipSchedule = MutableStateFlow<HarborScheduleResponse?>(null)
    val shipSchedule: StateFlow<HarborScheduleResponse?> = _shipSchedule

    fun onTriggerEvents(event: ShipScheduleEvents) {
        when(event) {
            is ShipScheduleEvents.RemoveHeadMessage -> removeHeadQueue()
            is ShipScheduleEvents.ShipSchedule ->
                shipSchedule(
                    from = event.from, to = event.to, date = event.date,
                    passengers = event.passengers
                )
        }
    }

    private fun shipSchedule(
        from: String, to: String, date: String, passengers: ShipPassengers?
    ) {
        useCases
            .harborSchedule(
                from, to, date, (passengers?.male ?: 1).plus(passengers?.female ?: 0).toString(),
                passengers?.infant.toString(), passengers?.male.toString(), passengers?.female.toString()
            ).onEach {
                it.data?.let { data -> _shipSchedule.value = data }
                it.message?.let { error -> appendErrorMessage(error) }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(currentMessage)
    }

    private fun appendErrorMessage(error: String) {
        val currentMessage = stateError.value
        currentMessage.add(error)
        _stateError.value = ArrayDeque(currentMessage)
    }
}