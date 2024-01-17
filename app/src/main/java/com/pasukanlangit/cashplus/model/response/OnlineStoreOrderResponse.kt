package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnlineStoreOrderResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String
) : Parcelable

@Parcelize
data class OnlineStoreOrderDetailItem(

	@field:SerializedName("note")
	val note: String,

	@field:SerializedName("quantity")
	val quantity: String,

	@field:SerializedName("price")
	val price: String,

	@field:SerializedName("product_id")
	val productId: String,

	@field:SerializedName("product_variant")
	val productVariant: String,

	@field:SerializedName("sub_total")
	val subTotal: Int,

	@field:SerializedName("discount")
	val discount: String,

	@field:SerializedName("image1")
	val image1: String,

	@field:SerializedName("order_id")
	val orderId: String,

	@field:SerializedName("product_name")
	val productName: String
) : Parcelable

@Parcelize
data class OrderTransaction(

	@field:SerializedName("member_id")
	val memberId: String,

	@field:SerializedName("no_resi")
	val noResi: String,

	@field:SerializedName("quantity")
	val quantity: Int,

	@field:SerializedName("total_discount")
	val totalDiscount: Int,

	@field:SerializedName("courier_fee")
	val courierFee: Int,

	@field:SerializedName("city")
	val city: String,

	@field:SerializedName("total_weight")
	val totalWeight: Int,

	@field:SerializedName("total_order")
	val totalOrder: Int,

	@field:SerializedName("order_status")
	val orderStatus: String,

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("payment_method_id")
	val paymentMethodId: String,

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("courier_service")
	val courierService: String,

	@field:SerializedName("shipping_address")
	val shippingAddress: String,

	@field:SerializedName("order_id")
	val orderId: String
) : Parcelable

@Parcelize
data class Data(

	@field:SerializedName("order_transaction")
	val orderTransaction: OrderTransaction,

	@field:SerializedName("order_detail")
	val orderDetail: List<OnlineStoreOrderDetailItem>
) : Parcelable
