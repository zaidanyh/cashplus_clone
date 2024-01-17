package com.pasukanlangit.id.kyc_datasource

import com.pasukanlangit.id.kyc_datasource.network.dto.*
import com.pasukanlangit.id.kyc_domain.domain.model.*

fun ProfileResponseDto.toEKycProfileUser(): EKycProfileUser =
    EKycProfileUser(
        full_name = this.fullName, address = this.address, prov = this.prov, city = this.city,
        district = this.district, village = this.village, zipcode = this.zipcode, nik = this.nik,
        email = this.email, img_url = this.imgUrl, phones = this.phones?.toListUserPhone(),
        owner_name = this.ownerName, placeOfBorn = this.placeOfBorn, dateOfBirth = dateOfBirth
    )

private fun List<UserPhones>.toListUserPhone(): List<UserPhone> = this.map { UserPhone(phone = it.phone) }

fun EkycRequest.toUploadFileEKycRequestDto(): UploadFileEKycRequestDto =
    UploadFileEKycRequestDto(
        uuid = this.uuid, uploadType = this.uploadType, base64Data = this.base64
    )

fun UploadFileEKycResponseDto.toEKycResponse(): EkycResponse =
    EkycResponse(
        uploadType = this.uploadType, idType = this.idType, fileExt = this.fileExt
    )

fun EKycVerifyRequest.toEKycVerifyRequestDto(): VerifyEKycRequestDto =
    VerifyEKycRequestDto(uuid = this.uuid, verificationType = this.verifyingType)

fun VerifyEKycResponseDto.toEKycVerifyResponse(): EKycVerifyResponse =
    EKycVerifyResponse(
        rc = this.rc, msg = this.msg, ocrNIK = this.ocrNik,
        ocrFullName = this.ocrFullName, ownerName = this.ownerName
    )

