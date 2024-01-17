package com.pasukanlangit.id.downline_nearby_agent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.core.utils.NetworkConnectivity
import com.pasukanlangit.id.domain_downline.model.NearbyAgentResponse
import com.pasukanlangit.id.domain_downline.usecases.DownLineUseCases
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.util.*

class NearbyAgentViewModel(
    private val stateNetwork: NetworkConnectivity,
    private val useCase: DownLineUseCases
): ViewModel(){
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading
    private val _updateLoading = MutableStateFlow(false)
    val updateLoading: StateFlow<Boolean> = _updateLoading

    private val _nearByAgent = MutableStateFlow<NearbyAgentResponse?>(null)
    val nearByAgent: StateFlow<NearbyAgentResponse?> = _nearByAgent
    private val _updateLocation = Channel<Boolean?>()
    val updateLocation = _updateLocation.receiveAsFlow()
    private val _stateError = MutableStateFlow(ArrayDeque<String>())
    val stateError: StateFlow<Queue<String>> = _stateError

    fun onEventClosestAgent(event: ClosestAgentEvent) {
        when(event) {
            is ClosestAgentEvent.GetClosestAgent -> getNearAgent()
            is ClosestAgentEvent.UpdateLatLong -> updateLatLong(event.lat, event.long)
            is ClosestAgentEvent.RemoveHeadMessage -> removeHeadQueue()
        }
    }

    private fun getNearAgent() {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .getNearAgent()
                        .onEach {
                            it.data?.let { nearAgent ->
                                _nearByAgent.value = nearAgent
                            }

                            it.message?.let { error ->
                                appendErrorMessage(error)
                            }

                            _stateLoading.value = it.isLoading
                        }
                        .launchIn(viewModelScope)
                    return
                }
                appendErrorMessage(Constants.networkError)
            }
        })
    }

    private fun updateLatLong(lat: Double, long: Double) {
        stateNetwork.checkInternetConnection(object: NetworkConnectivity.ConnectivityCallback {
            override fun onDetected(isConnected: Boolean) {
                if (isConnected) {
                    useCase
                        .updateLatLong(lat = lat, long = long)
                        .onEach {
                            _updateLoading.value = it.isLoading
                            it.data?.let { data -> _updateLocation.send(data) }
                            it.message?.let { error -> appendErrorMessage(error) }
                        }.launchIn(viewModelScope)
                    return
                }
                appendErrorMessage(Constants.networkError)
            }
        })
    }

    private fun removeHeadQueue() {
        val currentMessage = stateError.value
        currentMessage.remove()
        _stateError.value = ArrayDeque(emptyList())
    }

    private fun appendErrorMessage(error: String) {
        val newMessage = ArrayDeque<String>()
        newMessage.add(error)
        _stateError.value = ArrayDeque(newMessage)
    }
}