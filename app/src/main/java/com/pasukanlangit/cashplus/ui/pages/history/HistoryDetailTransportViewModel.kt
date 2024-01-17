package com.pasukanlangit.cashplus.ui.pages.history

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.BuildConfig
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class HistoryDetailTransportViewModel(
    private val mainRepository: MainRepository,
    private val context: Context
): ViewModel() {
    private val _file = MutableLiveData<MyResponse<ResponseBody>>()
    val file : LiveData<MyResponse<ResponseBody>> = _file

    fun downloadFile(data: String) = viewModelScope.launch(
        Dispatchers.IO)  {
        _file.postValue(MyResponse.loading(null))

        try{
            val response = mainRepository.downloadFile("${BuildConfig.BASE_URL_CASHPLUS_WEB}api/print/transportation", data)

            if(response?.code() == 200){
                _file.postValue(MyResponse.success(response.body()))
            }else{
                _file.postValue(MyResponse.error(context.getString(R.string.exception_gson), null))
            }
        }catch (ex : Exception){
            _file.postValue(MyResponse.error(context.getString(R.string.exception_network), null))
        }
    }
}