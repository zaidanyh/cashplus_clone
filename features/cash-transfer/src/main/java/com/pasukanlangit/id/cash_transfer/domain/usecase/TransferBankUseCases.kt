package com.pasukanlangit.id.cash_transfer.domain.usecase

data class TransferBankUseCases(
    val checkBalance: CheckBalance,
    val localBanksList: LocalBankList,
    val localBankSaved: LocalBankSaved,
    val deleteBankUseCase: DeleteBankUseCase,
    val localSavingBankUseCase: LocalSavingBankUseCase,

    val getCountryList: GetCountryList,
    val getBanksNationsAndRelationships: GetBanksNationsAndRelationships,
    val globalBankSaved: GlobalBankSaved,
    val savingGlobalBank: GlobalSavingBankUseCase,
    val getRateConversion: GetRateConversion,
    val globalBankCreateTrans: GlobalBankCreateTrans,

    val bankTransferTransaction: BankTransferTransaction
)