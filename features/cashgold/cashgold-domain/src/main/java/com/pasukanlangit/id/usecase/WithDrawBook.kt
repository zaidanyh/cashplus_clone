package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.WithDrawBookRequest
import com.pasukanlangit.id.model.WithDrawBookResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WithDrawBook(
    private val repository: CashGoldRepository
) {
    operator fun invoke(
        addressId: String?,
        providerId: String?,
        quantity: String?,
        denomination: String?
    ): Flow<DataState<WithDrawBookResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }

            if(addressId == null || providerId == null || quantity == null || denomination == null){
                throw(Exception(Constants.unknownError))
            }
            val request = WithDrawBookRequest(
                quantity = quantity,
                uuid = uuid,
                productId = providerId,
                addressId = addressId,
                denomination = denomination
            )
            val response = repository.withDrawBook(request, accessToken)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}