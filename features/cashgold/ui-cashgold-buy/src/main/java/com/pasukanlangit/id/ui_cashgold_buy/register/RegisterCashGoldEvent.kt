package com.pasukanlangit.id.ui_cashgold_buy.register


sealed class RegisterCashGoldEvent {
    object GetProfile: RegisterCashGoldEvent()
    data class RegisterUser(val name: String, val email: String, val phoneNumber: String, val identityNumber: String, val taxNumber: String?): RegisterCashGoldEvent()
//    data class UpdateUser(val profession: String? = null, val zipCode: String? = null, val taxIdentityNumber: String, val incomePerYear: String? = null, val address: String? = null, val gender: String? = null, val lastEducation: String? = null, val city: String? = null, val uuid: String, val religion: String? = null, val birthPlace: String? = null, val province: String? = null, val phone: String, val identityNumber: String, val dayOfBirth: String? = null, val district: String? = null, val incomeSource: String? = null, val village: String? = null, val user: String? = null, val email: String, val maritalStatus: String? = null): RegisterCashGoldEvent()
    object RemoveHeadQueue: RegisterCashGoldEvent()
}
