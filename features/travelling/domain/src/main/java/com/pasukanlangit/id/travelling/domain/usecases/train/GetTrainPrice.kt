package com.pasukanlangit.id.travelling.domain.usecases.train

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.BalanceRequest
import com.pasukanlangit.id.travelling.domain.model.BalanceResponse
import com.pasukanlangit.id.travelling.domain.model.train.TrainPriceRequest
import com.pasukanlangit.id.travelling.domain.model.train.TrainPriceResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTrainPrice(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        session: String?, from: String, to: String, date:String, adult: String, infant: String,
        trainCode: String?, trainClass: String?, trainSubClass: String?
    ): Flow<DataState<Pair<BalanceResponse, TrainPriceResponse>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = TrainPriceRequest(
                uuid, session.toString(), from, to, date, adult,
                infant, trainCode.toString(), trainClass.toString(), trainSubClass.toString()
            )
            val balance = repository.balanceCheck(BalanceRequest(uuid), accessToken)
            val trainPrice = repository.trainPrice(request, accessToken)

            emit(DataState.data(data = Pair(balance, trainPrice)))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}