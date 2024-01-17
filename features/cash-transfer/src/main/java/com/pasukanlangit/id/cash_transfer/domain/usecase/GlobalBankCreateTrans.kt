package com.pasukanlangit.id.cash_transfer.domain.usecase

import com.pasukanlangit.id.cash_transfer.domain.TransferRepository
import com.pasukanlangit.id.cash_transfer.domain.model.GlobalBankCreateTransRequest
import com.pasukanlangit.id.cash_transfer.domain.model.TransferTransactionRequest
import com.pasukanlangit.id.cash_transfer.domain.utils.GlobalBankCreateTransTAGResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GlobalBankCreateTrans(
    private val repository: TransferRepository
) {
    operator fun invoke(
        quoteId: String, relationCode: String, nationCode: String, bankCode: String, bankAccNum: String,
        bankAccName: String, countryCode: String, address: String, city: String, reqId: String?
    ): Flow<DataState<GlobalBankCreateTransTAGResponse>> = flow {
        emit(DataState.loading())
        try {
            val uuid = repository.getUUID()
            val accessToken = repository.getAccessToken()
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty())
                throw Exception(Constants.invalidAuth)

            val name = bankAccName.findName()
            val createTransRequest = GlobalBankCreateTransRequest(
                uuid = uuid, quoteId = quoteId, relationCode = relationCode, bankCode = bankCode,
                bankAccNum = bankAccNum, countryCode = countryCode, firstName = name.first, lastName = name.second,
                addressStreet = address, addressCity = city, nationCode = nationCode
            )
            val createTrans = repository.globalCreateTransaction(createTransRequest, accessToken)
            val requestTag = TransferTransactionRequest(
                uuid = uuid, codeProduct = "TAGFBANK", dest = createTrans.quoteId, pin = "", reqId = reqId
            )
            val responseTAG = repository.transferTransaction(requestTag, accessToken)
            val response = GlobalBankCreateTransTAGResponse(quoteId = createTrans.quoteId, tagResponse = responseTAG)
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }

    private fun String.findName(): Pair<String, String> {
        val splitterName = this.split(" ")
        if (splitterName.size > 1) {
            var lastname = ""
            splitterName.forEachIndexed { index, value ->
                if (index == 0) return@forEachIndexed
                if (index != splitterName.lastIndex) {
                    lastname += "${value.trim()} "
                    return@forEachIndexed
                }
                lastname += value.trim()
            }
            return Pair(splitterName.first(), lastname)
        }
        return Pair(this, this)
    }
}