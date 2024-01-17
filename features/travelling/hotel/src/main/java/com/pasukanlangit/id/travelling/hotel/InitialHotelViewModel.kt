package com.pasukanlangit.id.travelling.hotel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.hotel.HotelCityListResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import com.pasukanlangit.id.travelling.hotel.temp.CitySelected
import com.pasukanlangit.id.travelling.hotel.temp.RoomVisitor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class InitialHotelViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    val searchInput = MutableStateFlow("")

    private val _cityList = Channel<HotelCityListResponse?>()
    val cityList = _cityList.receiveAsFlow()

    private val _citySelected = MutableStateFlow<CitySelected?>(null)
    val citySelected: StateFlow<CitySelected?> = _citySelected

    private val _roomVisitor = MutableStateFlow<RoomVisitor?>(null)
    val roomVisitor: StateFlow<RoomVisitor?> = _roomVisitor

    fun onTriggerEvent(event: HotelEvents) {
        when(event) {
            is HotelEvents.RemoveHeadMessage -> removeHeadQueue()
            is HotelEvents.CityList -> hotelCityList()
            is HotelEvents.SetCitySelected -> setCitySelected(event.citySelected)
            is HotelEvents.SetRoomVisitor -> setRoomVisitor(event.roomVisitor)
        }
    }

    @OptIn(FlowPreview::class)
    private fun hotelCityList() {
        viewModelScope.launch {
            searchInput
                .debounce(800)
                .distinctUntilChanged()
                .filter { it.length > 2 }
                .collectLatest {
                    useCases
                        .hotelCityList(it)
                        .onEach { cities ->
                            cities.data.let { data -> _cityList.send(data) }
                            cities.message?.let { error -> appendErrorMessage(error) }
                            _stateLoading.value = cities.isLoading
                        }.launchIn(viewModelScope)
                }
        }
    }

    private fun setCitySelected(citySelected: CitySelected) {
        _citySelected.value = citySelected
    }

    private fun setRoomVisitor(roomVisitor: RoomVisitor) {
        _roomVisitor.value = roomVisitor
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