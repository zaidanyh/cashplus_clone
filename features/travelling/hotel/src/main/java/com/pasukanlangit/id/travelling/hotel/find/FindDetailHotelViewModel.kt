package com.pasukanlangit.id.travelling.hotel.find

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.hotel.FindHotelResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class FindDetailHotelViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _findDetailHotel = MutableStateFlow<FindHotelResponse?>(null)
    val findDetailHotel: StateFlow<FindHotelResponse?> = _findDetailHotel

    fun onTriggerEvents(events: DetailHotelEvents) {
        when(events) {
            is DetailHotelEvents.RemoveHeadMessage -> removeHeadQueue()
            is DetailHotelEvents.FindDetailHotel ->
                findDetailHotel(events.cityCode, events.hotelCode, events.checkIn, events.checkOut, events.room)
        }
    }

    private fun findDetailHotel(
        cityCode: String, hotelCode: String,
        checkIn: String, checkOut: String, room: String
    ) {
        useCases
            .findHotel(
                cityCode = cityCode, hotelCode = hotelCode, checkin = checkIn,
                checkout = checkOut, room = room
            ).onEach {
                it.data.let { data -> _findDetailHotel.value = data }
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