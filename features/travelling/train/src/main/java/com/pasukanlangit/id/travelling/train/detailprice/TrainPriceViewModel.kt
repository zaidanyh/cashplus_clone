package com.pasukanlangit.id.travelling.train.detailprice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.travelling.domain.model.BalanceResponse
import com.pasukanlangit.id.travelling.domain.model.BookingCodeResponse
import com.pasukanlangit.id.travelling.domain.model.TransactionResponse
import com.pasukanlangit.id.travelling.domain.model.train.TrainPriceResponse
import com.pasukanlangit.id.travelling.domain.usecases.TravellingUseCases
import com.pasukanlangit.id.travelling.train.temp.Passengers
import com.pasukanlangit.id.travelling.train.temp.TrainBooking
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class TrainPriceViewModel(
    private val useCases: TravellingUseCases
): ViewModel() {
    private val _balance = Channel<BalanceResponse?>()
    val balance get() = _balance.receiveAsFlow()
    private val _loadingPrice = MutableStateFlow(false)
    val loadingPrice: StateFlow<Boolean> get() = _loadingPrice
    private val _trainPrice = MutableStateFlow<TrainPriceResponse?>(null)
    val trainPrice: StateFlow<TrainPriceResponse?> get() = _trainPrice
    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> get() = _stateError

    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> get() = _stateLoading
    private val _trainBooking = Channel<BookingCodeResponse?>()
    val trainBooking get() = _trainBooking.receiveAsFlow()
    private val _bookingError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val bookingError: StateFlow<Queue<String>> get() = _bookingError

    private val _transactionLoading = MutableStateFlow(false)
    val transactionLoading: StateFlow<Boolean> get() = _transactionLoading
    private val _transactionBooking = Channel<TransactionResponse?>()
    val transactionBooking get() = _transactionBooking.receiveAsFlow()
    private val _transactionError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val transactionError: StateFlow<Queue<String>> get() = _transactionError

    fun onTriggerEvents(event: TrainPriceEvent) {
        when(event) {
            is TrainPriceEvent.RemoveHeadMessage -> removeHeadQueue()
            is TrainPriceEvent.TrainPrice -> trainPrice(
                event.from, event.to, event.date,
                event.passenger, event.trainBooking
            )
            is TrainPriceEvent.RemoveHeadBookingMessage -> removeHeadQueueBooking()
            is TrainPriceEvent.TrainBookingTicket -> trainBooking(
                event.idNumbers, event.phone, event.email, event.from, event.to, event.date,
                event.passengersName, event.passenger, event.trainBooking
            )
            is TrainPriceEvent.RemoveHeadTransactionMessage -> removeHeadQueueTrx()
            is TrainPriceEvent.BookingTransaction -> bookingTransaction(
                event.codeProduct, event.destination, event.pin
            )
        }
    }

    private fun trainPrice(
        from: String, to: String, date: String,
        passenger: Passengers?, trainBooking: TrainBooking?
    ) {
        useCases
            .trainPrice(
                session = trainBooking?.session, from = from, to = to, date = date,
                adult = passenger?.adult.toString(), infant = passenger?.infant.toString(),
                trainCode = trainBooking?.trainCode, trainClass = trainBooking?.trainClass,
                trainSubClass = trainBooking?.trainSubClass
            ).onEach {
                it.data.let { data ->
                    _balance.send(data?.first)
                    _trainPrice.value = data?.second
                }
                it.message?.let { error -> appendErrorMessage(error) }
                _loadingPrice.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun trainBooking(
         idNumbers: String, phone: String, email: String, from: String, to: String,
         date: String, passengersName: String, passenger: Passengers, trainBooking: TrainBooking
    ) {
        useCases
            .trainBooking(
                session = trainBooking.session, from = from, to = to, date = date, adult = passenger.adult.toString(),
                infant = passenger.infant.toString(), trainCode = trainBooking.trainCode, trainClass = trainBooking.trainClass,
                trainSubClass = trainBooking.trainSubClass, passengerName = passengersName, idNumber = idNumbers,
                phone = phone, email = email
            ).onEach {
                it.data.let { data -> _trainBooking.send(data) }
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