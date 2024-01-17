package com.pasukanlangit.id.cash_transfer.presentation.global.find

sealed class FindBankCountryEvents {
    object GetCountries: FindBankCountryEvents()
    object RemoveHeadMessageError: FindBankCountryEvents()
}