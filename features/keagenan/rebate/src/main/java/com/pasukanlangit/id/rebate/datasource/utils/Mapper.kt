package com.pasukanlangit.id.rebate.datasource.utils

import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.rebate.datasource.network.dto.*
import com.pasukanlangit.id.rebate.domain.model.*

fun RebateRequest.toRebateRequestDto(): RebateRequestDto =
    RebateRequestDto(
        dateStart = dateStart,
        dateEnd = dateEnd,
        uuid = uuid
    )

fun RebateResponseDto.toRebateResponse(): RebateResponse =
    RebateResponse(
        totalRebateRaw = totalRebate,
        totalRebateRupiah = getNumberRupiah(totalRebate.toDoubleOrNull())
    )

fun RebateProductDetailResponseDto.toRebateProductDetailList(): List<RebatePerProductResponse> =
    this.data.map {
        RebatePerProductResponse(
            productCode = it.kodeProduk,
            totalRebateRupiah = getNumberRupiah(it.totalRebate.toIntOrNull())
        )
    }

fun RemainingRebateRequest.toRemainingRebateRequestDto(): RemainingRebateRequestDto =
    RemainingRebateRequestDto(uuid = this.uuid)

fun RemainingRebateResponseDto.toRemainingRebateResponse(): RemainingRebateResponse =
    RemainingRebateResponse(sum_rebate = this.sum_rebate ?: "0")