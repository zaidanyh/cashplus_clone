package com.pasukanlangit.id.travelling.train.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.train.StationsResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import com.pasukanlangit.id.travelling.train.temp.Passengers
import com.pasukanlangit.id.travelling.train.temp.TrainRoute
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class InitialTrainViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    val searchInput = MutableStateFlow("")

    private val _stations = Channel<StationsResponse?>()
    val stations = _stations.receiveAsFlow()

    private val _departure = MutableStateFlow<TrainRoute?>(null)
    val departure: StateFlow<TrainRoute?> = _departure

    private val _destination = MutableStateFlow<TrainRoute?>(null)
    val destination: StateFlow<TrainRoute?> = _destination

    private val _passengers = MutableStateFlow<Passengers?>(null)
    val passengers: StateFlow<Passengers?> = _passengers

    fun onTriggerEvents(events: TrainEvents) {
        when(events) {
            is TrainEvents.RemoveHeadMessage -> removeHeadQueue()
            is TrainEvents.StationsList -> stationsList()
            is TrainEvents.SetDeparture -> setDeparture(events.departure)
            is TrainEvents.SetDestination -> setDestination(events.destination)
            is TrainEvents.SetPassengers -> setPassengers(events.passengers)
        }
    }

    @OptIn(FlowPreview::class)
    private fun stationsList() {
        viewModelScope.launch {
            searchInput
                .debounce(800)
                .distinctUntilChanged()
                .filter { it.length > 2 }
                .collectLatest {
                    useCases
                        .stations(it)
                        .onEach { stations ->
                            stations.data?.let { data -> _stations.send(data) }
                            stations.message?.let { error -> appendErrorMessage(error) }
                            _stateLoading.value = stations.isLoading
                        }.launchIn(viewModelScope)
                }
        }
    }

    private fun setDeparture(departure: TrainRoute) {
        _departure.value = departure
    }

    private fun setDestination(destination: TrainRoute) {
        _destination.value = destination
    }

    private fun setPassengers(passengers: Passengers) {
        _passengers.value = passengers
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