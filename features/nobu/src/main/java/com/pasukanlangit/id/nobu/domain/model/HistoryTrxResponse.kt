package com.pasukanlangit.id.nobu.domain.model

data class HistoryTrxResponse(
    val listData: List<DataTrx>,
    val totalPage: String
)

data class DataTrx(
    val dateTimeTrx: String,
    val currency: String,
    val amount: String,
    val remark: String,
    val status: String,
    val type: String,
    val feeAmount: String,
    val referenceNum: String,
    val receiptNum: String,
)