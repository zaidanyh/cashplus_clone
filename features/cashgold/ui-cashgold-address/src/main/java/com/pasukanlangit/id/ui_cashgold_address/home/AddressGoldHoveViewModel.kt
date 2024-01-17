package com.pasukanlangit.id.ui_cashgold_address.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.id.model.Address
import com.pasukanlangit.id.usecase.CashGoldUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*

class AddressGoldHoveViewModel(
    private val useCases: CashGoldUseCase
): ViewModel(){
    private val _stateLoading = MutableStateFlow(false)
    val stateLoading: StateFlow<Boolean> = _stateLoading

    private val _stateError = MutableStateFlow(ArrayDeque<String>(listOf()))
    val stateError: StateFlow<Queue<String>> = _stateError

    private val _stateAddressList = MutableStateFlow<List<Address>?>(null)
    val stateAddressList: StateFlow<List<Address>?> = _stateAddressList

    fun onTriggerEvent(event: AddressGoldHomeEvent){
        when(event){
            is AddressGoldHomeEvent.GetAddressList -> {
                getAddressList()
            }
            is AddressGoldHomeEvent.RemoveHeadQueue -> {
                removeHeadQueue()
            }
            is AddressGoldHomeEvent.UpdateAddress -> {
                updateAddress(
                    event.id,
                    event.address,
                    event.city,
                    event.district,
                    event.province,
                    event.village,
                    event.zipCode,
                    event.isMain
                )
            }
            is AddressGoldHomeEvent.DeleteAddress -> {
                deleteAddress(event.id)
            }
        }
    }

    private fun deleteAddress(id: Int) {
        useCases
            .deleteAddress(
                id = id
            )
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { deleteStatus ->
                    if(deleteStatus){
                        getAddressList()
                    }
                }

                it.message?.let { message ->
                    appendErrorMessage(message)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun updateAddress(id: Int, address: String, city: String, district: String, province: String, village: String, zipCode: String, main: Boolean) {
        useCases
            .updateAddress(
                address = address,
                city = city,
                district = district,
                province = province,
                village = village,
                zipCode = zipCode,
                isMain = main,
                id = id
            )
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { updateStatus ->
                    if(updateStatus){
                        getAddressList()
                    }
                }

                it.message?.let { message ->
                    appendErrorMessage(message)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getAddressList() {
        useCases
            .addressList()
            .onEach {
                _stateLoading.value = it.isLoading
                it.data?.let { data ->
                    _stateAddressList.value = null
                    _stateAddressList.value = data
                }

                it.message?.let { errorMessage ->
                    appendErrorMessage(errorMessage)
                }
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