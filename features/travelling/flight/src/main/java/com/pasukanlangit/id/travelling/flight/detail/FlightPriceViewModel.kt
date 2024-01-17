package com.pasukanlangit.id.travelling.flight.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.TransactionResponse
import com.pasukanlangit.id.travelling.domain.model.BalanceResponse
import com.pasukanlangit.id.travelling.domain.model.BookingCodeResponse
import com.pasukanlangit.id.travelling.domain.model.flight.FlightPriceResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class FlightPriceViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _balance = Channel<BalanceResponse?>()
    val balance get() = _balance.receiveAsFlow()
    private val _loadingPrice = MutableStateFlow(false)
    val loadingPrice: StateFlow<Boolean> get() = _loadingPrice
    private val _flightPrice = MutableStateFlow<FlightPriceResponse?>(null)
    val flightPrice: StateFlow<FlightPriceResponse?> get() = _flightPrice
    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> get() = _stateError

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> get() = _stateLoading
    private val _flightBooking = Channel<BookingCodeResponse?>()
    val flightBooking get() = _flightBooking.receiveAsFlow()
    private val _bookingError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val bookingError: StateFlow<Queue<String>> get() = _bookingError

    private val _transactionLoading = MutableStateFlow(false)
    val transactionLoading: StateFlow<Boolean> get() = _transactionLoading
    private val _transactionBooking = Channel<TransactionResponse?>()
    val transactionBooking get() = _transactionBooking.receiveAsFlow()
    private val _transactionError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val transactionError: StateFlow<Queue<String>> get() = _transactionError

    fun onTriggersEvent(event: FlightPriceEvent) {
        when(event) {
            is FlightPriceEvent.FlightPrice -> flightPrice(
                event.departureCode, event.destinationCode, event.date, event.flightCode,
                event.adult, event.child, event.infant
            )
            is FlightPriceEvent.RemoveHeadMessage -> removeHeadQueue()
            is FlightPriceEvent.FlightBooking -> flightBooking(
                event.from, event.to, event.date, event.flightCode, event.adult, event.child,
                event.infant, event.email, event.phone, event.passengersName, event.passengersDateOfBirth,
                event.baggageVolumes
            )
            is FlightPriceEvent.RemoveHeadMessageBooking -> removeHeadQueueBooking()
            is FlightPriceEvent.BookingTransaction -> bookingTransaction(
                event.codeProduct, event.destination, event.pin
            )
            is FlightPriceEvent.RemoveHeadMessageTransaction -> removeHeadQueueTrx()
        }
    }

    private fun flightPrice(
        departureCode: String, destinationCode: String, date: String, flightCode: String,
        adult: String, child: String, infant: String
    ) {
        useCases
            .flightPrice(
                from = departureCode, to = destinationCode, date = date,
                flightCode = flightCode, adult = adult, child = child,
                infant = infant
            ).onEach {
                it.data.let { data ->
                    _balance.send(data?.first)
                    _flightPrice.value = data?.second
                }
                it.message?.let { error -> appendErrorMessage(error) }
                _loadingPrice.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun flightBooking(
        from: String, to: String, date:String, flightCode: String, adult: String, child: String,
        infant: String, email: String, phone: String, passengersName: String,
        passengersDateOfBirth: String, baggageVolumes: String
    ) {
        useCases
            .flightBooking(
                from = from, to = to, date = date, flightCode = flightCode, adult = adult, child = child,
                infant = infant, email = email, phone = phone, passengersName = passengersName,
                passengersDateOfBirth = passengersDateOfBirth, baggageVolumes = baggageVolumes
            ).onEach {
                it.data.let { data -> _flightBooking.send(data) }
                it.message?.let { error -> appendErrorMessageBooking(error) }
                _stateLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun bookingTransaction(
        codeProduct: String, destination: String?, pin: String
    ) {
        useCases
            .booking(
                codeProduct = codeProduct, destination = destination.toString(), pin = pin
            ).onEach {
                it.data.let { data -> _transactionBooking.send(data) }
                it.message?.let { error -> appendErrorTrx(error) }
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

    private fun removeHeadQueueTrx() {
        val currentMessage = transactionError.value
        currentMessage.remove()
        _transactionError.value = ArrayDeque(currentMessage)
    }

    private fun appendErrorTrx(error: String) {
        val currentMessage = transactionError.value
        currentMessage.add(error)
        _transactionError.value = ArrayDeque(currentMessage)
    }
}