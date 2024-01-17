package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class AddressMainBookResponse(

	@field:SerializedName("rc")
	val rc: String,

	@field:SerializedName("data")
	val data: AddressBookData?,

	@field:SerializedName("message")
	val message: String
)

@Parcelize
@Entity(tableName = "address_book")
data class AddressBookData(

	@field:SerializedName("member_id")
	var memberId: String,

	@field:SerializedName("address")
	var address: String,

	@field:SerializedName("address_type")
	var addressType: String,

	@field:SerializedName("province_id")
	var provinceId: String,

	@field:SerializedName("time_start")
	var timeStart: String,

	@field:SerializedName("pos_code")
	var posCode: String,

	@field:SerializedName("receiver_name")
	var receiverName: String,

	@PrimaryKey
	@field:SerializedName("book_address_id")
	var bookAddressId: String,

	@field:SerializedName("phone_number")
	var phoneNumber: String,

	@field:SerializedName("is_main_address")
	var isMainAddress: String,

	@field:SerializedName("row_num")
	var rowNum: Int,

	@field:SerializedName("city_id")
	var cityId: String,

	@field:SerializedName("subdistrict_id")
	var subdistrictId: String,

	var isSelected : Boolean = false,
	var userId: String ?= ""
) : Parcelable
