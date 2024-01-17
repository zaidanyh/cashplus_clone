package com.pasukanlangit.id.usecase

import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.library_core.utils.Constants
import com.pasukanlangit.id.model.TrxGoldResponse
import com.pasukanlangit.id.model.TrxRequest
import com.pasukanlangit.id.library_core.domain.model.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

sealed class TrxProvider(val type: TrxType,val providerCode: String, val destination: String?){
    data class BuyGoldGram(val trxType: TrxType, val dest: String?): TrxProvider(trxType, "EMAS", dest)
    data class BuyGoldRupiah(val trxType: TrxType,val dest: String?): TrxProvider(trxType, "RUPIAH", dest)
    data class WithDrawGold(val trxType: TrxType, val dest: String?): TrxProvider(trxType, "CETAKEMAS", dest)
}

enum class TrxType {
    TAG,
    PAY
}

class SendTrx(
    private val repository: CashGoldRepository
) {
    operator fun invoke(trxProvider: TrxProvider, pin: String): Flow<DataState<TrxGoldResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()

            if(uuid == null || accessToken == null){
                throw Exception(Constants.invalidAuth)
            }

            if(trxProvider.destination == null){
                throw Exception(Constants.invalidBookId)
            }

            val productCode = when(trxProvider.type){
                TrxType.TAG -> "TAG${trxProvider.providerCode}"
                TrxType.PAY -> "PAY${trxProvider.providerCode}"
            }

            val request = TrxRequest(
                pin = pin,
                dest = trxProvider.destination,
                uuid = uuid,
                kodeProduk = productCode
            )

            val response = repository.sendTrxGold(request, accessToken)
            emit(DataState.data(data = response))
        }catch (e: Exception){
            emit(DataState.error(message = e.message ?: Constants.unknownError))
        }
    }
}