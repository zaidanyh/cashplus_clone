package com.pasukanlangit.id.domain_downline.model

import com.pasukanlangit.id.library_core.utils.TrxStatus

enum class DbCrType {
    DB,
    CR,
    UNKNOWN
}
data class DownLineMutationResponse(
    val valueRupiah: String,
    val endingBalanceRupiah: String,
    val dbCrType: DbCrType,
    val desc: String,
    val date: String,
    val productCode: String,
    val trxStatus: TrxStatus
)
