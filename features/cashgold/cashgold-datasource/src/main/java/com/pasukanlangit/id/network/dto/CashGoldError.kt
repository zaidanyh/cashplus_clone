package com.pasukanlangit.id.network.dto

data class CashGoldError(
	val rc: String?,
	val message: String?,
	val msg: String?,
	val data: CashGoldErrorData?
)

data class ErrorsItem(
	val code: Int?,
	val message: String?
)

data class CashGoldErrorData(
	val error: Error?
)

data class Error(
	val code: Int?,
	val message: String?,
	val errors: List<ErrorsItem>?
)

