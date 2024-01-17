package com.pasukanlangit.cashplus.data

import com.pasukanlangit.cashplus.domain.model.*
import com.pasukanlangit.cashplus.model.request_body.*
import com.pasukanlangit.cashplus.model.response.*

fun RegisterRequest.toRegisterRequestDto(): RegisterRequestDto =
    RegisterRequestDto(
        uuid = this.uuid, phone = this.phoneNumber, full_name = this.fullName, email = this.email,
        referral = this.referral, owner_name = this.ownerName, via = this.via
    )

fun LoginRequest.toLoginByOtpDto(): LoginByOtpRequestDto =
    LoginByOtpRequestDto(uuid = this.uuid, phone = this.phone, via = this.via)

fun OTPResponseDto.toLoginOtpResponse(): OtpResponse =
    OtpResponse(message = this.msg, otpLink = this.otpLink)

fun LoginRequest.toLoginRequestDto(): LoginRequestDto =
    LoginRequestDto(uuid = this.uuid, phone = this.phone)

fun OmniRequest.toOmniMenuRequestDto(): OmniRequests =
    OmniRequests(uuid = this.uuid, dest = this.dest)

fun OmniRequest.toOmniRequestPackageListDto(): OmniRequests =
    OmniRequests(uuid = this.uuid, dest = this.dest, mlid = this.mlid)

fun OmniRequest.toOmniRequestPackageOrderDto(): OmniRequests =
    OmniRequests(uuid = this.uuid, dest = this.dest, packageId = this.packageId)

fun OmniMenuResponseDto.toListOmniMenuResponse(): OmniMenuResponse =
    OmniMenuResponse(
        menuTitle = this.menu?.map { menu ->
            MenuTitle(icon = menu.icon, mlid = menu.mlid, title = menu.title.toOmniMenuLanguage())
        }, error = this.error
    )

private fun OmniMenuTitle.toOmniMenuLanguage(): MenuLanguage = MenuLanguage(en = this.titleEN, id = this.titleID)

fun OmniPackageListResponseDto.toOmniPackageListResponse(): OmniPackageListResponse =
    OmniPackageListResponse(
        error = this.error,
        subcategory = this.subcategory,
        packages = this.packages?.map { pack ->
            ResponsePackage(
                autoRenewal = pack.autoRenewal,
                bonuses = pack.bonuses?.map { bonus ->
                    BonusPackage(
                        classData = bonus.classData,
                        consumptionTime = bonus.consumptionTime,
                        highlight = bonus.highlight,
                        longDesc = bonus.longDesc,
                        name = bonus.name,
                        quota = bonus.quota,
                        shortDesc = bonus.shortDesc,
                        subClass = bonus.subClass,
                        validity = bonus.validity,
                        viral = bonus.viral == "true"
                    )
                },
                businesspId = pack.businesspId,
                id = pack.id,
                longDesc = pack.longDesc,
                name = pack.name,
                originalPrice = pack.originalPrice,
                price = pack.price,
                shortDesc = pack.shortDesc,
                subcategory = pack.subcategory,
                tag = pack.tag,
                terms = pack.terms,
                validity = pack.validity
            )
        }
    )

fun TransactionTAGResponse.toOmniPackageOrderResponse(paymentCode: String): OmniPackageOrderResponse =
    OmniPackageOrderResponse(
        paymentCode = paymentCode,
        transaction = this
    )

fun TransactionBulkRequest.toTransactionBulkRequestDto(): TransactionBulkRequestDto =
    TransactionBulkRequestDto(
        uuid = this.uuid, productCode = this.codeProduct, pin = this.pin,
        dataBulk = this.data.map {
            DataBulk(destination = it.dest, reqId = it.reqId)
        }
    )

fun UpdateEmailRequest.toUpdateEmailRequestDto(): UpdateEmailRequestDto =
    UpdateEmailRequestDto(
        uuid = this.uuid, email = this.email
    )

fun ResetPINRequest.toResetPINRequestDto(): ResetPINRequestDto =
    ResetPINRequestDto(
        uuid = this.uuid, pin = this.newPin, via = this.via
    )

fun ResetPasswordRequest.toResetPasswordRequestDto(): ResetPassRequestDto =
    ResetPassRequestDto(
        uuid = this.uuid, phone = this.phoneNumber, newPass = this.newPassword, via = this.via
    )

fun UpdateSellingPriceRequest.toUpdateSellingPriceRequestDto(): UpdateSellingPriceRequestDto =
    UpdateSellingPriceRequestDto(
        uuid = this.uuid, productCode = this.productCode, price = this.newPrice
    )

fun CalculateUnitPriceResponseDto.toCalculateUnitPriceResponse(): CalculateUnitPriceResponse =
    CalculateUnitPriceResponse(
        productCode = this.productCode, qty = this.qty, price = this.price,
        admin = this.admin, totalPrice = this.totalPrice
    )

fun PrintReceiptResponseDto.toPrintReceiptResponse(): PrintReceiptResponse =
    PrintReceiptResponse(
        fee = this.fee, feeCounter = this.feeCounter, price = this.price, receipt = this.receipt
    )

fun DeleteAccountRequest.toDeleteAccountRequestDto(): DeleteAccountRequestDto =
    DeleteAccountRequestDto(uuid = this.uuid, reason = this.reason)