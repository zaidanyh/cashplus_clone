package com.pasukanlangit.cashplus.ui.pembayaran_game_menu

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.model.request_body.ProductRequest
import com.pasukanlangit.id.core.datasource.network.ErrorMessageResponse
import com.pasukanlangit.cashplus.model.response.ProductModel
import com.pasukanlangit.cashplus.model.response.ProductResultModel
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.IOException

class GameMenuViewModel(private val mainRepository: MainRepository,private val context: Context): ViewModel() {
    private val _menuGames = MutableLiveData<MyResponse<ProductResultModel>>()
    val menuGames: LiveData<MyResponse<ProductResultModel>> = _menuGames

    private val _gamesSearch = MutableLiveData<MyResponse<List<ProductModel>>>()
    val gamesSearch: LiveData<MyResponse<List<ProductModel>>> = _gamesSearch

    private val searchInput = MutableStateFlow("")

    fun searchVoucherGame(keyword: String) {
        searchInput.value = keyword
        observeSearch()
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearch() {
        searchInput
            .debounce(800)
            .distinctUntilChanged()
            .mapLatest { keyword ->
                filterSearch(keyword)
            }.launchIn(viewModelScope)
    }

    private fun filterSearch(keyword: String) {
        if(_menuGames.value?.data == null) return
        _gamesSearch.postValue(MyResponse.loading(data = null))

        val gameSearch = menuGames.value?.data?.data?.filter {
            it.kode_produk.replace("_", " ").contains(keyword, ignoreCase = true) || it.short_dsc.contains(keyword, ignoreCase = true)
        } ?: listOf()

        _gamesSearch.postValue(MyResponse.success(data = gameSearch))
    }

    fun getMenu(productRequest: ProductRequest, accessToken: String){
        viewModelScope.launch(Dispatchers.IO)  {
            _menuGames.postValue(MyResponse.loading(null))

            try{
                val response = mainRepository.getProductList(productRequest, accessToken)

                if(response.code() == 200){
                    _menuGames.postValue(MyResponse.success(response.body()))

                    if(searchInput.value.isNotEmpty()) {
                        filterSearch(keyword = searchInput.value)
                    } else {
                        _gamesSearch.postValue(
                            MyResponse.success(
                                response.body()?.data ?: listOf()
                            )
                        )
                    }
                }else{
                    val gson: Gson = GsonBuilder().create()
                    try {
                        val errorBody =
                            gson.fromJson(response.errorBody()!!.string(), ErrorMessageResponse::class.java)
                        val message: String= errorBody.msg ?: errorBody.message ?: throw IOException()

                        _menuGames.postValue(MyResponse.error(message, null))
                    } catch (e: IOException) {
                        _menuGames.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
                    }

                }
            }catch (ex : Exception){
                _menuGames.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
            }
        }
    }
}