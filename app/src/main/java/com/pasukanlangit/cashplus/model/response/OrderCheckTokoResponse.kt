package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class OrderCheckTokoResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: OrderCheckTokoData,

	@field:SerializedName("message")
	val message: String
)


data class OrderTransactionToko(

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

	@field:SerializedName("row_num")
	val rowNum: Int,

	@field:SerializedName("total_weight")
	val totalWeight: Int,

	@field:SerializedName("total_order")
	val totalOrder: Int,

	@field:SerializedName("order_date")
	val orderDate: String,

	@field:SerializedName("order_status")
	val orderStatus: String,

	@field:SerializedName("payment_method_id")
	val paymentMethodId: String,

	@field:SerializedName("total")
	val total: Int,

	@field:SerializedName("province")
	val province: String,

	@field:SerializedName("courier_service")
	val courierService: String,

	@field:SerializedName("complain_message")
	val complainMessage: String,

	@field:SerializedName("shipping_address")
	val shippingAddress: String,

	@field:SerializedName("order_id")
	val orderId: String
)

data class OrderCheckTokoData(

	@field:SerializedName("order_transaction")
	val orderTransaction: OrderTransactionToko,

	@field:SerializedName("order_detail")
	val orderDetail: List<ProductStoreDataItem>
)
