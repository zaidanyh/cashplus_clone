package com.pasukanlangit.id.travelling.hotel.fill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.BalanceResponse
import com.pasukanlangit.id.travelling.domain.model.BookingCodeResponse
import com.pasukanlangit.id.travelling.domain.model.TransactionResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class FillDataViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading
    private val _balance = Channel<BalanceResponse?>()
    val balance = _balance.receiveAsFlow()
    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _stateBookingLoading = MutableStateFlow(false)
    val stateBookingLoading: StateFlow<Boolean> = _stateBookingLoading
    private val _hotelBooking = Channel<BookingCodeResponse?>()
    val hotelBooking = _hotelBooking.receiveAsFlow()
    private val _stateBookingError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateBookingError: StateFlow<Queue<String>> = _stateBookingError

    private val _stateTransactionLoading = MutableStateFlow(false)
    val stateTransactionLoading: StateFlow<Boolean> = _stateTransactionLoading
    private val _transaction = Channel<TransactionResponse?>()
    val transaction = _transaction.receiveAsFlow()
    private val _stateTransactionError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateTransactionError: StateFlow<Queue<String>> = _stateTransactionError

    fun onTriggerEvent(event: FillDataEvents) {
        when(event) {
            is FillDataEvents.CheckBalance -> checkBalance()
            is FillDataEvents.RemoveHeadMessage -> removeHeadQueue()
            is FillDataEvents.HotelBooking ->
                hotelBooking(
                    event.hotelCode, event.hotelKey, event.rateKey, event.checkIn,
                    event.checkOut, event.hotelRoom, event.paxName, event.email,
                    event.phone, event.hotelRequest
                )
            is FillDataEvents.RemoveHeadMessageBooking -> removeHeadQueueBooking()
            is FillDataEvents.HotelBookingTransaction ->
                hotelTransaction(event.codeProduct, event.destination, event.pin)
            is FillDataEvents.RemoveHeadMessageTransaction -> removeHeadQueueTransaction()
        }
    }

    private fun checkBalance() {
        useCases
            .checkBalance()
            .onEach {
                it.data?.let { data -> _balance.send(data) }
                it.message?.let { error -> appendErrorMessage(error) }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun hotelBooking(
        code: String, hotelKey: String, rateKey: String, checkIn: String, checkOut: String,
        room: String, paxName: String, email: String, phone: String, request: String
    ) {
        useCases
            .hotelBooking(code, hotelKey, rateKey, checkIn, checkOut, room, paxName, email, phone, request)
            .onEach {
                it.data?.let { data -> _hotelBooking.send(data) }
                it.message?.let { error -> appendErrorMessageBooking(error) }
                _stateBookingLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun hotelTransaction(codeProduct: String, destination: String?, pin: String) {
        useCases
            .booking(codeProduct, destination.toString(), pin)
            .onEach {
                it.data?.let { data -> _transaction.send(data) }
                it.message?.let { error -> appendErrorMessageTransaction(error) }
                _stateTransactionLoading.value = it.isLoading
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

    private fun removeHeadQueueBooking() {
        val currentMessage = stateBookingError.value
        currentMessage.remove()
        _stateBookingError.value = ArrayDeque(currentMessage)
    }

    private fun appendErrorMessageBooking(error: String) {
        val currentMessage = stateBookingError.value
        currentMessage.add(error)
        _stateBookingError.value = ArrayDeque(currentMessage)
    }

    private fun removeHeadQueueTransaction() {
        val currentMessage = stateTransactionError.value
        currentMessage.remove()
        _stateTransactionError.value = ArrayDeque(currentMessage)
    }

    private fun appendErrorMessageTransaction(error: String) {
        val currentMessage = stateTransactionError.value
        currentMessage.add(error)
        _stateTransactionError.value = ArrayDeque(currentMessage)
    }
}