package com.pasukanlangit.cashplus.model.response

import com.google.gson.annotations.SerializedName

data class OmniPackageListResponseDto(
    @field:SerializedName("rc")
    val rc: String,
    @field:SerializedName("msisdn")
    val msidn: Long,
    @field:SerializedName("error")
    val error: String?,
    @field:SerializedName("subcategory")
    val subcategory: List<String>?,
    @field:SerializedName("package_list")
    val packages: List<Package>?
)

data class Package(
    @field:SerializedName("autorenewal")
    val autoRenewal: String,
    @field:SerializedName("bonus")
    val bonuses: List<Bonus>?,
    @field:SerializedName("businesspid")
    val businesspId: String,
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("longdesc")
    val longDesc: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("originalprice")
    val originalPrice: Int,
    @field:SerializedName("price")
    val price: Int,
    @field:SerializedName("shortdesc")
    val shortDesc: String,
    @field:SerializedName("subcategory")
    val subcategory: String,
    @field:SerializedName("tag")
    val tag: String,
    @field:SerializedName("terms")
    val terms: String,
    @field:SerializedName("validity")
    val validity: String
)

data class Bonus(
    @field:SerializedName("class")
    val classData: String,
    @field:SerializedName("consumptiontime")
    val consumptionTime: String,
    @field:SerializedName("highlight")
    val highlight: String,
    @field:SerializedName("longdesc")
    val longDesc: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("quota")
    val quota: String,
    @field:SerializedName("shortdesc")
    val shortDesc: String,
    @field:SerializedName("subclass")
    val subClass: String,
    @field:SerializedName("validity")
    val validity: String,
    @field:SerializedName("viral")
    val viral: String
)
