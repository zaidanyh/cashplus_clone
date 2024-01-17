package com.pasukanlangit.id.recapitulation.domain.usecase.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.recapitulation.domain.RecapRepository
import com.pasukanlangit.id.recapitulation.domain.model.MutationDepositResponse
import com.pasukanlangit.id.recapitulation.domain.model.RecapRequest
import retrofit2.HttpException

class GetMutationBalancePaging(
    private val repository: RecapRepository,
    private val dateStart: String,
    private val dateEnd: String
): PagingSource<Int, MutationDepositResponse>() {

    override fun getRefreshKey(state: PagingState<Int, MutationDepositResponse>): Int? = state.anchorPosition?.let {
        state.closestPageToPosition(it)?.prevKey?.plus(100)
            ?: state.closestPageToPosition(it)?.nextKey?.minus(100)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MutationDepositResponse> = try {
        val pageNumber = params.key ?: 1

        val uuid = repository.getUUID()
        val accessToken = repository.getAccessToken()
        if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
            throw Exception(Constants.invalidAuth)

        val request = RecapRequest(
            uuid = uuid, dateStart = dateStart, dateEnd = dateEnd,
            rowStart = pageNumber.toString(), isSummary = "1"
        )
        val response = repository.getMutationBalance(request = request, accessToken = accessToken)

        val prevKey = if (pageNumber > 1) pageNumber.minus(100) else null
        val nextKey = if (response.isEmpty()) null else pageNumber.plus(100)
        LoadResult.Page(
            data = response, prevKey = prevKey, nextKey = nextKey
        )
    } catch (e: Exception) {
        LoadResult.Error(e)
    } catch (http: HttpException) {
        LoadResult.Error(http)
    }
}