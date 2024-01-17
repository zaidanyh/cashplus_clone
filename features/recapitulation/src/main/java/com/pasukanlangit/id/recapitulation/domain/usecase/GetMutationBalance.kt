package com.pasukanlangit.id.recapitulation.domain.usecase

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.recapitulation.domain.RecapRepository
import com.pasukanlangit.id.recapitulation.domain.model.RecapRequest
import com.pasukanlangit.id.recapitulation.domain.utils.RecapDepositResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMutationBalance(
    private val repository: RecapRepository
) {
    operator fun invoke(dateStart: String, dateEnd: String): Flow<DataState<RecapDepositResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = RecapRequest(uuid = uuid, dateStart = dateStart, dateEnd = dateEnd, isSummary = "1")
            val mutation = repository.getMutationBalance(request, accessToken)

            var debit = 0
            var credit = 0
            if (mutation.isNotEmpty()) {
                mutation.forEachIndexed { _, response ->
                    if (response.isDB) {
                        debit += response.value.toInt()
                        return@forEachIndexed
                    }
                    credit += response.value.toInt()
                }
            }
            val response = RecapDepositResponse(
                debit = debit.toString(), credit = credit.toString()
            )
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.networkError))
        }
    }
}