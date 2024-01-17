package com.pasukanlangit.id.travelling.train.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.train.TrainScheduleResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class TrainScheduleViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _schedule = MutableStateFlow<TrainScheduleResponse?>(null)
    val schedule: StateFlow<TrainScheduleResponse?> = _schedule

    fun onTriggerEvents(events: TrainScheduleEvents) {
        when(events) {
            is TrainScheduleEvents.RemoveHeadMessage -> removeHeadQueue()
            is TrainScheduleEvents.TrainSchedule ->
                schedule(events.from, events.to, events.date, events.adult, events.infant)
        }
    }

    private fun schedule(from: String, to: String, date: String, adult: String, infant: String) {
        useCases
            .trainSchedule(
                from = from, to = to, date = date,
                adult = adult, infant = infant
            ).onEach {
                it.data?.let { data -> _schedule.value = data }
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