package com.pasukanlangit.cashplus.ui.pages.history

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.domain.usecase.HistoryPagingSource
import com.pasukanlangit.cashplus.domain.usecase.HistoryTopUpPagingSource
import com.pasukanlangit.cashplus.model.response.HistoryTopUpDataItem
import com.pasukanlangit.cashplus.model.response.TransactionHistoryResponse
import com.pasukanlangit.cashplus.model.response.TransactionItem
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import kotlinx.coroutines.flow.*
import java.util.ArrayDeque
import java.util.Queue

class HistoryViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val repository: MainRepository,
    private val context: Context
): ViewModel() {
    private val _dateHistory = MutableStateFlow<DateHistory?>(null)
    val dateHistory: StateFlow<DateHistory?> = _dateHistory

    private val _isSearch = MutableStateFlow(false)
    val isSearch: StateFlow<Boolean> = _isSearch

    private val _searchLoading = MutableStateFlow(false)
    val searchLoading: StateFlow<Boolean> = _searchLoading
    private val _searchTransaction = MutableStateFlow<TransactionHistoryResponse?>(null)
    val searchTransaction: StateFlow<TransactionHistoryResponse?> = _searchTransaction
    private val _searchError = MutableStateFlow(ArrayDeque<String>())
    val searchError: StateFlow<Queue<String>> = _searchError

    fun setDate(dateHistory: DateHistory) {
        _isSearch.value = false
        _dateHistory.value = dateHistory
    }

    fun historyTransaction(
        uuid: String, accessToken: String, dateStart: String, dateEnd: String
    ): Flow<PagingData<TransactionItem>> {
        _isSearch.value = false
        return Pager(PagingConfig(1)) {
            HistoryPagingSource(
                repository = repository, uuid = uuid, accessToken = accessToken,
                dateStart = dateStart, dateEnd = dateEnd
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun historyTopUp(
        uuid: String, accessToken: String, dateStart: String, dateEnd: String
    ): Flow<PagingData<HistoryTopUpDataItem>> = Pager(PagingConfig(1)) {
        HistoryTopUpPagingSource(
            repository = repository, uuid = uuid, accessToken = accessToken,
            dateStart = dateStart, dateEnd = dateEnd
        )
    }.flow.cachedIn(viewModelScope)

    fun searchTransactions(uuid: String, dest: String, token: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .searchHistoryTransaction(uuid = uuid, dest = dest, accessToken = token)
                        .onEach {
                            it.data?.let { data ->
                                _dateHistory.value = null
                                _isSearch.value = true
                                _searchTransaction.value = data
                            }
                            it.message?.let { error -> appendSearchErrorMessage(error) }
                            _searchLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendSearchErrorMessage(context.getString(R.string.exception_network))
            }
        })
    }

    private fun appendSearchErrorMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _searchError.value = newMessage
    }

    fun removeSearchErrorMessage() {
        val currentMessage = searchError.value
        currentMessage.remove()
        _searchError.value = ArrayDeque(emptyList())
    }
}