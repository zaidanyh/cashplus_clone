package com.pasukanlangit.id.cash_transfer.domain.usecase

import com.pasukanlangit.id.cash_transfer.domain.TransferRepository
import com.pasukanlangit.id.cash_transfer.domain.model.FetchCountryRequest
import com.pasukanlangit.id.cash_transfer.domain.model.FetchCountryResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCountryList(
    private val repository: TransferRepository
) {
    operator fun invoke(
        countryName: String = "",
        currencyDsc: String = ""
    ): Flow<DataState<List<FetchCountryResponse>>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val request = FetchCountryRequest(uuid = uuid, country_name = countryName, currency_dsc = currencyDsc)
            val response = repository.fetchCountryList(request, accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}
