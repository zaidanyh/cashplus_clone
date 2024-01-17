package com.pasukanlangit.id.ui_downline_transfersaldo.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.model.DownLineItem
import com.pasukanlangit.id.domain_downline.model.DownLineRequest
import com.pasukanlangit.id.domain_downline.model.DownLineResponse
import com.pasukanlangit.id.domain_downline.utils.PagingDataType
import com.pasukanlangit.id.library_core.utils.Constants

class DownlinePagingSource(
    private val repository: DownLineRepository,
    private val dateStart: String,
    private val dateEnd: String,
    private val type: String,
    private val value: String
): PagingSource<Int, DownLineItem>() {
    override fun getRefreshKey(state: PagingState<Int, DownLineItem>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DownLineItem> {
        return try {
            val rowStart = params.key ?: 1

            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            lateinit var response: DownLineResponse
            when (type) {
                PagingDataType.SEARCH_BY_PHONE.value -> {
                    response = repository.getDownLineList(
                        DownLineRequest(
                            uuid = uuid, dateStart = dateStart, dateEnd = dateEnd,
                            downLineFullName = "", downLinePhone = value, rowStart = rowStart.toString()
                        ), accessToken
                    )
                }
                PagingDataType.SEARCH_BY_NAME.value -> {
                    response = repository.getDownLineList(
                        DownLineRequest(
                            uuid = uuid, dateStart = dateStart, dateEnd = dateEnd,
                            downLineFullName = value, downLinePhone = "", rowStart = rowStart.toString()
                        ), accessToken
                    )
                }
                else -> {
                    response = repository.getDownLineList(
                        DownLineRequest(
                            uuid = uuid, dateStart = dateStart, dateEnd = dateEnd,
                            downLineFullName = "", downLinePhone = "", rowStart = rowStart.toString()
                        ), accessToken
                    )
                }
            }
            val data = mutableListOf<DownLineItem>()
            response.downLineItems?.let { data.addAll(it) }

            LoadResult.Page(
                data = data.toList(),
                prevKey = if (rowStart == 1) null else -1,
                nextKey = if (data.isEmpty()) null else rowStart.plus(10)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}