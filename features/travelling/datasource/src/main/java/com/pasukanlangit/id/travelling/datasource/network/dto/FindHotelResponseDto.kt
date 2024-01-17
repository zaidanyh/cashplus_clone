package com.pasukanlangit.id.travelling.datasource.network.dto

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.pasukanlangit.id.travelling.datasource.utils.*

data class FindHotelResponseDto(

    @field:SerializedName("rc")
    val rc: String,

    @field:SerializedName("message")
    val message: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @field:SerializedName("data")
    val data: FindHotel?,

    @field:SerializedName("endOfData")
    val endOfData: String?
)

data class FindHotel(
    @field:SerializedName("result")
    val result: String? = null,

    @field:SerializedName("data")
    val dataHotel: List<DataHotel>? = null
)

data class DataHotel(
    @field:SerializedName("hotel_key")
    val hotelKey: String,

    @field:SerializedName("hotel_code")
    val hotelCode: String,

    @field:SerializedName("hotel_name")
    val hotelName: String,

    @field:SerializedName("hotel_rating")
    val hotelRating: String?,

    @field:SerializedName("hotel_address")
    val hotelAddress: String?,

    @field:SerializedName("hotel_city")
    val hotelCity: String,

    @field:SerializedName("hotel_country")
    val hotelCountry: String,

    @field:SerializedName("hotel_latitude")
    val hotelLatitude: String,

    @field:SerializedName("hotel_longitude")
    val hotelLongitude: String,

    @field:SerializedName("hotel_marketname")
    val hotelMarketname: String?,

    @field:SerializedName("hotel_checkin")
    val hotelCheckin: String,

    @field:SerializedName("hotel_checkout")
    val hotelCheckout: String,

    @JsonAdapter(JsonArrayOfStringConverter::class, nullSafe = true)
    @field:SerializedName("hotel_image")
    val hotelImage: List<String>? = null,

    @field:SerializedName("hotel_remark")
    val hotelRemark: String?,

    @field:SerializedName("hotel_extrabeds")
    val hotelExtrabeds: String?,

    @field:SerializedName("hotel_facilities")
    val hotelFacilities: List<String>?,

    @field:SerializedName("hotel_room")
    val hotelRoom: List<ItemRoomHotel>? = null,

    @field:SerializedName("hotel_cancelation")
    val hotelCancelation: String?
)

data class ItemRoomHotel(
    @field:SerializedName("room_rate_key")
    val roomRateKey: String,

    @field:SerializedName("room_code")
    val roomCode: String?,

    @field:SerializedName("room_name")
    val roomName: String,

    @field:SerializedName("room_request")
    val roomRequest: String,

    @field:SerializedName("room_price")
    val roomPrice: Int,

    @field:SerializedName("room_nta")
    val roomNta: Int,

    @field:SerializedName("room_realnta")
    val roomRealNta: Int?,

    @field:SerializedName("room_image")
    val roomImage: String?,

    @field:SerializedName("room_boardname")
    val roomBoardname: String,

    @field:SerializedName("room_cancellation")
    val roomCancellation: String?,

    @field:SerializedName("room_included")
    val roomIncluded: String?,

//    @field:SerializedName("room_surcharge")
//    val roomSurcharge: List<RoomSurcharge>?
)

data class RoomSurcharge(

    @field:SerializedName("surcharge_nama")
    val surchargeName: String?,

    @field:SerializedName("surcharge_total")
    val surchargeTotal: String?,

    @field:SerializedName("surcharge_keterangan")
    val surchargeDesc: String?
)
