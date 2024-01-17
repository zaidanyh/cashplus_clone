package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.ChartResponse
import com.pasukanlangit.id.model.GetChartRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

enum class ChartDuration(val numberOfDays: String){
    A_MONTH("30"),
    THREE_MONTH("90"),
    A_YEAR("360")
}

class GetChartUseCase(
    private val repository: CashGoldRepository
) {
    operator fun invoke(duration: ChartDuration): Flow<DataState<List<ChartResponse>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }
            val response = repository.getChart(GetChartRequest(uuid = uuid, numberOfDays = duration.numberOfDays),
                accessToken
            )
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}