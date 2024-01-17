package com.pasukanlangit.cashplus.view_model

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.model.response.CategoryProductResponse
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.ui.onlinestore.AllProductPagingSource
import com.pasukanlangit.cashplus.ui.onlinestore.ProductSearchPagingSource
import com.pasukanlangit.cashplus.utils.MyResponse
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AllProductEcommerceViewModel(
    private val mainRepository: MainRepository,
    private val sharedPref: SharedPrefDataSource,
    private val context: Context
) : ViewModel() {
    private val _categories = MutableLiveData<MyResponse<CategoryProductResponse>>()
    val categories : LiveData<MyResponse<CategoryProductResponse>> = _categories

    private val _productsInCart = MutableLiveData<List<ProductStoreDataItem>>()
    val productsInCart : LiveData<List<ProductStoreDataItem>> = _productsInCart

    fun getAllProductCarts() = viewModelScope.launch(Dispatchers.IO) {
        mainRepository.getProductCart(sharedPref.getPhoneNumber() ?: "-1").collect {
            _productsInCart.postValue(it)
        }
    }

    fun getCategoriesProduct(){
        viewModelScope.launch(Dispatchers.IO)  {
            _categories.postValue(MyResponse.loading(null))

            try{
                val uuid = sharedPref.getUUID() ?: throw Exception()
                val response = mainRepository.categoryProduct(ProfileRequest(uuid))

                if(response.code() == 200){
                    _categories.postValue(MyResponse.success(response.body()))
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw java.io.IOException()

                        _categories.postValue(MyResponse.error(message, null))
                    } catch (e: java.io.IOException) {
                        _categories.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
                    }

                }
            }catch (ex : Exception){
                _categories.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }

    fun getProductsPaging(token: String, uuid: String, categoryName: String?) : Flow<PagingData<ProductStoreDataItem>> {
        return Pager(PagingConfig(pageSize = 1)){
            AllProductPagingSource(mainRepository,uuid,token, categoryName)
        }.flow.cachedIn(viewModelScope)
    }

    fun searchProductPagging(token: String, uuid: String, keyword: String?): Flow<PagingData<ProductStoreDataItem>>{
        return Pager(PagingConfig(pageSize = 1)){
            ProductSearchPagingSource(mainRepository, uuid, token, keyword)
        }.flow.cachedIn(viewModelScope)
    }

//    fun getProducts(token: String, productStoreRequest: ProductStoreRequest){
//        viewModelScope.launch(Dispatchers.IO)  {
//            _products.postValue(MyResponse.loading(null))
//
//            try{
//                val response = mainRepository.getProductStore(token, productStoreRequest)
//
//                if(response.code() == 200){
//                    _products.postValue(MyResponse.success(response.body()))
//                }else{
//                    val gson: Gson = GsonBuilder().create()
//                    try {
//                        val errorBody =
//                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
//                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()
//
//                        _products.postValue(MyResponse.error(message, null))
//                    } catch (e: IOException) {
//                        _products.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
//                    }
//
//                }
//            }catch (ex : Exception){
//                _products.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
//            }
////            if(MyUtils.isOnline()){
////
////            }else{
////                _products.postValue(MyResponse.error("Maaf internet terputus, silahkan cek jaringan atau refresh jaringan", null))
////            }
//        }
//    }

}