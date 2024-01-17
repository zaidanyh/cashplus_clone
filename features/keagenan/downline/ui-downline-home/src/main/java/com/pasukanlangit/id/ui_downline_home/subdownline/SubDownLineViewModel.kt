package com.pasukanlangit.id.ui_downline_home.subdownline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.DownLineItem
import com.pasukanlangit.id.ui_downline_home.paging.SubDownLinePagingSource
import kotlinx.coroutines.flow.*

class SubDownLineViewModel(
    private val repository: DownLineRepository
): ViewModel() {

    private val _dateStart = MutableStateFlow("")
    private val _dateEnd = MutableStateFlow("")

    fun onTriggerEvent(event: SubDownLineEvent) {
        when(event){
            is SubDownLineEvent.SetDatePicker -> {
                _dateStart.value = event.dateStart
                _dateEnd.value = event.dateEnd
            }
        }
    }

    fun getSubDownLineList(parentPhone: String): Flow<PagingData<DownLineItem>> =
    Pager(PagingConfig(1)) {
        SubDownLinePagingSource(
            repository = repository,
            dateStart = _dateStart.value,
            dateEnd = _dateEnd.value,
            parentNumber = parentPhone
        )
    }.flow.cachedIn(viewModelScope)
}