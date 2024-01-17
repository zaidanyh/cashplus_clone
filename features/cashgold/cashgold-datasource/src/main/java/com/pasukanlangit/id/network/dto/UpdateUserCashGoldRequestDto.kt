package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class UpdateUserCashGoldRequestDto(

	@field:SerializedName("profession")
	val profession: String? = null,

	@field:SerializedName("zipCode")
	val zipCode: String? = null,

	@field:SerializedName("taxIdentityNumber")
	val taxIdentityNumber: String?,

	@field:SerializedName("incomePerYear")
	val incomePerYear: String? = null,

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("lastEducation")
	val lastEducation: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("religion")
	val religion: String? = null,

	@field:SerializedName("birthPlace")
	val birthPlace: String? = null,

	@field:SerializedName("province")
	val province: String? = null,

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("identityNumber")
	val identityNumber: String,

	@field:SerializedName("dayOfBirth")
	val dayOfBirth: String? = null,

	@field:SerializedName("district")
	val district: String? = null,

	@field:SerializedName("incomeSource")
	val incomeSource: String? = null,

	@field:SerializedName("village")
	val village: String? = null,

	@field:SerializedName("name")
	val user: String? = null,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("maritalStatus")
	val maritalStatus: String? = null
)
