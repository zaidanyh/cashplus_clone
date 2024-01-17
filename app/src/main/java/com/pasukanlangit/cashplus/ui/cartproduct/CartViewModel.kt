package com.pasukanlangit.cashplus.ui.cartproduct

import androidx.lifecycle.*
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CartViewModel (private val mainRepository: MainRepository, private val sharedPref: SharedPrefDataSource) : ViewModel() {
    private val _productCarts = MutableLiveData<List<ProductStoreDataItem>>()
    val productCarts : LiveData<List<ProductStoreDataItem>> = _productCarts


    fun getAllProductCarts() = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.getProductCart(sharedPref.getPhoneNumber() ?: "-1").collect {
            _productCarts.postValue(it)
        }
    }

    fun deleteProductFromCart(productStoreDataItem: ProductStoreDataItem) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.deleteProductFromCart(productStoreDataItem, sharedPref.getPhoneNumber() ?: "-1")
    }

    fun updateCartsIsChecked(value: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.updateCartsIsChecked(value, sharedPref.getPhoneNumber() ?: "-1")
    }

    fun updateCartDb(productStoreDataItem: ProductStoreDataItem) = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.updateCart(productStoreDataItem, sharedPref.getPhoneNumber() ?: "-1")
    }
}
