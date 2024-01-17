package com.pasukanlangit.cashplus.di.topup

import com.pasukanlangit.cash_topup.datasource.TopUpRepositoryImpl
import com.pasukanlangit.cash_topup.datasource.network.TopUpServices
import com.pasukanlangit.cash_topup.domain.TopUpRepository
import com.pasukanlangit.cash_topup.domain.usecase.*
import com.pasukanlangit.cash_topup.presentation.NewTopUpViewModel
import com.pasukanlangit.cash_topup.presentation.via_bank.ViaBankViewModel
import com.pasukanlangit.cash_topup.presentation.via_ewallet.ViaEWalletViewModel
import com.pasukanlangit.cash_topup.presentation.via_merchant.ViaMerchantViewModel
import com.pasukanlangit.cash_topup.presentation.via_others.TopUpViaOthersViewModel
import com.pasukanlangit.cash_topup.presentation.via_others.qris.ViaQRISViewModel
import com.pasukanlangit.cash_topup.presentation.via_va.ViaVirtualAccountViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private fun provideTopUpService(retrofit: Retrofit): TopUpServices =
    retrofit.create(TopUpServices::class.java)

private fun provideTopUpRepository(services: TopUpServices, errorParser: GlobalErrorParser, sharedPref: SharedPrefDataSource): TopUpRepository =
    TopUpRepositoryImpl(
        services = services,
        errorParser = errorParser,
        sharedPref = sharedPref
    )

private fun provideTopUpUseCase(repository: TopUpRepository): TopUpUseCase =
    TopUpUseCase(
        balanceCheck = BalanceCheckUseCase(repository = repository),
        costNicePay = CostNicePayUseCase(repository = repository),
        viaBank = ViaBankUseCase(repository = repository),
        viaVA = ViaVAUseCase(repository = repository),
        viaMerchant = ViaMerchantUseCase(repository = repository),
        viaEWallet = ViaEWalletUseCase(repository = repository),
        viaQRIS = ViaQRISUseCase(repository = repository),
        flipAcceptList = GetFlipAcceptListUseCase(repository = repository),
        costFlipAccept = CostFlipAcceptUseCase(repository = repository),
        createBillFlip = FlipAcceptCreateBillUseCase(repository = repository)
    )

val topUpModule = module {
    single { provideTopUpService(get()) }
    single { provideTopUpRepository(get(), get(), get()) }
    single { provideTopUpUseCase(get()) }

    viewModel { NewTopUpViewModel(get(), get()) }
    viewModel { ViaBankViewModel(get(), get()) }
    viewModel { ViaVirtualAccountViewModel(get(), get()) }
    viewModel { ViaMerchantViewModel(get(), get()) }
    viewModel { ViaEWalletViewModel(get(), get()) }
    viewModel { TopUpViaOthersViewModel(get(), get()) }
    viewModel { ViaQRISViewModel(get(), get()) }
}