package com.pasukanlangit.cashplus.ui.info_promo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pasukanlangit.cashplus.ui.pages.home.BannerDummy
import com.pasukanlangit.cashplus.ui.pages.home.getBannerDummy
import com.pasukanlangit.cashplus.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class InfoPromoListViewModel : ViewModel() {
    private val _banner = MutableLiveData<MyResponse<List<BannerDummy>>>()
    val banner : LiveData<MyResponse<List<BannerDummy>>> = _banner

    fun getBanner() {
        viewModelScope.launch(Dispatchers.IO) {
            _banner.postValue(MyResponse.loading(null))

            delay(1200)
            _banner.postValue(MyResponse.success(getBannerDummy()))
        }
    }
}