package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TopUpBankResponse(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("bank_acc_name")
	val bankAccName: String,

	@field:SerializedName("amount")
	val amount: String,

	@field:SerializedName("bank")
	val bank: String,

	@field:SerializedName("unique_id")
	val uniqueId: String,

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("bank_acc_num")
	val bankAccNum: String,

	@field:SerializedName("dest")
	val dest: String,

	var nominalTopUp: String? = null
): Parcelable
