package com.pasukanlangit.id.nobu.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class ProfileResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msg")
    val msg: String,
    @field:SerializedName("full_name")
    val fullName : String,
    @field:SerializedName("balance")
    val balance : Double,
    @field:SerializedName("my_point")
    val point: Double,
    @field:SerializedName("address")
    val address : String,
    @field:SerializedName("prov")
    val prov : String,
    @field:SerializedName("city")
    val city : String,
    @field:SerializedName("district")
    val district : String,
    @field:SerializedName("village")
    val village : String,
    @field:SerializedName("zipcode")
    val zipcode : String,
    @field:SerializedName("nik")
    val nik: String,
    @field:SerializedName("email")
    val email : String,
    @field:SerializedName("img_url")
    val imgUrl : String,
    @field:SerializedName("referral")
    val referral: String,
    @field:SerializedName("is_kyc_approved")
    val isKycApproved: String,
    @field:SerializedName("owner_name")
    val ownerName: String,
    @field:SerializedName("tempat_lahir")
    val placeOfBorn: String,
    @field:SerializedName("tgl_lahir")
    val dateOfBirth: String,
    @field:SerializedName("my_referral_code")
    val myReferralCode: String?,
    @field:SerializedName("referral_full_name")
    val referralFullName: String,
    @field:SerializedName("referral_code")
    val referralCode: String,
    @field:SerializedName("phones")
    val phones: List<Phones>?
)

data class Phones(
    @field:SerializedName("phone")
    val phone: String
)
