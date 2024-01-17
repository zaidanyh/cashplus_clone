package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class UserCashGoldCheckResponseDto(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: UserCashGoldData,

	@field:SerializedName("message")
	val message: String
)

data class BankAcc(

	@field:SerializedName("account_num")
	val accountNum: String,

	@field:SerializedName("bank_id")
	val bankId: Int,

	@field:SerializedName("account_name")
	val accountName: String,

	@field:SerializedName("bank_name")
	val bankName: String
)

data class UserCashGold(

	@field:SerializedName("npwp_status")
	val npwpStatus: String,

	@field:SerializedName("address")
	val address: String,

	@field:SerializedName("gender")
	val gender: String,

	@field:SerializedName("income_per_year")
	val incomePerYear: String,

	@field:SerializedName("date_of_birth")
	val dateOfBirth: String,

	@field:SerializedName("source_of_income")
	val sourceOfIncome: String,

	@field:SerializedName("ktp")
	val ktp: String,

	@field:SerializedName("npwp")
	val npwp: String,

	@field:SerializedName("ktp_address")
	val ktpAddress: KtpAddress,

	@field:SerializedName("bank_acc")
	val bankAcc: BankAcc,

	@field:SerializedName("last_education")
	val lastEducation: String,

	@field:SerializedName("zip_code")
	val zipCode: String,

	@field:SerializedName("religion")
	val religion: String,

	@field:SerializedName("place_of_birth")
	val placeOfBirth: String,

	@field:SerializedName("marital_status")
	val maritalStatus: String,

	@field:SerializedName("user_id")
	val userId: String,

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("job")
	val job: String,

	@field:SerializedName("ktp_status")
	val ktpStatus: String,

	@field:SerializedName("email")
	val email: String,

	@field:SerializedName("selfie_status")
	val selfieStatus: String
)

data class KtpAddress(

	@field:SerializedName("provinsi")
	val provinsi: String,

	@field:SerializedName("kecamatan")
	val kecamatan: String,

	@field:SerializedName("kodepos")
	val kodepos: String,

	@field:SerializedName("kabupaten")
	val kabupaten: String,

	@field:SerializedName("kelurahan")
	val kelurahan: String,

	@field:SerializedName("alamat")
	val alamat: String
)

data class UserCashGoldData(

	@field:SerializedName("data")
	val userCashGold: UserCashGold?
)
