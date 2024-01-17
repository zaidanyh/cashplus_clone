package com.pasukanlangit.id.travelling.domain.usecases.hotel

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.hotel.HotelCityListRequest
import com.pasukanlangit.id.travelling.domain.model.hotel.HotelCityListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCityList(
    private val repository: TravellingRepository
) {
    operator fun invoke(cityName: String): Flow<DataState<HotelCityListResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) {
                throw Exception(Constants.invalidAuth)
            }

            val request = HotelCityListRequest(uuid, cityName)
            val response = repository.cityListHotel(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}