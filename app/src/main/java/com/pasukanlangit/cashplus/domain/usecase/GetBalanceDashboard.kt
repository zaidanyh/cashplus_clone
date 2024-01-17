package com.pasukanlangit.cashplus.domain.usecase

import com.pasukanlangit.cashplus.domain.model.BalanceAccountResponse
import com.pasukanlangit.cashplus.domain.model.ItemBalance
import com.pasukanlangit.cashplus.model.request_body.ProfileRequest
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.library_core.domain.model.DataState
import com.pasukanlangit.id.library_core.utils.Constants
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetBalanceDashboard(
    private val repository: MainRepository
) {
    operator fun invoke(
        uuid: String?,
        accessToken: String?
    ): Flow<DataState<BalanceAccountResponse>> = flow {
        emit(DataState.loading())
        try {
            if (uuid.isNullOrEmpty() || accessToken.isNullOrEmpty()) throw Exception(Constants.invalidAuth)

            val request = ProfileRequest(uuid)
            val responseBalance = mutableListOf<Deferred<ItemBalance>>()
            var profileResult: ProfileResponse? = null

            val profile = repository.getProfile(request, accessToken)
            val isBinded = repository.getIsBinded(request, accessToken)
            coroutineScope {
                if (profile.isSuccessful) {
                    profile.body()?.let { data ->
                        profileResult = data

                        val result = async {
                            ItemBalance(
                                "Deposit Cashplus", data.balance, true
                            )
                        }
                        responseBalance.add(result)
                    }
                }
                if (isBinded.isSuccessful) {
                    isBinded.body()?.let { data ->
                        if (data.isBinded == "1") {
                            val balanceInquiry = repository.getBalanceInquiry(request, accessToken)
                            if (balanceInquiry.isSuccessful) {
                                balanceInquiry.body()?.let {
                                    val result = async {
                                        ItemBalance(
                                            "Deposit QRIS",
                                            it.accountInfos.first().availableBalance.value.split(".").first().toDoubleOrNull(),
                                            true
                                        )
                                    }
                                    responseBalance.add(result)
                                }
                            } else {
                                val result = async {
                                    ItemBalance(
                                        "Deposit QRIS", null,
                                        stateBalance = false, stateIsAvailable = false
                                    )
                                }
                                responseBalance.add(result)
                            }
                        } else {
                            val result = async {
                                ItemBalance(
                                    "Deposit QRIS", null,
                                    stateBalance = false, stateIsAvailable = false
                                )
                            }
                            responseBalance.add(result)
                        }
                    }
                } else {
                    val result = async {
                        ItemBalance(
                            "Deposit QRIS", null, false
                        )
                    }
                    responseBalance.add(result)
                }
            }
            val response = profileResult?.let {
                BalanceAccountResponse(
                    profileResponse = it,
                    itemBalance = responseBalance.awaitAll()
                )
            }
            emit(DataState.data(data = response))
        } catch (e: Exception) {
            emit(DataState.error(e.message ?: Constants.unknownError))
        }
    }
}