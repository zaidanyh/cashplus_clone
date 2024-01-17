package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class ProfileResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("referral_full_name")
	val referralFullName: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("owner_name")
	val ownerName: String,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("phones")
	val phones: List<PhonesItem>,

	@field:SerializedName("ip_address")
	val ipAddress: String,

	@field:SerializedName("zipcode")
	val zipcode: String,

	@field:SerializedName("callback_url")
	val callbackUrl: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("full_name")
	val fullName: String,

	@field:SerializedName("referral")
	val referral: String,

	@field:SerializedName("balance")
	val balance: Double,

	@field:SerializedName("img_url")
	val imgUrl: String,

	@field:SerializedName("district")
	val district: String,

	@field:SerializedName("village")
	val village: String,

	@field:SerializedName("prov")
	val prov: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("is_kyc_approved")
	val isKycApproved: String
)

data class PhonesItem(

	@field:SerializedName("phone")
	val phone: String
)
