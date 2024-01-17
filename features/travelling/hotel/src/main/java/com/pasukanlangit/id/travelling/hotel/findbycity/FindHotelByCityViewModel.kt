package com.pasukanlangit.id.travelling.hotel.findbycity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.hotel.FindHotelResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class FindHotelByCityViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _hotelByCity = Channel<FindHotelResponse?>()
    val hotelByCity = _hotelByCity.receiveAsFlow()

    fun onTriggerEvent(events: HotelByCityEvents) {
        when(events) {
            is HotelByCityEvents.RemoveHeadMessage -> removeHeadQueue()
            is HotelByCityEvents.FindHotelByCity ->
                hotelByCity(events.cityCode, events.checkIn, events.checkOut, events.roomTotal)
        }
    }

    private fun hotelByCity(cityCode: String, checkIn: String, checkOut: String, roomTotal: String) {
        useCases
            .findHotelByCity(
                cityCode = cityCode, checkIn = checkIn,
                checkOut = checkOut, room = roomTotal
            ).onEach {
                it.data?.let { data -> _hotelByCity.send(data) }
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