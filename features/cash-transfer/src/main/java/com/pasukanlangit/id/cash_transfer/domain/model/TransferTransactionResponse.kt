package com.pasukanlangit.id.cash_transfer.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class TransferTransactionResponse(
    val fee: String,
    val price: String,
    val transferBillData: TransferBillData
)

@Parcelize
data class TransferBillData(
    val bank_acc_num: String?,
    val name: String?,
    val bankName: String?,
    val bill: String?,
    val adminFee: String?,
    val total: String?
): Parcelable
