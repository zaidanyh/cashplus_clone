package com.pasukanlangit.id.ui_downline_home

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.DownLineItem
import com.pasukanlangit.id.domain_downline.model.DownlineTrxCount
import com.pasukanlangit.id.domain_downline.model.SummaryDownlineResponse
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import com.pasukanlangit.id.ui_downline_home.paging.DownlinePagingSource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

data class QrExpired(
    val isCounting: Boolean = false,
    val currentTickTime: Long = DownLineViewModel.QR_CODE_EXPIRED_TIME
)

class DownLineViewModel(
    private val repository: DownLineRepository,
    private val useCases: DownLineUseCases
): ViewModel() {

    private val _dateStart = MutableStateFlow("")
    private val _dateEnd = MutableStateFlow("")

    private var countdownTimer: CountDownTimer? = null

    private val _isLoading = MutableStateFlow<Boolean?>(false)
    val isLoading: StateFlow<Boolean?> = _isLoading
    private val _summaryDownLine = MutableStateFlow<SummaryDownlineResponse?>(null)
    val summaryDownLine: StateFlow<SummaryDownlineResponse?> = _summaryDownLine
    private val _stateSummaryError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateSummaryError: StateFlow<Queue<String>> = _stateSummaryError

    private val _downLineTrxCount = MutableStateFlow<DownlineTrxCount?>(null)
    val downLineTrxCount: StateFlow<DownlineTrxCount?> = _downLineTrxCount
    private val _stateDownLineTrxCountError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateDownLineTrxCountError: StateFlow<Queue<String>> = _stateDownLineTrxCountError

    private val _isLoadingSetMarkup = MutableStateFlow(false)
    val isLoadingSetMarkup: StateFlow<Boolean> = _isLoadingSetMarkup
    private val _isSetAllMarkup = Channel<Boolean?>()
    val isSetAllMarkup = _isSetAllMarkup.receiveAsFlow()
    private val _isResetSuccess = Channel<Boolean?>()
    val isResetSuccess = _isResetSuccess.receiveAsFlow()
    private val _stateErrorSetMarkup = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateErrorSetMarkup: StateFlow<Queue<String>> = _stateErrorSetMarkup

    private val _isLoadingQr = MutableStateFlow(false)
    val isLoadingQr: StateFlow<Boolean> = _isLoadingQr
    private val _imageQR = MutableStateFlow<String?>(null)
    val imageQR: StateFlow<String?> = _imageQR
    private val _imageQRExpired = MutableStateFlow(QrExpired())
    val imageQRExpired: StateFlow<QrExpired> = _imageQRExpired

    fun onTriggerEvent(event: DownLineEvent) {
        when(event) {
            is DownLineEvent.SetDate -> {
                _dateStart.value = event.dateStart
                _dateEnd.value = event.dateEnd
                getDownLineTrxCount()
                getSummaryDownline()
            }
            is DownLineEvent.RemoveHeadMessageSummary -> removeHeadQueueSummary()
            is DownLineEvent.RemoveHeadMessageTrxCount -> removeHeadQueueTrxCount()
            is DownLineEvent.SetAllProductMarkup ->
                setAllProductMarkup(dest = event.dest, markupValue = event.markupValue)
            is DownLineEvent.ResetMarkup ->  resetMarkup(event.value)
            is DownLineEvent.GenerateQR -> generateQRCode(amount = event.amount)
            is DownLineEvent.RemoveHeadMessage -> removeHeadQueue()
        }
    }

    private fun generateQRCode(amount: String) {
        _imageQR.value = null
        _imageQRExpired.value = QrExpired()

        useCases
            .generateQRCode(amount = amount)
            .onEach {
                it.data?.let { qr ->
                    _imageQR.value = qr.imgCodeUrl
                    startCountingQrExpired()
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }

                _isLoadingQr.value = it.isLoading
            }
            .launchIn(viewModelScope)
    }

    private fun startCountingQrExpired() {
        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(QR_CODE_EXPIRED_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _imageQRExpired.value = QrExpired(
                    isCounting = true,
                    currentTickTime = millisUntilFinished
                )
            }

            override fun onFinish() {
                _imageQRExpired.value = QrExpired(
                    isCounting = false,
                    currentTickTime = 0
                )
            }
        }
        countdownTimer?.start()
    }

    private fun setAllProductMarkup(dest: String, markupValue: String) {
        useCases.setAllProductMarkup(
            dest = dest,
            newMarkupValue = markupValue
        ).onEach {
            it.data?.let { success ->
                _isSetAllMarkup.send(success)
            }
            it.message?.let { error ->
                appendErrorMessage(error)
            }
            _isLoadingSetMarkup.value = it.isLoading
        }.launchIn(viewModelScope)
    }

    private fun resetMarkup(value: String) {
        useCases
            .resetMarkup(
              markUpValue = value
            )
            .onEach {
                it.data?.let { isSuccess ->
                    _isResetSuccess.send(isSuccess)
                }
                it.message?.let { error ->
                    appendErrorMessage(error)
                }
                _isLoadingSetMarkup.value = it.isLoading
            }
            .launchIn(viewModelScope)
    }

    fun getDownLine(type: String, value: String): Flow<PagingData<DownLineItem>> =
    Pager(PagingConfig(pageSize = 1)) {
        DownlinePagingSource(
            repository = repository,
            dateStart = _dateStart.value,
            dateEnd = _dateEnd.value,
            type = type,
            value = value
        )
    }.flow.cachedIn(viewModelScope)

    private fun getSummaryDownline() {
        _isLoading.value = null
        _summaryDownLine.value = null
        useCases
            .getSummaryDownLine(
                dateStart = _dateStart.value,
                dateEnd = _dateEnd.value
            ).onEach {
                it.data?.let { summary ->
                    _summaryDownLine.value = summary
                }
                it.message?.let { error ->
                    appendErrorMessageSummary(error)
                }
                _isLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun getDownLineTrxCount() {
        _isLoading.value = null
        _downLineTrxCount.value = null
        useCases
            .getDownLineTrxCount(
                dateStart = _dateStart.value,
                dateEnd = _dateEnd.value
            ).onEach {
                it.data?.let { trxCount ->
                    _downLineTrxCount.value = trxCount
                }
                it.message?.let { error ->
                    appendErrorMessageTrxCount(error)
                }
                _isLoading.value = it.isLoading
            }.launchIn(viewModelScope)
    }

    private fun removeHeadQueue() {
        val currentMessage = stateErrorSetMarkup.value
        currentMessage.remove()
        _stateErrorSetMarkup.value = ArrayDeque(currentMessage)
    }
    private fun appendErrorMessage(error: String) {
        val currentMessage = stateErrorSetMarkup.value
        currentMessage.add(error)
        _stateErrorSetMarkup.value = ArrayDeque(currentMessage)
    }

    private fun removeHeadQueueTrxCount() {
        val currentMessage = stateDownLineTrxCountError.value
        currentMessage.remove()
        _stateDownLineTrxCountError.value = ArrayDeque(currentMessage)
    }
    private fun appendErrorMessageTrxCount(error: String) {
        val currentMessage = stateDownLineTrxCountError.value
        currentMessage.add(error)
        _stateDownLineTrxCountError.value = ArrayDeque(currentMessage)
    }

    private fun removeHeadQueueSummary() {
        val current = stateSummaryError.value
        current.remove()
        _stateSummaryError.value = ArrayDeque(current)
    }
    private fun appendErrorMessageSummary(error: String) {
        val current = stateSummaryError.value
        current.add(error)
        _stateSummaryError.value = ArrayDeque(current)
    }

    companion object {
        const val QR_CODE_EXPIRED_TIME: Long = 1000*60*2 // 2 minutes
    }
}