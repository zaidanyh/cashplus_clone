package com.pasukanlangit.id.nobu.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.nobu.domain.NobuRepository
import com.pasukanlangit.id.nobu.domain.model.HistoryTrxRequest
import com.pasukanlangit.id.nobu.domain.model.HistoryTrxResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*

class GetHistoryTrxNobu(
    private val repository: NobuRepository
) {
    operator fun invoke(
        dateStart: String? = null,
        dateEnd: String? = null
    ): Flow<DataState<HistoryTrxResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) throw Exception(Constants.invalidAuth)

            val getLastMonth = getLastMonth()
            val request = HistoryTrxRequest(
                uuid =  uuid,
                dateStart = if (dateStart.isNullOrEmpty()) getLastMonth.first() else dateStart
                , dateEnd = if (dateEnd.isNullOrEmpty()) getLastMonth().last() else dateEnd,
                pageNumber = "1", pageSize = "25"
            )
            val response = repository.historyTrxNobu(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }

    private fun getLastMonth() : List<String> {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

        val startOfLastMonth = getDayTimeBefore()
        val endOfLastMonth = Date()

        return listOf(
            sdf.format(startOfLastMonth),
            sdf.format(endOfLastMonth)
        )
    }

    private fun getDayTimeBefore(): Long {
        var currentDateTime = Date().time
        for(i in 0 until 182) {
            currentDateTime -= 86400000
        }
        return currentDateTime
    }
}