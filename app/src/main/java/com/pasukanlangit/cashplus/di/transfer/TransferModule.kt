package com.pasukanlangit.cashplus.di.transfer

import com.pasukanlangit.id.cash_transfer.datasource.TransferRepositoryImpl
import com.pasukanlangit.id.cash_transfer.datasource.network.TransferService
import com.pasukanlangit.id.cash_transfer.domain.TransferRepository
import com.pasukanlangit.id.cash_transfer.domain.usecase.*
import com.pasukanlangit.id.cash_transfer.presentation.global.GlobalBankViewModel
import com.pasukanlangit.id.cash_transfer.presentation.global.beneficiary.BeneficiaryAccountViewModel
import com.pasukanlangit.id.cash_transfer.presentation.global.detail.DetailGlobalTransferViewModel
import com.pasukanlangit.id.cash_transfer.presentation.global.find.FindBankCountryViewModel
import com.pasukanlangit.id.cash_transfer.presentation.local.LocalBankViewModel
import com.pasukanlangit.id.cash_transfer.presentation.local.transfer.TransferViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private fun provideTransferService(retrofit: Retrofit): TransferService =
    retrofit.create(TransferService::class.java)

private fun provideTransferRepository(transferService: TransferService, errorParse: GlobalErrorParser, sharedPref: SharedPrefDataSource) : TransferRepository =
    TransferRepositoryImpl(
        service = transferService,
        errorParser = errorParse,
        sharedPref = sharedPref
    )

private fun provideTransferUseCases(repository: TransferRepository): TransferBankUseCases =
    TransferBankUseCases(
        checkBalance = CheckBalance(repository = repository),
        localBanksList = LocalBankList(repository = repository),
        localBankSaved = LocalBankSaved(repository = repository),
        deleteBankUseCase = DeleteBankUseCase(repository = repository),
        localSavingBankUseCase = LocalSavingBankUseCase(repository = repository),
        getCountryList = GetCountryList(repository = repository),
        getBanksNationsAndRelationships = GetBanksNationsAndRelationships(repository = repository),
        globalBankSaved = GlobalBankSaved(repository = repository),
        savingGlobalBank = GlobalSavingBankUseCase(repository = repository),
        getRateConversion = GetRateConversion(repository = repository),
        globalBankCreateTrans = GlobalBankCreateTrans(repository = repository),
        bankTransferTransaction = BankTransferTransaction(repository = repository)
    )

val transferModule = module {
    single { provideTransferService(get()) }
    single { provideTransferRepository(get(), get(), get()) }
    single { provideTransferUseCases(get()) }

    viewModel { LocalBankViewModel(get(), get()) }
    viewModel { TransferViewModel(get(), get()) }
    viewModel { GlobalBankViewModel(get(), get()) }
    viewModel { FindBankCountryViewModel(get(), get()) }
    viewModel { BeneficiaryAccountViewModel(get(), get()) }
    viewModel { DetailGlobalTransferViewModel(get(), get()) }
}