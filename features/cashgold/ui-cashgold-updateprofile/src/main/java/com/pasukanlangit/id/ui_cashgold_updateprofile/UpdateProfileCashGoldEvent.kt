package com.pasukanlangit.id.ui_cashgold_updateprofile

sealed class UpdateProfileCashGoldEvent {
    object GetProfileGold: UpdateProfileCashGoldEvent()
    object RemoveHeadQueue: UpdateProfileCashGoldEvent()
    data class SaveProfile(
        val user: String?,
        val identityNumber: String,
        val gender: String?,
        val maritalStatus: String?,
        val birthPlace: String?,
        val dayOfBirth: String?,
        val email: String,
        val phone: String,
        val religion: String?,
        val taxIdentityNumber: String?,
        val lastEducation: String?,
        val profession: String?,
        val incomeSource: String?,
        val incomePerYear: String?,
        val province: String?,
        val city: String?,
        val district: String?,
        val village: String?,
        val address: String?,
        val zipCode: String?
    ): UpdateProfileCashGoldEvent()
    object GetProvinces: UpdateProfileCashGoldEvent()
    data class GetCity(val provinces: String): UpdateProfileCashGoldEvent()
    data class GetDistrict(val city: String): UpdateProfileCashGoldEvent()
    data class GetVillage(val city: String, val district: String): UpdateProfileCashGoldEvent()
}