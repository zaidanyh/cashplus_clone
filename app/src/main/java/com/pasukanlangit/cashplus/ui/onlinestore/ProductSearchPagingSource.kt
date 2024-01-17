package com.pasukanlangit.cashplus.ui.onlinestore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pasukanlangit.cashplus.model.request_body.ProductSearchRequest
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.repository.MainRepository

class ProductSearchPagingSource(private val mainRepository: MainRepository, private val uuid: String, private val token: String,private val keyword: String?): PagingSource<Int, ProductStoreDataItem>() {
    override fun getRefreshKey(state: PagingState<Int, ProductStoreDataItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductStoreDataItem> {
        return try{
            val rowStart = params.key ?: 1
            val response = mainRepository.searchProductStore(token,
                ProductSearchRequest(
                    rowStart = rowStart.toString(),
                    rowEnd = rowStart.plus(9).toString(),
                    keyword = keyword ?: "",
                    uuid = uuid
                )
            )
            val data = response.body()?.data ?: emptyList()
            val responseLiveData = mutableListOf<ProductStoreDataItem>()
            responseLiveData.addAll(data)

            LoadResult.Page(
                data = responseLiveData,
                prevKey = if(rowStart == 1) null else -1,
                nextKey = if(data.isEmpty()) null else rowStart.plus(10)
            )
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}