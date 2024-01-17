package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class TrackingOrderResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: TrackingOrderData,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
data class ManifestItem(

	@field:SerializedName("city_name")
	val cityName: String,

	@field:SerializedName("manifest_time")
	val manifestTime: String,

	@field:SerializedName("manifest_description")
	val manifestDescription: String,

	@field:SerializedName("manifest_code")
	val manifestCode: Int,

	@field:SerializedName("manifest_date")
	val manifestDate: String
) : Parcelable

data class TrackingOrderData(

	@field:SerializedName("summary")
	val summary: Summary,

	@field:SerializedName("manifest")
	val manifest: List<ManifestItem>,

	@field:SerializedName("delivered")
	val delivered: Boolean,

	@field:SerializedName("details")
	val details: TrackingOrderDetails,

	@field:SerializedName("delivery_status")
	val deliveryStatus: DeliveryStatus
)

data class TrackingOrderDetails(

	@field:SerializedName("shipper_address2")
	val shipperAddress2: String,

	@field:SerializedName("waybill_date")
	val waybillDate: String,

	@field:SerializedName("shipper_address3")
	val shipperAddress3: String,

	@field:SerializedName("receiver_city")
	val receiverCity: String,

	@field:SerializedName("origin")
	val origin: String,

	@field:SerializedName("shipper_address1")
	val shipperAddress1: String,

	@field:SerializedName("destination")
	val destination: String,

	@field:SerializedName("weight")
	val weight: String,

	@field:SerializedName("waybill_time")
	val waybillTime: String,

	@field:SerializedName("receiver_address3")
	val receiverAddress3: String,

	@field:SerializedName("waybill_number")
	val waybillNumber: String,

	@field:SerializedName("receiver_address2")
	val receiverAddress2: String,

	@field:SerializedName("receiver_address1")
	val receiverAddress1: String,

	@field:SerializedName("shippper_name")
	val shippperName: String,

	@field:SerializedName("shipper_city")
	val shipperCity: String,

	@field:SerializedName("receiver_name")
	val receiverName: String
)

data class DeliveryStatus(

	@field:SerializedName("pod_receiver")
	val podReceiver: String,

	@field:SerializedName("pod_time")
	val podTime: String,

	@field:SerializedName("pod_date")
	val podDate: String,

	@field:SerializedName("status")
	val status: String
)

data class Summary(

	@field:SerializedName("waybill_date")
	val waybillDate: String,

	@field:SerializedName("courier_name")
	val courierName: String,

	@field:SerializedName("waybill_number")
	val waybillNumber: String,

	@field:SerializedName("courier_code")
	val courierCode: String,

	@field:SerializedName("origin")
	val origin: String,

	@field:SerializedName("destination")
	val destination: String,

	@field:SerializedName("service_code")
	val serviceCode: String,

	@field:SerializedName("receiver_name")
	val receiverName: String,

	@field:SerializedName("shipper_name")
	val shipperName: String,

	@field:SerializedName("status")
	val status: String
)
