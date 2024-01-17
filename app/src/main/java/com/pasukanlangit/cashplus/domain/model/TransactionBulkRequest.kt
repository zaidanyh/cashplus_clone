package com.pasukanlangit.cashplus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class TransactionBulkRequest(
    val uuid: String,
    val codeProduct: String,
    val pin: String,
    val data: List<DataInject>
)

@Parcelize
data class DataInject(
    val dest: String,
    val reqId: String
): Parcelable
