package com.pasukanlangit.id.travelling.ship.fill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.BalanceResponse
import com.pasukanlangit.id.travelling.domain.model.BookingCodeResponse
import com.pasukanlangit.id.travelling.domain.model.TransactionResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import com.pasukanlangit.id.travelling.ship.temp.ShipPassengers
import com.pasukanlangit.id.travelling.ship.temp.ShipSchedules
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class FillDataShipViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _balance = Channel<BalanceResponse?>()
    val balance = _balance.receiveAsFlow()
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading
    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _stateBookingLoading = MutableStateFlow(false)
    val stateBookingLoading: StateFlow<Boolean> = _stateBookingLoading
    private val _shipBooking = Channel<BookingCodeResponse?>()
    val shipBooking = _shipBooking.receiveAsFlow()
    private val _bookingError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val bookingError: StateFlow<Queue<String>> = _bookingError

    private val _transactionLoading = MutableStateFlow(false)
    val transactionLoading: StateFlow<Boolean> = _transactionLoading
    private val _transactionBooking = Channel<TransactionResponse?>()
    val transactionBooking = _transactionBooking.receiveAsFlow()
    private val _transactionError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val transactionError: StateFlow<Queue<String>> = _transactionError

    fun onTriggerEvents(events: FillDataShipEvents) {
        when(events) {
            is FillDataShipEvents.RemoveHeadMessage -> removeHeadQueue()
            is FillDataShipEvents.CheckBalance -> checkBalance()
            is FillDataShipEvents.RemoveHeadBookingMessage -> removeHeadQueueBooking()
            is FillDataShipEvents.ShipBooking ->
                shipBooking(
                    events.from, events.to, events.date, events.shipSchedules,
                    events.passengers, events.passengerName, events.dateOfBirth, events.idNumber,
                    events.phone, events.email
                )
            is FillDataShipEvents.RemoveHeadMessageTransaction -> removeHeadQueueTransaction()
            is FillDataShipEvents.ShipBookingTransaction -> shipTransaction(events.codeProduct, events.destination, events.pin)
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

    private fun shipBooking(
        from: String, to: String, date: String, shipSchedules: ShipSchedules,
        passengers: ShipPassengers, passengerName: String, dateOfBirth: String,
        idNumber: String, phone: String, email: String
    ) {
        useCases
            .shipBooking(
                from = from, to = to, date = date, shipName = shipSchedules.name, shipNumber = shipSchedules.number,
                shipCode = shipSchedules.code, shipClass = shipSchedules.shipClass, adult = passengers.male.plus(passengers.female).toString(),
                infant = passengers.infant.toString(), passengerName = passengerName, dateOfBirth = dateOfBirth,
                idNumber = idNumber, phone = phone, email = email
            )
            .onEach {
                it.data?.let { data ->_shipBooking.send(data) }
                it.message?.let { error -> appendErrorMessageBooking(error) }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun shipTransaction(codeProduct: String, destination: String?, pin: String) {
        useCases
            .booking(codeProduct, destination.toString(), pin)
            .onEach {
                it.data?.let { data -> _transactionBooking.send(data) }
                it.message?.let { error -> appendErrorMessageTransaction(error) }
                _transactionLoading.value = it.isLoading
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
        val currentMessage = bookingError.value
        currentMessage.remove()
        _bookingError.value = ArrayDeque(currentMessage)
    }

    private fun appendErrorMessageBooking(error: String) {
        val currentMessage = bookingError.value
        currentMessage.add(error)
        _bookingError.value = ArrayDeque(currentMessage)
    }

    private fun removeHeadQueueTransaction() {
        val currentMessage = transactionError.value
        currentMessage.remove()
        _transactionError.value = ArrayDeque(currentMessage)
    }

    private fun appendErrorMessageTransaction(error: String) {
        val currentMessage = transactionError.value
        currentMessage.add(error)
        _transactionError.value = ArrayDeque(currentMessage)
    }
}