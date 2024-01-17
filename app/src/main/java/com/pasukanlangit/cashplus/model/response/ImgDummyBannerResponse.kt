package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class ImgDummyBannerResponse(
	@field:SerializedName("thumbnail")
	val thumbnail: List<ThumbnailItem>

)

data class ThumbnailItem(

	@field:SerializedName("url")
	val url: String
)
