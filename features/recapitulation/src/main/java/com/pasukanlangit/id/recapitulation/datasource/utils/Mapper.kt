package com.pasukanlangit.id.recapitulation.datasource.utils

import com.pasukanlangit.id.recapitulation.datasource.network.dto.trans.*
import com.pasukanlangit.id.recapitulation.datasource.network.dto.RecapRequestDto
import com.pasukanlangit.id.recapitulation.datasource.network.dto.deposit.RecapDepositMutationResponseDto
import com.pasukanlangit.id.recapitulation.domain.model.MutationDepositResponse
import com.pasukanlangit.id.recapitulation.domain.model.ProfitByProductResponse
import com.pasukanlangit.id.recapitulation.domain.model.ProfitSummaryResponse
import com.pasukanlangit.id.recapitulation.domain.model.RecapRequest

fun RecapRequest.toRecapRequestDto(): RecapRequestDto =
    RecapRequestDto(
        uuid = this.uuid, dateStart = this.dateStart, dateEnd = this.dateEnd,
        rowStart = this.rowStart ?: "1", isSummary = this.isSummary ?: ""
    )

fun RecapProfitSummaryResponseDto.toProfitSummaryResponse(): ProfitSummaryResponse =
    ProfitSummaryResponse(
        qty = this.data?.first()?.qty ?: "", modal = this.data?.first()?.modal ?: "",
        selling = this.data?.first()?.selling ?: "", profit = this.data?.first()?.profit ?: ""
    )

fun RecapProfitByProductResponseDto.toProfitByProductResponse(): List<ProfitByProductResponse> =
    this.data?.map { item ->
        ProfitByProductResponse(
            productCode = item.productCode, qty = item.qty, modal = item.modal, selling = item.selling,
            profit = item.profit, desc = item.description, shortDesc = item.shortDesc, operator = item.operatorName,
            category = item.category, providerCode = item.providerCode
        )
    } ?: emptyList()

fun RecapDepositMutationResponseDto.toMutationDepositResponse(): List<MutationDepositResponse> =
    this.data?.map { item ->
        MutationDepositResponse(
            rowNum = item.rowNum, trxDate = item.dateTrans, value = item.value, isDB = item.isDBorCR == "DB",
            endingBalance = item.endingBalance, productCode = item.productCode, dest = item.destination,
            productDesc = item.productDesc, providerCode = item.providerCode, category = item.category
        )
    } ?: emptyList()
