package com.pasukanlangit.cashplus.model.request_body

import com.google.gson.annotations.SerializedName

data class OnlineStoreOrderRequest(

	@field:SerializedName("courier")
	val courier: OnlineStoreOrderCourier,

	@field:SerializedName("voucher_code")
	val voucherCode: String,

	@field:SerializedName("uuid")
	val uuid: String,

	@field:SerializedName("payment_method")
	val paymentMethod: String,

	@field:SerializedName("products")
	val products: List<ProductsItemOrder>,

	@field:SerializedName("buyer")
	val buyer: Buyer
)

data class OnlineStoreOrderCourier(

	@field:SerializedName("courier_service")
	val courierService: String,

	@field:SerializedName("service")
	val service: String
)

data class ProductsItemOrder(

	@field:SerializedName("note")
	val note: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("qty")
	val qty: String,

	@field:SerializedName("product_variant")
	val productVariant: String
)

data class Buyer(

	@field:SerializedName("book_address_id")
	val bookAddressId: String
)
