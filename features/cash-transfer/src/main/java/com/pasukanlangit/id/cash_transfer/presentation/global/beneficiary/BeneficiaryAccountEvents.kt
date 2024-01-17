package com.pasukanlangit.id.cash_transfer.presentation.global.beneficiary


sealed class BeneficiaryAccountEvents {
    data class GetGlobalBanksNationsAndRelations(val countryCode: String): BeneficiaryAccountEvents()
    data class SavingGlobalBank(
        val bankCode: String, val bankAccNum: String, val bankAccName: String,
        val relationCode: String, val nationCode: String, val addressStreet: String,
        val addressCity: String
    ): BeneficiaryAccountEvents()
    object RemoveHeadMessageError: BeneficiaryAccountEvents()
}
