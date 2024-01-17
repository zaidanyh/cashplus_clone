package com.pasukanlangit.id.ui_downline_home.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.DownLineItem
import com.pasukanlangit.id.domain_downline.model.GetSubDownLineRequest
import com.pasukanlangit.id.library_core.utils.Constants
import retrofit2.HttpException

class SubDownLinePagingSource(
    private val repository: DownLineRepository,
    private val dateStart: String,
    private val dateEnd: String,
    private val parentNumber: String
): PagingSource<Int, DownLineItem>() {
    override fun getRefreshKey(state: PagingState<Int, DownLineItem>): Int? = state.anchorPosition?.let {
        state.closestPageToPosition(it)?.prevKey?.plus(10)
            ?: state.closestPageToPosition(it)?.nextKey?.minus(10)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DownLineItem> = try {
        val pageNumber = params.key ?: 1

        val uuid = repository.getUUID()
        val accessToken = repository.getAccessToken()
        if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
            throw Exception(Constants.invalidAuth)

        val response = repository.getSubDownLineList(
            GetSubDownLineRequest(
                uuid = uuid, dateStart = dateStart, dateEnd = dateEnd, downLineNumber = parentNumber,
                downLineName = "", rowStart = pageNumber.toString()
            ), accessToken
        )
        val data = mutableListOf<DownLineItem>()
        response.downLineItems?.let { data.addAll(it) }

        val prevKey = if (pageNumber > 1) pageNumber.minus(10) else null
        val nextKey = if (data.isEmpty()) null else pageNumber.plus(10)

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