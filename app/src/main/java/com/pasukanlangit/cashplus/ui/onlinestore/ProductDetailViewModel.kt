package com.pasukanlangit.cashplus.ui.onlinestore

import androidx.lifecycle.*
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductDetailViewModel(private val mainRepository: MainRepository,private var sharedPref: SharedPrefDataSource) : ViewModel(){

    private val _productsInCart = MutableLiveData<List<ProductStoreDataItem>>()
    val productsInCart : LiveData<List<ProductStoreDataItem>> = _productsInCart

    fun getAllProductCarts() = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.getProductCart(sharedPref.getPhoneNumber() ?: "-1").collect {
            _productsInCart.postValue(it)
        }
    }

    fun insertProductToCart(productStoreDataItem: ProductStoreDataItem) = viewModelScope.launch(Dispatchers.IO) {
        productStoreDataItem.userId = sharedPref.getPhoneNumber() ?: "-1"
        mainRepository.insertProductToCart(productStoreDataItem)
    }
}