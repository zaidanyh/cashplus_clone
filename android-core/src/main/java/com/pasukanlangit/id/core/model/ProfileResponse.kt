package com.pasukanlangit.id.core.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileResponse(
    @field:SerializedName("full_name")
    val full_name : String,
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
    val img_url : String,
    @field:SerializedName("referral")
    val referral: String,
    @field:SerializedName("is_kyc_approved")
    val is_kyc_approved: String,
    @field:SerializedName("owner_name")
    val owner_name: String,
    @field:SerializedName("tempat_lahir")
    val placeOfBorn: String,
    @field:SerializedName("tgl_lahir")
    val dateOfBirth: String,
    @field:SerializedName("my_referral_code")
    val my_referral_code: String?,
    @field:SerializedName("referral_full_name")
    val referral_full_name: String,
    @field:SerializedName("referral_code")
    val referral_code: String,
    @field:SerializedName("phones")
    val phones: List<PhoneList>?,
):Parcelable

@Parcelize
data class PhoneList (
    @field:SerializedName("phone")
    val phone : String
) : Parcelable
