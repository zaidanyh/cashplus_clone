package com.pasukanlangit.cashplus.domain.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pasukanlangit.cashplus.model.request_body.TrxHistoryRequest
import com.pasukanlangit.cashplus.model.response.HistoryTopUpDataItem
import com.pasukanlangit.cashplus.repository.MainRepository
import retrofit2.HttpException

class HistoryTopUpPagingSource(
    private val repository: MainRepository,
    private val uuid: String,
    private val accessToken: String,
    private val dateStart: String,
    private val dateEnd: String
): PagingSource<Int, HistoryTopUpDataItem>() {
    override fun getRefreshKey(state: PagingState<Int, HistoryTopUpDataItem>): Int? = state.anchorPosition?.let {
        state.closestPageToPosition(it)?.prevKey?.plus(100)
            ?: state.closestPageToPosition(it)?.nextKey?.minus(100)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryTopUpDataItem> = try {
        val pageNumber = params.key ?: 1
        val response = repository.getTransactionHistoryTopUp(
            TrxHistoryRequest(
                uuid = uuid, dateStart = dateStart,
                dateEnd = dateEnd, rowStart = pageNumber.toString()
            ), accessToken = accessToken
        )
        val data = mutableListOf<HistoryTopUpDataItem>()
        if (response.isSuccessful) {
            response.body()?.data?.let {
                data.addAll(it)
            }
        }

        val prevKey = if (pageNumber > 1) pageNumber.minus(100) else null
        val nextKey = if (data.isEmpty()) null else pageNumber.plus(100)

        LoadResult.Page(
            data = data.toList(),
            prevKey = prevKey,
            nextKey = nextKey
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    } catch (http: HttpException) {
        LoadResult.Error(http)
    }
}