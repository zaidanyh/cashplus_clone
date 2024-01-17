package com.pasukanlangit.id.datasource_downline.mapper

import com.pasukanlangit.id.core.model.BalanceResponseCore
import com.pasukanlangit.id.datasource_downline.network.dto.*
import com.pasukanlangit.id.domain_downline.model.*
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.library_core.utils.TrxUtil

fun DownLineSummaryRequest.toDownLineSummaryRequestDto(): DownLineSummaryRequestDto =
    DownLineSummaryRequestDto(
        uuid = this.uuid, dateStart = this.dateStart, dateEnd = this.dateEnd
    )

fun DownLineRequest.toDownLineRequestDto(): DownLineRequestDto =
    DownLineRequestDto(
        uuid = this.uuid, dateStart = this.dateStart, dateEnd = this.dateEnd,
        downLinePhone = this.downLinePhone, downLineFullName = this.downLineFullName,
        rowStart = this.rowStart
    )

fun SummaryDownlineResponseDto.toSummaryDownlineResponse(): SummaryDownlineResponse =
    SummaryDownlineResponse(
        fullname = this.fullname,
        myBalance = this.myBalance,
        myTrxCount = this.myTrxCount,
        downlineCount = this.downlineCount,
        downlineTotalBalance = this.downlineTotalBalance
    )

fun DownlineListResponseDto.toDownLineResponse(): DownLineResponse =
    DownLineResponse(
        downLineItems = this.data?.toDownLineItems()
    )

fun SetAllProductMarkupRequest.toSetAllProductMarkupDto(): SetAllProductMarkupRequestDto =
    SetAllProductMarkupRequestDto(
        uuid = this.uuid, dest = this.dest, newMarkup = this.newMarkup
    )

fun DownlineTrxCountResponseDto.toDownLineTrxCountResponse(): DownlineTrxCount =
    DownlineTrxCount(
        trxCount = this.downlineTrxCount
    )

fun ResetMarkupRequest.toResetMarkupDto(): ResetMarkupRequestDto =
    ResetMarkupRequestDto(
        uuid = this.uuid,
        markup = this.markUpValue
    )

private fun List<DownlineDataItem>.toDownLineItems(): List<DownLineItem> =
    this.map {
        DownLineItem(
            userPhone = it.user,
            name = it.fullName,
            ownerName = it.ownerName,
            phoneActive = it.phones.getOrNull(0)?.phone ?: "",
            balanceRupiah = getNumberRupiah(it.balance.toIntOrNull()),
            isActive = it.isActive == "1",
            markupCode = it.codeMarkup,
            markup = it.markup,
            trxCount = it.trxCount,
            downLineCount = it.downlineCount,
            address = it.address,
            img_url = it.imgUrl,
            markupPerProduct = it.markupPerProduct.toItemProductMarkup()
        )
    }

fun List<MarkupPerProductItem>.toItemProductMarkup(): List<ItemProductMarkup> =
    this.map {
        ItemProductMarkup(
            productCode = it.kodeProduk,
            markup = it.markup
        )
    }

fun GetSubDownLineRequest.toSubDownLineRequestDto(): GetSubDownLineRequestDto =
    GetSubDownLineRequestDto(
        uuid = this.uuid, dateStart = this.dateStart, dateEnd = this.dateEnd,
        downlineFullName = this.downLineName, downlinePhone = this.downLineNumber,
        rowStart = this.rowStart
    )

fun List<ProvinceDataItem>.toListProvince(): List<LocationNameResponse> =
    this.map {
        LocationNameResponse(
            name = it.name,
            id = it.id
        )
    }


fun CityRequest.toCityRequestDto(): CityRequestDto =
    CityRequestDto(
        provinceId = this.provinceId,
        uuid = this.uuid
    )

fun DistrictRequest.toDistrictRequestDto(): DistrictRequestDto =
    DistrictRequestDto(
        regencyId = this.cityId,
        uuid = this.uuid
    )

fun VillageRequest.toVillageRequestDto(): VillageRequestDto =
    VillageRequestDto(
        districtId = this.districtId,
        uuid = this.uuid
    )

fun List<CityDataItem>.toListCity(): List<LocationNameResponse> =
    this.map {
        LocationNameResponse(
            name = it.name,
            id = it.id
        )
    }

fun List<DistrictDataItem>.toDistrictCity(): List<LocationNameResponse> =
    this.map {
        LocationNameResponse(
            name = it.name,
            id = it.id
        )
    }


fun List<VillageDataItem>.toVillages(): List<LocationNameResponse> =
    this.map {
        LocationNameResponse(
            name = it.name,
            id = it.id
        )
    }


fun RegisterDownLineRequest.toRegisterDownLineRequestDto(): RegisterDownLineRequestDto =
    RegisterDownLineRequestDto(
        uuid = this.uuid,
        phone = this.phone,
        fullName = this.fullName,
        ownerName = this.ownerName,
        email = this.email,
        password = this.password,
        address = this.address,
        prov = this.prov,
        city = this.city,
        district = this.district,
        village = this.village,
        zipcode = this.zipcode
    )

fun TopUpDownLine.toTopUpDownLineRequestDto(): DownlineTopUpRequestDto =
    DownlineTopUpRequestDto(
        pin = this.pin,
        dest = this.dest,
        uuid = this.uuid,
        value = this.value,
        paymentMethod = ""
    )

fun MessageResponseDto.toMessageResponse(): MessageResponse =
    MessageResponse(
        message = this.msg
    )

fun DownLineSumDetRequest.toDownLineMutationRequestDto(): DownLineMutationRequestDto =
    DownLineMutationRequestDto(
        dateStart = this.dateStart,
        dateEnd = this.dateEnd,
        uuid = this.uuid,
        downlinePhone = this.downlinePhone,
        isSummary = "1",
        rowStart = "1"
    )
fun DownLineSumDetRequest.toDetailTransferRequestDto(): DownlineTransferRequestDto =
    DownlineTransferRequestDto(
        dateStart = this.dateStart,
        dateEnd = this.dateEnd,
        uuid = this.uuid,
        downlinePhone = this.downlinePhone,
    )

fun DownLineMutationResponseDto.toDownLineMutationResponseDto(): List<DownLineMutationResponse> =
    this.data.map {
        DownLineMutationResponse(
            valueRupiah = getNumberRupiah(it.value.toIntOrNull() ?: 0),
            endingBalanceRupiah = getNumberRupiah(it.value.toIntOrNull() ?: 0),
            dbCrType = when {
                it.dbCr.equals("db", ignoreCase = true) -> {
                    DbCrType.DB
                }
                it.dbCr.equals("cr", ignoreCase = true) -> {
                    DbCrType.CR
                }
                else -> {
                    DbCrType.UNKNOWN
                }
            },
            desc = it.transStatDsc,
            date = it.trxDate,
            productCode = it.kodeProduk,
            trxStatus = TrxUtil.getTrxStatusByCode(it.transStat)
        )
    }

fun DetailTransferResponseDto.toDetailTransferDownline(): List<DetailTransferDownlineResponse> =
    this.data.map {
        DetailTransferDownlineResponse(
            date = it.trxDate,
            paymentMethod = it.paymentMethod,
            valueRupiah = getNumberRupiah(it.value.toIntOrNull()),
            endingBalanceRupiah = getNumberRupiah(it.value.toIntOrNull()),
            destinationNumber = it.dest,
            downlineName = it.downlineFullName,
            transtStat = it.transStat
        )
    }

fun DownlineSummaryTransferResponseDto.toSummaryTransferDownline(): List<SummaryTransferResponse> =
    this.data.map {
        SummaryTransferResponse(
            totalTransferRupiah = getNumberRupiah(it.totalTransfer.toIntOrNull()),
            username = it.userName,
            downlineName = it.downlineFullName
        )
    }

fun NearbyAgenResponseDto.toNearByAgenResponse(): NearbyAgentResponse =
    NearbyAgentResponse(
        myLat = this.myLat.toDoubleOrNull() ?: 0.0,
        myLong = this.myLong.toDoubleOrNull() ?: 0.0,
        agents = this.data.map {
            AgentNearBy(
                name = it.fullName,
                address = it.address,
                distance = it.distance,
                lat = it.lat.toDoubleOrNull() ?: 0.0,
                long = it.long.toDoubleOrNull() ?: 0.0
            )
        }
    )

fun GenerateQRRequest.toGenerateQRRequestDto(): GenerateQrRequestDto =
    GenerateQrRequestDto(
        amount = this.amount,
        uuid = this.uuid
    )

fun GenerateQrResponseDto.toGenerateQRResponse(): GenerateQRResponse =
    GenerateQRResponse(
        imgCodeUrl = this.imgUrl
    )

fun ScanQRAgenRequest.toScanQRAgenRequestDto(): ScanQRAgenRequestDto =
    ScanQRAgenRequestDto(
        id = this.id,
        pin = this.pin,
        uuid = this.uuid
    )

fun ProductRequest.toProductRequestDto(): ProductRequestDto =
    ProductRequestDto(
        uuid = this.uuid,
        keyword = keyword ?: ""
    )

fun ProductsResponseDto.toProductResponse(): List<ProductResponse> =
    this.data?.map {
        ProductResponse(
            kodeProduct = it.kodeProduk,
            kodeProvider = it.kodeProvider,
            category = it.category,
            price = it.price,
            outlet_price = it.outletPrice,
            isActive = it.isActive
        )
    }!!

fun PricePlanRequest.toPricePlanRequestDto(): PricePlanRequestDto =
    PricePlanRequestDto(uuid = this.uuid, filter = this.keyword)

fun MarkupPlansResponseDto.toMarkupPlansResponse(): MarkupPlansResponse =
    MarkupPlansResponse(
        markupPlans = this.markupPlans?.map {
            MarkupPlan(
                codeMarkupPlan = it.markupPlanCode, description = it.description
            )
        }
    )

fun DetailMarkupPlanRequest.toDetailMarkupPlanRequestDto(): DetailMarkupPlanRequestDto =
    DetailMarkupPlanRequestDto(
        uuid = this.uuid, markupPlanCode = this.codeMarkupPlan, downlinePhone = this.downLinePhone
    )

fun DetailMarkupPlanResponseDto.toDetailMarkupPlanResponse(): DetailMarkupPlansResponse =
    DetailMarkupPlansResponse(
        detailMarkupPlan = this.data?.map {
            DetailMarkupPlan(
                codeMarkupPlan = it.markupPlanCode, codeProduct = it.productCode, markup = it.markup
            )
        }
    )

fun CreateReplaceMarkupPlanRequest.toCreateReplaceMarkupPlanRequestDto(): CreateReplaceMarkupPlanRequestDto =
    CreateReplaceMarkupPlanRequestDto(
        uuid = this.uuid, markupCode = this.markupCode, description = this.description,
        products = this.data.map {
            ProductMarkupPlan(productCode = it.productCode, markup = it.markup)
        }
    )

fun UpdateLatLongRequest.toUpdateLatLongCurrentLocationRequestDto(): UpdateLatLongCurrentLocationRequestDto =
    UpdateLatLongCurrentLocationRequestDto(
        uuid = this.uuid, lat = this.lat, long = long
    )

fun BalanceResponseCore.toCheckBalanceResponse(): CheckBalanceResponse =
    CheckBalanceResponse(balance = this.balance)