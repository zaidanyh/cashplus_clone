package com.pasukanlangit.cashplus.ui.omni.packageomni

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.domain.model.OmniMenuResponse
import com.pasukanlangit.cashplus.domain.model.OmniPackageListResponse
import com.pasukanlangit.cashplus.domain.model.OmniPackageOrderResponse
import com.pasukanlangit.cashplus.domain.usecase.AppUseCases
import com.pasukanlangit.cashplus.model.response.TransactionTAGResponse
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.core.model.BalanceResponseCore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.ArrayDeque
import java.util.Queue

class PackageOmniViewModel(
    private val useCases: AppUseCases,
    private val networkConnectivity: NetworkConnectivity,
    private val context: Context
): ViewModel() {

    private val _loadingBalance = MutableStateFlow(false)
    val loadingBalance: StateFlow<Boolean> get() = _loadingBalance
    private val _balance = Channel<BalanceResponseCore?>()
    val balance get() = _balance.receiveAsFlow()
    private val _errorBalance = MutableStateFlow(ArrayDeque<String>())
    val errorBalance: StateFlow<Queue<String>> get() = _errorBalance

    private val _loadingMenu = MutableStateFlow(false)
    val loadingMenu: StateFlow<Boolean> get() = _loadingMenu
    private val _menus = MutableStateFlow<OmniMenuResponse?>(null)
    val menus: StateFlow<OmniMenuResponse?> get() = _menus
    private val _errorMenu = MutableStateFlow(ArrayDeque<String>())
    val errorMenu: StateFlow<Queue<String>> get() = _errorMenu

    private val _loadingPackage = MutableStateFlow(false)
    val loadingPackage: StateFlow<Boolean> get() = _loadingPackage
    private val _packages = MutableStateFlow<OmniPackageListResponse?>(null)
    val packages: StateFlow<OmniPackageListResponse?> get() = _packages
    private val _errorPackage = MutableStateFlow(ArrayDeque<String>())
    val errorPackage: StateFlow<Queue<String>> get() = _errorPackage

    private val _orderLoading = MutableStateFlow(false)
    val orderLoading: StateFlow<Boolean> get() = _orderLoading
    private val _packageOrder = Channel<OmniPackageOrderResponse?>()
    val packageOrder get() = _packageOrder.receiveAsFlow()
    private val _orderError = MutableStateFlow(ArrayDeque<String>())
    val orderError: StateFlow<Queue<String>> get() = _orderError

    private val _trxLoading = MutableStateFlow(false)
    val trxLoading: StateFlow<Boolean> get() = _trxLoading
    private val _trxResponse = Channel<TransactionTAGResponse?>()
    val trxResponse get() = _trxResponse.receiveAsFlow()
    private val _trxError = MutableStateFlow(ArrayDeque<String>())
    val trxError: StateFlow<Queue<String>> get() = _trxError

    fun onTriggersEvent(events: PackageOmniEvents) {
        when(events) {
            is PackageOmniEvents.FetchMenu -> {
                getBalance(uuid = events.uuid, accessToken = events.accessToken)
                fetchMenu(uuid = events.uuid, dest = events.dest, accessToken = events.accessToken)
            }
            is PackageOmniEvents.RemoveFetchMenuError -> removeMenuError()
            is PackageOmniEvents.RemoveBalanceMessage -> removeBalanceMessage()
            is PackageOmniEvents.FetchPackage -> {
                fetchPackages(
                    uuid = events.uuid, dest = events.dest,
                    mlid = events.mlId, accessToken = events.accessToken
                )
            }
            is PackageOmniEvents.RemovePackageMessage -> removePackageMessage()
            is PackageOmniEvents.PackageOrder ->
                packageOrder(uuid = events.uuid, dest = events.dest, packageId = events.packageId, accessToken = events.accessToken)
            is PackageOmniEvents.RemovePackageOrderMessage -> removePackageOrderMessage()
            is PackageOmniEvents.PackageTransaction ->
                packageTransaction(
                    uuid = events.uuid, codeProduct = events.codeProduct, dest = events.dest,
                    pin = events.pin, accessToken = events.accessToken
                )
            is PackageOmniEvents.RemovePackageTransactionMessage -> removeTrxMessage()
        }
    }

    private fun getBalance(uuid: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                   useCases
                       .getBalance(uuid = uuid, accessToken = accessToken)
                       .onEach {
                           it.data?.let { data -> _balance.send(data) }
                           it.message?.let { message -> appendErrorBalanceMessage(message) }
                           _loadingBalance.value = it.isLoading
                       }.launchIn(viewModelScope)
                    return
                }
                appendErrorBalanceMessage(context.getString(R.string.exception_network))
            }
        })
    }

    private fun fetchMenu(uuid: String, dest: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .omniMenuUseCase(uuid = uuid, dest = dest, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data ->
                                _menus.value = data.first
                                _packages.value = data.second
                            }
                            it.message?.let { message -> appendErrorMenuMessage(message) }
                            _loadingMenu.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorMenuMessage(context.getString(R.string.exception_network))
            }
        })
    }

    private fun fetchPackages(uuid: String, dest: String, mlid: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .omniPackageList(uuid = uuid, dest = dest, mlid = mlid, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _packages.value = data }
                            it.message?.let { error -> appendErrorMessage(error) }
                            _loadingPackage.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorMessage(context.getString(R.string.exception_network))
            }
        })
    }

    private fun packageOrder(uuid: String, dest: String, packageId: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .omniPackageOrder(uuid = uuid, dest = dest, packageId = packageId, accessToken = accessToken)
                        .onEach {
                            it.data?.let { data -> _packageOrder.send(data) }
                            it.message?.let { error -> appendErrorOrderMessage(error) }
                            _orderLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorOrderMessage(context.getString(R.string.exception_network))
            }
        })
    }

    private fun packageTransaction(uuid: String, codeProduct: String, dest: String, pin: String, accessToken: String) {
        networkConnectivity.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCases
                        .billPayTransaction(
                            uuid = uuid, codeProduct = codeProduct, destination = dest,
                            pin = pin, accessToken = accessToken
                        )
                        .onEach {
                            it.data?.let { data -> _trxResponse.send(data) }
                            it.message?.let { error -> appendErrorTrxMessage(error) }
                            _trxLoading.value = it.isLoading
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorTrxMessage(context.getString(R.string.exception_network))
            }
        })
    }

    private fun appendErrorMenuMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _errorMenu.value = newMessage
    }

    private fun removeMenuError() {
        val currentMessage = errorMenu.value
        currentMessage.remove()
        _errorMenu.value = ArrayDeque(emptyList())
    }

    private fun appendErrorMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _errorPackage.value = newMessage
    }
    private fun removePackageMessage() {
        val currentMessage = errorPackage.value
        currentMessage.remove()
        _errorPackage.value = ArrayDeque(emptyList())
    }

    private fun appendErrorBalanceMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _errorBalance.value = newMessage
    }
    private fun removeBalanceMessage() {
        val currentMessage = errorBalance.value
        currentMessage.remove()
        _errorBalance.value = ArrayDeque(emptyList())
    }

    private fun appendErrorOrderMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _orderError.value = newMessage
    }
    private fun removePackageOrderMessage() {
        val currentMessage = orderError.value
        currentMessage.remove()
        _orderError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorTrxMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _trxError.value = newMessage
    }

    private fun removeTrxMessage() {
        val currentMessage = trxError.value
        currentMessage.remove()
        _trxError.value = ArrayDeque(emptyList())
    }
}