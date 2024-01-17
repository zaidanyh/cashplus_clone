package com.pasukanlangit.cashplus.domain.model

import com.pasukanlangit.id.core.model.ProfileResponse

data class BalanceAccountResponse(
    val profileResponse: ProfileResponse,
//    val phones: String,
//    val email: String,
//    val fullName: String,
//    val imgUrl: String,
//    val placeOfBorn: String,
//    val dateOfBirth: String,
    val itemBalance: List<ItemBalance>
)

data class ItemBalance(
    val label: String,
    val balance: Double?,
    val stateBalance: Boolean,
    val stateIsAvailable: Boolean = true
)
