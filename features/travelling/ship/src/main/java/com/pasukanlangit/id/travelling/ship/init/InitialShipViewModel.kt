package com.pasukanlangit.id.travelling.ship.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.ship.HarborCitiesResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import com.pasukanlangit.id.travelling.ship.temp.ShipRoute
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class InitialShipViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    val searchInput = MutableStateFlow("")

    private val _harbors = Channel<HarborCitiesResponse?>()
    val harbors = _harbors.receiveAsFlow()

    private val _departure = MutableStateFlow<ShipRoute?>(null)
    val departure: StateFlow<ShipRoute?> = _departure

    private val _destination = MutableStateFlow<ShipRoute?>(null)
    val destination: StateFlow<ShipRoute?> = _destination

    private val _passengers = MutableStateFlow<ShipPassengers?>(null)
    val passengers: StateFlow<ShipPassengers?> = _passengers

    fun onTriggerEvents(event: ShipEvents) {
        when(event) {
            is ShipEvents.RemoveHeadMessage -> removeHeadQueue()
            is ShipEvents.HarborsShip -> stationsList()
            is ShipEvents.SetDeparture -> setDeparture(event.departure)
            is ShipEvents.SetDestination -> setDestination(event.destination)
            is ShipEvents.SetPassengers -> setPassengers(event.passengers)
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
                        .harbors(it)
                        .onEach { stations ->
                            stations.data?.let { data -> _harbors.send(data) }
                            stations.message?.let { error -> appendErrorMessage(error) }
                            _stateLoading.value = stations.isLoading
                        }.launchIn(viewModelScope)
                }
        }
    }

    private fun setDeparture(departure: ShipRoute) {
        _departure.value = departure
    }

    private fun setDestination(destination: ShipRoute) {
        _destination.value = destination
    }

    private fun setPassengers(passengers: ShipPassengers) {
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