package com.pasukanlangit.id.domain_downline.model

data class RegisterDownLineRequest(
	val uuid: String,
	val phone: String,
	val fullName: String,
	val ownerName: String,
	val email: String,
	val password: String,
	val address: String,
	val prov: String,
	val city: String,
	val district: String,
	val village: String,
	val zipcode: String
)
