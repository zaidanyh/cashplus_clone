package com.pasukanlangit.id.nobu.datasource.utils

import com.pasukanlangit.id.core.model.PhoneList
import com.pasukanlangit.id.core.model.ProfileResponse
import com.pasukanlangit.id.nobu.datasource.network.dto.*
import com.pasukanlangit.id.nobu.domain.model.*

fun SendResultScanRequest.toSendResultScanRequestDto(): SendResultScanRequestDto =
    SendResultScanRequestDto(
        uuid = this.uuid,
        resultScan = this.resultScan,
        callBackUrl = this.callBackUrl
    )

fun QrMpmResponseDto.toSendResultScanResponse(): SendResultScanResponse =
    SendResultScanResponse(
        url = this.redirectUrl
    )


fun NobuRequest.toNobuRequestDto(): NobuRequestDto =
    NobuRequestDto(uuid = this.uuid)

fun ProfileResponseDto.toProfileResponse(): ProfileResponse =
    ProfileResponse(
        full_name = this.fullName, balance = this.balance, point = this.point, address = this.address,
        prov = this.prov, city = this.city, district = this.district, village = this.village, zipcode = this.zipcode,
        nik = this.nik, email = this.email, img_url = this.imgUrl, referral = this.referral, is_kyc_approved = this.isKycApproved,
        owner_name = this.ownerName, placeOfBorn = this.placeOfBorn, dateOfBirth = this.dateOfBirth,
        my_referral_code = this.myReferralCode, referral_full_name = this.referralFullName,
        referral_code = this.referralCode, phones = this.phones?.map {
            PhoneList(it.phone)
        }
    )

fun IsBindedResponseDto.toIsBindedResponse(): IsBindedResponse =
    IsBindedResponse(isBinded = this.isBinded)

fun TermConditionResponseDto.toTermConditionResponse(): TermConditionResponse =
    TermConditionResponse(
        url = this.data.url,
        textHtml = this.data.html
    )

fun SecurityQuestionResponseDto.toSecurityQuestions(): List<SecurityQuestions> =
    this.data.map {
        SecurityQuestions(securityQuestion = it.securityQuestion, securityCode = it.securityCode)
    }

fun AccountCreationRequest.toAccountCreationRequestDto(): AccountCreationRequestDto =
    AccountCreationRequestDto(
        uuid = this.uuid, phone = this.phone, email = this.email, name = this.name,
        securityAnswer = this.securityAnswer, securityQuestion = this.securityQuestion,
        securityCode = this.securityCode, redirectUrl = this.redirectUrl
    )

fun AccountCreationResponseDto.toAccountCreationResponse(): AccountCreationResponse =
    AccountCreationResponse(
        referenceNo = this.referenceNo, partnerReferenceNp = this.partnerReferenceNp, authCode = this.authCode,
        apiKey = this.apiKey, accountId = this.accountId, state = this.state, additionalInfo = Additional(
            referralCode = this.additional.referralCode,
            securityAnswer = this.additional.securityAnswer,
            securityQuestion = this.additional.securityQuestion,
            securityCode = this.additional.securityCode
        )
    )

fun AccountBindingRequest.toAccountBindingRequestDto(): AccountCreationRequestDto =
    AccountCreationRequestDto(
        uuid = this.uuid, phone = this.phone, email = this.email,
        name = this.name, redirectUrl = this.redirectUrl
    )

fun AccountBindingResponseDTO.toAccountBindingResponse(): AccountBindingResponse =
    AccountBindingResponse(
        msg = this.msg, userInfo = UserInfoResponse(publicUserId = this.userInfo?.publicUserId),
        accessTokenInfo = AccessTokenResponse(
            expiresIn = this.accessTokenInfo?.expiresIn,
            tokenStatus = this.accessTokenInfo?.tokenStatus,
            reExpiresIn = this.accessTokenInfo?.reExpiresIn,
            accessToken = this.accessTokenInfo?.accessToken,
            refreshToken = this.accessTokenInfo?.refreshToken
        ), redirectUrl = this.redirectUrl, referenceNo = this.referenceNo, nextAction = this.nextAction,
        params = ParamsResponse(
            pinWebViewUrl = this.params?.pinWebViewUrl, redirectToDeeplink = this.params?.redirectToDeeplink,
            action = this.params?.action
        ), responseCode = this.responseCode, rc = this.rc, additionalInfo = AdditionalInfoResponse(this.additionalInfo?.name),
        partnerReferenceNo = this.partnerReferenceNo, id = this.id, responseMessage = this.responseMessage,
        linkageToken = this.linkageToken
    )

fun VerifyOtpRequest.toVerifyOtpRequestDto(): VerifyOtpRequestDto =
    VerifyOtpRequestDto(
        uuid = this.uuid,
        otp = this.otp
    )

fun VerifyOtpResponseDTO.toVerifyResponse(): VerifyOtpResponse =
    VerifyOtpResponse(qParamsURL = this.qParamsURL)

fun BalanceInquiryResponseDto.toBalanceInquiryResponse(): BalanceInquiryResponse =
    BalanceInquiryResponse(
        currency = this.accountInfos.first().availableBalance.currency,
        value = this.accountInfos.first().availableBalance.value
    )

fun HistoryTrxRequest.toHistoryTrxRequestDto(): HistoryTrxRequestDto =
    HistoryTrxRequestDto(
        uuid = this.uuid, dateStart = this.dateStart, dateEnd = this.dateEnd,
        pageNumber = this.pageNumber, pageSize = this.pageSize
    )

fun HistoryTrxResponseDto.toHistoryTrxResponse(): HistoryTrxResponse =
    HistoryTrxResponse(
        listData = this.detailData.map {
            DataTrx(
                dateTimeTrx = it.dateTime, currency = it.amount.currency, amount = it.amount.value, remark = it.remark,
                status = it.status, type = it.type, it.additionalInfo.feeAmount,
                it.additionalInfo.referenceNo, receiptNum = it.additionalInfo.receiptNo
            )
        }, this.additionalInfo.totalPage
    )

fun IsBindedResponseDto.toUnbindResponse(): UnbindResponse =
    UnbindResponse(
        rc = this.rc, msg = this.msg
    )

