package com.pasukanlangit.id.travelling.flight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.flight.*
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import com.pasukanlangit.id.travelling.flight.temp.FlightRoute
import com.pasukanlangit.id.travelling.flight.temp.Passengers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class FlightViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    val searchInput = MutableStateFlow("")

    private val _airports = Channel<AirportsResponse?>()
    val airports = _airports.receiveAsFlow()

    private val _departure = MutableStateFlow<FlightRoute?>(null)
    val departure: StateFlow<FlightRoute?> = _departure

    private val _destination = MutableStateFlow<FlightRoute?>(null)
    val destination: StateFlow<FlightRoute?> = _destination

    private val _passengers = MutableStateFlow<Passengers?>(null)
    val passengers: StateFlow<Passengers?> = _passengers

    fun onTriggerEvent(event: FlightEvents) {
        when(event) {
            is FlightEvents.RemoveHeadMessage -> removeHeadQueue()
            is FlightEvents.AirportList -> airportList()
            is FlightEvents.SetDeparture -> setDeparture(event.departure)
            is FlightEvents.SetDestination -> setDestination(event.destination)
            is FlightEvents.SetPassengers -> setPassengers(event.passengers)
        }
    }

    @OptIn(FlowPreview::class)
    private fun airportList() {
        viewModelScope.launch {
            searchInput
                .debounce(800)
                .distinctUntilChanged()
                .filter { it.length > 2 }
                .collectLatest {
                    useCases
                        .airportList(it)
                        .onEach { airports ->
                            airports.data.let { data -> _airports.send(data) }
                            airports.message?.let { error -> appendErrorMessage(error) }
                            _stateLoading.value = airports.isLoading
                        }.launchIn(viewModelScope)
                }
        }
    }

    private fun setDeparture(departure: FlightRoute) {
        _departure.value = departure
    }

    private fun setDestination(destination: FlightRoute) {
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