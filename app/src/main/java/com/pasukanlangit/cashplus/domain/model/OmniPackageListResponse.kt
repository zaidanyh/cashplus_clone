package com.pasukanlangit.cashplus.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class OmniPackageListResponse(
    val subcategory: List<String>?,
    val packages: List<ResponsePackage>?,
    val error: String?
)

@Parcelize
data class ResponsePackage(
    val autoRenewal: String,
    val bonuses: List<BonusPackage>?,
    val businesspId: String,
    val id: String,
    val longDesc: String,
    val name: String,
    val originalPrice: Int,
    val price: Int,
    val shortDesc: String,
    val subcategory: String,
    val tag: String,
    val terms: String,
    val validity: String
): Parcelable

@Parcelize
data class BonusPackage(
    val classData: String,
    val consumptionTime: String,
    val highlight: String,
    val longDesc: String,
    val name: String,
    val quota: String,
    val shortDesc: String,
    val subClass: String,
    val validity: String,
    val viral: Boolean
): Parcelable
