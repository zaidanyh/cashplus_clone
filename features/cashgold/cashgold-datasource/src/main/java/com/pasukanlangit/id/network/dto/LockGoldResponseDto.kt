package com.pasukanlangit.id.network.dto

import com.google.gson.annotations.SerializedName

data class LockGoldResponseDto(

	@field:SerializedName("data")
	val data: LockGoldData?,
)

data class LockGoldData(

	@field:SerializedName("lockedId")
	val lockedId: String
)
