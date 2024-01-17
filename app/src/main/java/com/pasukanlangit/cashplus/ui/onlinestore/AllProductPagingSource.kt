package com.pasukanlangit.cashplus.ui.onlinestore

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pasukanlangit.cashplus.model.request_body.ProductStoreRequest
import com.pasukanlangit.cashplus.model.request_body.TokoOnlineListRequest
import com.pasukanlangit.cashplus.model.response.ProductStoreDataItem
import com.pasukanlangit.cashplus.model.response.ProductStoreResponse
import com.pasukanlangit.cashplus.repository.MainRepository
import retrofit2.Response

class AllProductPagingSource(private val mainRepository: MainRepository,private val uuid: String,private val token: String, private val categoryName: String?): PagingSource<Int, ProductStoreDataItem>() {
    override fun getRefreshKey(state: PagingState<Int, ProductStoreDataItem>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductStoreDataItem> {
        return try{
            val rowStart = params.key ?: 1

            lateinit var response: Response<ProductStoreResponse>
            if(!categoryName.isNullOrEmpty()) {
                response = mainRepository.getProductStoreList(
                    TokoOnlineListRequest(
                        categoryName = categoryName,
                        uuid = uuid,
                        rowStart = rowStart.toString(),
                        rowEnd = rowStart.plus(9).toString()
                    ),
                    token
                )
            }else{
                response = mainRepository.getProductStore(token,
                    ProductStoreRequest(
                        uuid,
                        rowStart.toString(),
                        rowStart.plus(9).toString()
                    )
                )
            }
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