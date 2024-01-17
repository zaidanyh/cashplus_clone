package com.pasukanlangit.id.travelling.domain.usecases

import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.travelling.domain.TravellingRepository
import com.pasukanlangit.id.travelling.domain.model.TransactionRequest
import com.pasukanlangit.id.travelling.domain.model.TransactionResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BookingUseCase(
    private val repository: TravellingRepository
) {
    operator fun invoke(
        codeProduct: String,
        destination: String,
        pin: String
    ): Flow<DataState<TransactionResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) throw Exception(Constants.invalidAuth)

            val request = TransactionRequest(uuid, "TAG$codeProduct", destination, pin)
            repository.bookingTransaction(request, accessToken)
            val response = repository.bookingTransaction(request.copy(productCode = request.productCode.replaceRange(0,3, "PAY")), accessToken)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}