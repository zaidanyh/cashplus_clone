package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class ImgDummyResponse(

	@field:SerializedName("thumbnails")
	val thumbnails: List<ThumbnailsItem>
)

data class ThumbnailsItem(
	@field:SerializedName("url")
	val url: String
)