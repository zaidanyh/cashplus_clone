package com.pasukanlangit.id.travelling.domain.usecases.hotel

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.hotel.FindHotelRequest
import com.pasukanlangit.id.travelling.domain.model.hotel.FindHotelResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FindHotel(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        cityCode: String, hotelCode: String, checkin: String,
        checkout: String, room: String
    ): Flow<DataState<FindHotelResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
                throw Exception(Constants.invalidAuth)
            }

            val request = FindHotelRequest(uuid, cityCode, hotelCode, checkin, checkout, room)
            val response = repository.findHotel(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}