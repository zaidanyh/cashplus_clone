package com.pasukanlangit.cashplus.di.indigold

import com.google.gson.Gson
import com.pasukanlangit.id.CashGoldRepository
import com.pasukanlangit.id.ui_cashgold_address.addupdate.AddUpdateAddressViewModel
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.network.CashGoldRepositoryImpl
import com.pasukanlangit.id.network.CashGoldService
import com.pasukanlangit.id.network.utils.CashGoldErrorParser
import com.pasukanlangit.id.ui_cashgold_address.home.AddressGoldHoveViewModel
import com.pasukanlangit.id.ui_cashgold_buy.buy.BuyCashGoldViewModel
import com.pasukanlangit.id.ui_cashgold_buy.home.CashGoldHomeViewModel
import com.pasukanlangit.id.ui_cashgold_buy.register.RegisterCashGoldViewModel
import com.pasukanlangit.id.ui_cashgold_navigation.DashboardGoldViewModel
import com.pasukanlangit.id.ui_cashgold_updateprofile.UpdateProfileCashGoldViewModel
import com.pasukanlangit.id.ui_cashgold_withdraw.home.CashGoldWDViewModel
import com.pasukanlangit.id.ui_cashgold_withdraw.tag.TagWithDrawViewModel
import com.pasukanlangit.id.usecase.*
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

//Provide API Service
private fun provideCashGoldService(retrofit: Retrofit): CashGoldService =
    retrofit.create(CashGoldService::class.java)

private fun provideGson(): Gson = Gson()

private fun provideErrorParser(gson: Gson): CashGoldErrorParser =
    CashGoldErrorParser(gson)
//Provide Repository
private fun provideIndoGoldRepository(cashGoldService: CashGoldService, sharedprefDataSource: SharedPrefDataSource, errorParser: CashGoldErrorParser): CashGoldRepository =
    CashGoldRepositoryImpl(cashGoldService, sharedprefDataSource, errorParser)

//Provide UseCase
private fun provideCashGoldUseCase(cashGoldRepository: CashGoldRepository): CashGoldUseCase =
    CashGoldUseCase(
        getIndoGoldBalance = GetIndoGoldBalance(repository = cashGoldRepository),
        getGoldPrice = GetGoldPrice(repository = cashGoldRepository),
        getChartUseCase = GetChartUseCase(repository = cashGoldRepository),
        checkStatusRegister = CheckStatusRegister(repository = cashGoldRepository),
        registerUseCase = RegisterUseCase(repository = cashGoldRepository),
        getSt24Profile = GetSt24Profile(repository = cashGoldRepository),
        kycStatus = CheckKycStatus(repository = cashGoldRepository),
        lockGold = LockGold(repository = cashGoldRepository),
        sendTrx = SendTrx(repository = cashGoldRepository),
        getProductWithDraw = GetProductWithDraw(repository = cashGoldRepository),
        mainAddress = MainAddress(repository = cashGoldRepository),
        addressList = AddressList(repository = cashGoldRepository),
        getProvinces = GetProvinces(repository = cashGoldRepository),
        getCity = GetCity(repository = cashGoldRepository),
        getVillage = GetVillage(repository = cashGoldRepository),
        getDistrict = GetDistrict(repository = cashGoldRepository),
        addAddress = AddAddress(repository = cashGoldRepository),
        updateAddress = UpdateAddress(repository = cashGoldRepository),
        deleteAddress = DeleteAddress(repository = cashGoldRepository),
        updateUserCashGold = UpdateUserCashGold(repository = cashGoldRepository),
        checkKtpNotEmpty = CheckKtpNotEmpty(repository = cashGoldRepository),
        withDrawBook = WithDrawBook(repository = cashGoldRepository),
        getProfileCashGold = GetUserCashGold(repository = cashGoldRepository)
    )



@FlowPreview
val cashGoldModule = module {
    single { provideCashGoldService(get()) }
    single { provideGson() }
    single { provideErrorParser(get()) }
    single { provideIndoGoldRepository(get(), get(), get()) }
    single { provideCashGoldUseCase(get()) }

    viewModel { CashGoldHomeViewModel(get()) }
    viewModel { RegisterCashGoldViewModel(get()) }
    viewModel { BuyCashGoldViewModel(get()) }
    viewModel { CashGoldWDViewModel(get()) }
    viewModel { TagWithDrawViewModel(get()) }
    viewModel { AddressGoldHoveViewModel(get()) }
    viewModel { UpdateProfileCashGoldViewModel(get()) }
    viewModel { AddUpdateAddressViewModel(get()) }
    viewModel { DashboardGoldViewModel(get()) }
}