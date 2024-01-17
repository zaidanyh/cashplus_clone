package com.pasukanlangit.id.network

import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.model.*
import com.pasukanlangit.id.model.UserCashGold
import com.pasukanlangit.id.network.dto.*
import kotlin.math.floor

fun GoldData.toUserBalance() =
    IndoGoldBalance(gold = "${if(this.gold == floor(this.gold)) this.gold.toInt() else this.gold} gr", goldRaw = this.gold)

fun PriceDto.toGoldPrice() =
    GoldPrice(
        priceBuyCurrency = getNumberRupiah(this.buy.toIntOrNull() ?: 0) + "/gr",
        priceBuyInt = this.buy.toIntOrNull() ?: 0
    )

fun GetChartRequest.toGetChartDto(): ChartRequestDto =
    ChartRequestDto(
        uuid = this.uuid,
        numberOfDays = this.numberOfDays
    )

fun ProfileResponseDto.toSt24Balance(): St24Profile =
    St24Profile(
        balanceCurrency = getNumberRupiah(this.balance),
        fullName = this.fullName,
        email = this.email,
        phone = this.phones[0].phone,
        balanceRaw = this.balance
    )

fun RegisterRequest.toRegisterDto(): RegisterRequestDto =
    RegisterRequestDto(
        uuid = this.uuid,
        name = this.name,
        email = this.email,
        phone = this.phone
    )

fun ChartResponseDto.toListChartDomain(): List<ChartResponse> =
    this.data?.data?.map {
        ChartResponse(
            date = it.getOrNull(0) ?: "",
            lowPrice = it.getOrNull(1) ?: "",
            openPrice = it.getOrNull(2) ?: "",
            closePrice = it.getOrNull(3) ?: "",
            highPrice = it.getOrNull(4) ?: "",
        )
    } ?: listOf()


fun LockGoldRequest.toLockGoldByGramRequestDto(): LockByGramRequestDto =
    LockByGramRequestDto(
        gramNominal = this.nominal,
        uuid = this.uuid,
        price = this.priceGold.toString()
    )

fun LockGoldRequest.toLockGoldByRupiahRequestDto(): LockByRupiahRequestDto =
    LockByRupiahRequestDto(
        rupiahNominal = this.nominal,
        uuid = this.uuid,
        price = this.priceGold.toString()
    )

fun LockGoldResponseDto.toLockGoldResponse(): LockGoldResponse =
    LockGoldResponse(
        lockId = this.data?.lockedId
    )


fun TrxRequest.toTrxRequestDto(): TrxRequestDto =
    TrxRequestDto(
        pin = this.pin,
        dest = this.dest,
        kodeProduk = this.kodeProduk,
        uuid = this.uuid
    )

fun TrxResponseDto.toTrxGoldResponse(): TrxGoldResponse =
    TrxGoldResponse(
        priceTotalCurrency = getNumberRupiah(this.price.toIntOrNull()),
        fee = this.fee,
        priceRupiah = getNumberRupiah(this.billData?.tagihan?.toIntOrNull()) ,
        feeCurrency = getNumberRupiah(this.fee.replaceFirst("-","").toIntOrNull()),
        admin = getNumberRupiah(this.billData?.admin?.toIntOrNull()),
        gramBuy = this.billData?.periode?.plus(" gr") ?: "",
        qty = this.quantity ?: "",
        name = this.billData?.nama ?: "",
        withdrawData = WithDrawData(
            certificateFee = getNumberRupiah(this.billData?.certificateFee?.toIntOrNull()),
            assuranceFee = getNumberRupiah(this.billData?.assuranceFee?.toIntOrNull()),
            sendFee = getNumberRupiah(this.billData?.sendFee?.toIntOrNull()),
        ),
        trxGoldData = TrxGoldData(
            discountFee = getNumberRupiah(this.billData?.discountFee?.toIntOrNull()),
            pph22FeeRupiah = getNumberRupiah(this.billData?.pph22?.toIntOrNull()),
            ppn11FeeRupiah = getNumberRupiah(this.billData?.ppn11Persen?.toIntOrNull()),
        )
    )

fun WithDrawProductResponseDto.toWithDrawProduct(): WithDrawProductResponse =
   WithDrawProductResponse(
       provider = this.dataWithDraw?.product?.map {
            it.toWithDrawProvider()
       } ?: listOf()
   )

fun WithDrawProductItem.toWithDrawProvider(): com.pasukanlangit.id.model.WithDrawProvider =
    com.pasukanlangit.id.model.WithDrawProvider(
        id = this.providers.id,
        img = this.providers.logo,
        title = this.providers.name,
        product = this.products?.map { it.toWithDrawProductDomain() } ?: listOf()
    )


fun WithDrawDetailsItem.toWithDrawProductDomain(): WithDrawProduct =
    WithDrawProduct(
        denominationGram = this.denomination.gram,
        denominationRaw = this.denomination.raw,
        feeIdr = this.certificateFee.idr,
        feeRaw = this.certificateFee.raw,
        amount = this.stockAmount,
        withDrawDaily = this.totalWithdrawDaily,
        withDrawLimit = this.dailyWithdrawLimit
    )

fun List<AddressItem>.toAddressList() =
    this.map { it.toAddress() }

fun AddressItem.toAddress(): Address =
    Address(
        provinsi = provinsi ,
        keterangan = keterangan,
        kecamatan = kecamatan,
        kodepos = kodepos,
        id = id,
        isDefault = isDefault,
        kabupaten = kabupaten,
        kelurahan = kelurahan,
        alamat = alamat
    )

fun LocationRequest.toRequestCity() =
    GetCityDtoRequest(
        uuid = uuid,
        province = name
    )


fun LocationRequest.toRequestDistrict() =
    GetDistrictDtoRequest(
        uuid = uuid,
        city = name
    )

fun LocationVillageRequest.toRequestVillage() =
   GetVillageDtoRequest(
       uuid = uuid,
       district = district,
       city = city
   )

fun List<ProvinceDataItem>.toListProvince() =
    this.map {
        LocationList(name = it.provinsi)
    }

fun List<CityDataItem>.toListCity() =
    this.map {
        LocationList(name = it.city)
    }

fun List<DistrictDataItem>.toListDistrict() =
    this.map {
        LocationList(name = it.district)
    }

fun List<VillageDataItem>.toListVillage() =
    this.map {
        VillageList(
            name = it.village,
            postalCode = it.code
        )
    }

fun AddAddress.toAddAddressDto() =
    AddAddressRequestDto(
        zipCode = zipCode,
        address = address,
        province = province,
        city = city,
        district = district,
        village = village,
        uuid = uuid
    )

fun DeleteAddress.toDeleteAddressRequestDto() =
    DeleteAddressRequestDto(
        uuid = uuid,
        addressId = id.toString()
    )

fun UpdateAddress.toUpdateAddressDto() =
    UpdateAddressRequestDto(
        zipCode = zipCode,
        address = address,
        province = province,
        city = city,
        district = district,
        village = village,
        uuid = uuid,
        isDefault = if(isMain) "1" else "0",
        addressId = id.toString()
    )

fun UpdateUserCashGold.toUpdateUserDto() =
    UpdateUserCashGoldRequestDto(
        profession = profession,
        zipCode = zipCode,
        taxIdentityNumber = taxIdentityNumber,
        incomePerYear = incomePerYear,
        address = address,
        gender = gender,
        lastEducation = lastEducation,
        city = city,
        uuid = uuid,
        religion = religion,
        birthPlace = birthPlace,
        province = province,
        phone = phone,
        identityNumber = identityNumber,
        dayOfBirth = dayOfBirth,
        district = district,
        incomeSource = incomeSource,
        village = village,
        user = user,
        email = email,
        maritalStatus = maritalStatus
    )

fun UserCashGoldCheckResponseDto.isKtpNotEmpty(): Boolean =
    this.data.userCashGold?.ktpStatus.equals("validated", ignoreCase = true)

fun WithDrawBookRequest.toWithDrawBookRequest() =
    WithDrawBookRequestDto(
        quantity = quantity,
        productId = productId,
        uuid = uuid,
        addressId = addressId,
        denomination = denomination
    )

fun WithDrawBookResponseDto.toWithDrawBookResponse() =
    WithDrawBookResponse(
        withdrawId = this.data?.withdrawId
    )

fun UserCashGoldCheckResponseDto.toUserCashGold() =
    UserCashGold(
        user = this.data.userCashGold?.name ?: "",
        identityNumber = this.data.userCashGold?.ktp ?: "",
        gender = this.data.userCashGold?.gender,
        maritalStatus = this.data.userCashGold?.maritalStatus,
        birthPlace = this.data.userCashGold?.placeOfBirth,
        dayOfBirth = this.data.userCashGold?.dateOfBirth,
        email = this.data.userCashGold?.email ?: "",
        phone = this.data.userCashGold?.phone ?: "",
        religion = this.data.userCashGold?.religion,
        taxIdentityNumber = this.data.userCashGold?.npwp,
        lastEducation = this.data.userCashGold?.lastEducation,
        profession = this.data.userCashGold?.job,
        incomeSource = this.data.userCashGold?.sourceOfIncome,
        incomePerYear = this.data.userCashGold?.incomePerYear,
        province = this.data.userCashGold?.ktpAddress?.provinsi,
        city = this.data.userCashGold?.ktpAddress?.kabupaten,
        district = this.data.userCashGold?.ktpAddress?.kecamatan,
        village = this.data.userCashGold?.ktpAddress?.kelurahan,
        zipCode = this.data.userCashGold?.ktpAddress?.kodepos,
        address = this.data.userCashGold?.ktpAddress?.alamat
    )