package com.pasukanlangit.id.ui_downline_mutasi_summary_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.core.presentation.MutationSumDetPageType
import com.pasukanlangit.id.domain_downline.model.DetailTransferDownlineResponse
import com.pasukanlangit.id.domain_downline.model.DownLineMutationResponse
import com.pasukanlangit.id.domain_downline.model.SummaryTransferResponse
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class MutationSumDetViewModel(
    private val useCases: DownLineUseCases
): ViewModel(){

    private val _balanceMutation = MutableStateFlow<List<DownLineMutationResponse>?>(null)
    val balanceMutation: StateFlow<List<DownLineMutationResponse>?> = _balanceMutation

    private val _detailTransfer = MutableStateFlow<List<DetailTransferDownlineResponse>?>(null)
    val detailTransfer: StateFlow<List<DetailTransferDownlineResponse>?> = _detailTransfer

    private val _summaryTransfer = MutableStateFlow<List<SummaryTransferResponse>?>(null)
    val summaryTransfer: StateFlow<List<SummaryTransferResponse>?> = _summaryTransfer

    private val _isLoadingButton = MutableStateFlow(false)
    val isLoadingButton: StateFlow<Boolean> = _isLoadingButton

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _pageType = MutableStateFlow(MutationSumDetPageType.CEK_MUTATION)
    val pageType: StateFlow<MutationSumDetPageType> = _pageType

    private val _downlinePhoneNumber = MutableStateFlow("")
    private val _isFromSubDownLine = MutableStateFlow(false)
    private val _dateStart = MutableStateFlow("")
    private val _dateEnd = MutableStateFlow("")


    fun onTriggerEvent(event: MutasiSumDetEvent){
        when(event){
            is MutasiSumDetEvent.SetDate -> {
                _dateStart.value = event.dateStart
                _dateEnd.value = event.dateEnd
            }
            is MutasiSumDetEvent.RemoveHeadMessageQueue -> removeHeadQueue()
            is MutasiSumDetEvent.SetPhoneNumberDownLine -> {
                _downlinePhoneNumber.value =  event.phoneNumberDownLine
            }
            is MutasiSumDetEvent.OnSubmit -> {
                when(pageType.value){
                    MutationSumDetPageType.CEK_MUTATION -> {
                        getMutationDownLine()
                    }
                    MutationSumDetPageType.CEK_DETAIL_TRANSFER -> {
                        getDetailTransfer()
                    }
                    MutationSumDetPageType.CEK_SUMMARY_TRANSFER -> {
                        getSummaryTransfer()
                    }
                }
            }
            is MutasiSumDetEvent.SetPageType -> {
                _pageType.value = event.mutationSumDetPageType
            }
            is MutasiSumDetEvent.SetPageFrom -> {
                _isFromSubDownLine.value = event.isFromSubDownLine
            }
        }
    }

    private fun getSummaryTransfer() {
        _summaryTransfer.value = null
        useCases
            .getSummaryTransferDownLine(
                dateStart = _dateStart.value,
                dateEnd = _dateEnd.value,
                downLinePhone = _downlinePhoneNumber.value
            )
            .onEach {
                it.data?.let { summaryTransfer ->
                    _summaryTransfer.value = summaryTransfer
                }

                it.message?.let { error ->
                    appendErrorMessage(error)
                }

                _isLoadingButton.value = it.isLoading
            }
            .launchIn(viewModelScope)
    }

    private fun getDetailTransfer() {
        _detailTransfer.value = null
        useCases
            .getDetailTransferDownLine(
                dateStart = _dateStart.value,
                dateEnd = _dateEnd.value,
                downLinePhone = _downlinePhoneNumber.value
            )
            .onEach {
                it.data?.let { transferDetail ->
                    _detailTransfer.value = transferDetail
                }

                it.message?.let { error ->
                    appendErrorMessage(error)
                }

                _isLoadingButton.value = it.isLoading
            }
            .launchIn(viewModelScope)
    }

    private fun getMutationDownLine() {
        _balanceMutation.value = null
        useCases
            .getMutationDownLine(
                dateStart = _dateStart.value,
                dateEnd = _dateEnd.value,
                downLinePhone = _downlinePhoneNumber.value,
                isFromSubDownLine = _isFromSubDownLine.value
            )
            .onEach {
                it.data?.let { mutation ->
                    _balanceMutation.value = mutation
                }

                it.message?.let { error ->
                    appendErrorMessage(error)
                }

                _isLoadingButton.value = it.isLoading
            }
            .launchIn(viewModelScope)
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