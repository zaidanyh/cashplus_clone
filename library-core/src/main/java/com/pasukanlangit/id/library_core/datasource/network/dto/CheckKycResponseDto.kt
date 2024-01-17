package com.pasukanlangit.id.library_core.datasource.network.dto

import com.google.gson.annotations.SerializedName

data class CheckKycResponseDto(

	@field:SerializedName("msg")
	val msg: String,

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("is_kyc_rejected")
	val isKycRejected: String,

	@field:SerializedName("taxidnumber")
	val taxidnumber: String,

	@field:SerializedName("reject_note")
	val rejectNote: String,

	@field:SerializedName("selfieidentitycard")
	val selfieidentitycard: String,

	@field:SerializedName("identitycard")
	val identitycard: String,

	@field:SerializedName("taxidnumber_sync_indogold")
	val taxidnumberSyncIndogold: String,

	@field:SerializedName("selfieidentitycard_sync_indogold")
	val selfieidentitycardSyncIndogold: String,

	@field:SerializedName("identitycard_sync_indogold")
	val identitycardSyncIndogold: String,

	@field:SerializedName("is_kyc_approved")
	val isKycApproved: String
)
