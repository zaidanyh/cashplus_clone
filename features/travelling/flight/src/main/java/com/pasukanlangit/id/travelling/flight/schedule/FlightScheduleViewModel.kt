package com.pasukanlangit.id.travelling.flight.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.flight.FlightScheduleResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class FlightScheduleViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _flightSchedule = MutableStateFlow<FlightScheduleResponse?>(null)
    val flightSchedule: StateFlow<FlightScheduleResponse?> = _flightSchedule

    fun onTriggersEvent(event: ScheduleEvents) {
        when(event) {
            is ScheduleEvents.RemoveHeadMessage -> removeHeadQueue()
            is ScheduleEvents.FlightSchedule -> flightSchedules(
                event.departure, event.destination, event.date
            )
        }
    }

    private fun flightSchedules(departure: String, destination: String, date: String) {
        useCases
            .flightSchedule(
                departure, destination, date
            ).onEach {
                it.data.let { data -> _flightSchedule.value = data }
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