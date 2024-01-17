package com.pasukanlangit.id.travelling.domain.usecases.flight

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.BalanceRequest
import com.pasukanlangit.id.travelling.domain.model.BalanceResponse
import com.pasukanlangit.id.travelling.domain.model.flight.FlightPriceRequest
import com.pasukanlangit.id.travelling.domain.model.flight.FlightPriceResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetFlightPrice(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        from: String, to: String, date: String, flightCode: String,
        adult: String, child: String, infant: String
    ): Flow<DataState<Pair<BalanceResponse, FlightPriceResponse>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
                throw Exception(Constants.invalidAuth)
            }

            val balanceRequest = BalanceRequest(uuid)
            val balance = repository.balanceCheck(balanceRequest, accessToken)
            val flightPriceRequest = FlightPriceRequest(uuid, from, to, date, flightCode, adult, child, infant)
            val flightPrice = repository.flightPrice(flightPriceRequest, accessToken)

            emit(DataState.data(data = Pair(balance, flightPrice)))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}
