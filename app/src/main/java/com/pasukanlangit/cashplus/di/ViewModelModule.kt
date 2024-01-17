package com.pasukanlangit.cashplus.di

import com.pasukanlangit.cashplus.domain.usecase.*
import com.pasukanlangit.cashplus.repository.MainRepository
import com.pasukanlangit.cashplus.ui.addressbook.AddressBookViewModel
import com.pasukanlangit.cashplus.ui.addressbook.add.AddUpdateAddressBookViewModel
import com.pasukanlangit.cashplus.ui.all_menus.AllMenusViewModel
import com.pasukanlangit.cashplus.ui.all_menus.DetailMenusViewModel
import com.pasukanlangit.cashplus.ui.onlinestore.ProductDetailViewModel
import com.pasukanlangit.cashplus.ui.cartproduct.CartViewModel
import com.pasukanlangit.cashplus.ui.checkout.PembayaranTagihanViewModel
import com.pasukanlangit.cashplus.ui.checkout.TransactionPayViewModel
import com.pasukanlangit.cashplus.ui.ewallet.DirectProductPurchasesViewModel
import com.pasukanlangit.cashplus.ui.info_promo.InfoPromoListViewModel
import com.pasukanlangit.cashplus.ui.injectvoucher.InjectVoucherViewModel
import com.pasukanlangit.cashplus.ui.login.ResetPwViewModel
import com.pasukanlangit.cashplus.ui.omni.packageomni.PackageOmniViewModel
import com.pasukanlangit.cashplus.ui.pages.history.HistoryDetailTransportViewModel
import com.pasukanlangit.cashplus.ui.pages.history.HistoryDetailViewModel
import com.pasukanlangit.cashplus.ui.pages.history.HistoryViewModel
import com.pasukanlangit.cashplus.ui.pages.history.detailtokoonline.DetailHistoryTokoViewModel
import com.pasukanlangit.cashplus.ui.pages.home.HomeViewModel
import com.pasukanlangit.cashplus.ui.pages.others.settings.password.ChangePassViewModel
import com.pasukanlangit.cashplus.ui.pages.others.settings.pin.ChangePinWithoutLastViewModel
import com.pasukanlangit.cashplus.ui.pages.others.settings.profile.close.CloseAccountViewModel
import com.pasukanlangit.cashplus.ui.pembayaran_game_menu.GameMenuViewModel
import com.pasukanlangit.cashplus.ui.pembayarancart.PembayaranCartViewModel
import com.pasukanlangit.cashplus.ui.pembayarancart.pay.EnterPinOnlineStoreViewModel
import com.pasukanlangit.cashplus.ui.pembayarancart.pay.PayOnlineStoreViewModel
import com.pasukanlangit.cashplus.ui.product.StatusProductViewModel
import com.pasukanlangit.cashplus.view_model.*
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private fun provideAppUseCases(mainRepository: MainRepository): AppUseCases =
    AppUseCases(
        registerUseCase = RegisterUseCase(mainRepository),
        loginUseCase = LoginUseCase(mainRepository),
        loginByOtpUseCase = LoginByOtpUseCase(mainRepository),
        resetPasswordUseCase = ResetPasswordUseCase(mainRepository),
        resetPINUseCase = ResetPINUseCase(mainRepository),
        getProfile = GetProfile(mainRepository),
        changeReferral = ChangeReferralUseCase(mainRepository),
        logOut = LogoutUseCase(mainRepository),
        topUpQris = TopUpQrisUseCase(mainRepository),
        uploadPhotoProfile = UpdatePhotoProfile(mainRepository),
        verifyOtp = VerifyOtpUseCase(mainRepository),
        updateFCMToken = UpdateFCMToken(mainRepository),
        getBalanceDashboard = GetBalanceDashboard(mainRepository),
        searchHistoryTransaction = SearchHistoryTransaction(mainRepository),
        omniMenuUseCase = OmniMenuUseCase(mainRepository),
        omniPackageList = OmniPackageList(mainRepository),
        getBalance = GetBalance(mainRepository),
        omniPackageOrder = OmniPackageOrder(mainRepository),
        billPayTransaction = BillPayTransactionUseCase(mainRepository),
        injectVoucher = InjectVoucher(mainRepository),
        transactionBulk = TransactionBulk(mainRepository),
        updateEMail = UpdateEmailUseCase(mainRepository),
        productStatusUseCase = ProductStatusUseCase(mainRepository),
        updateSellingPrice = UpdateSellingPrice(mainRepository),
        calculateUnitPrice = CalculateUnitPrice(mainRepository),
        getSellingPrice = GetSellingPrice(mainRepository),
        printReceiptUseCase = PrintReceiptUseCase(mainRepository),
        deleteAccount = DeleteAccountUseCase(mainRepository)
    )

val vmModule = module {
    single { provideAppUseCases(get()) }

    viewModel { LoginViewModel(get(), get(), androidContext()) }
    viewModel { RegisterViewModel(get(), get(), get(), androidContext()) }
    viewModel { HomeViewModel(get(), get(), get(), androidContext()) }
    viewModel { HistoryViewModel(get(), get(), get(), androidContext()) }
    viewModel { MainViewModel(get(), get(), androidContext()) }
    viewModel { ProductDetailViewModel(get(), get()) }
    viewModel { TopupProviderViewModel(get(), androidContext()) }
    viewModel { TransactionViewModel(get(), androidContext()) }
    viewModel { TopUpEcommerceViewModel(get(), androidContext()) }
    viewModel { PembayaranViewModel(get(), androidContext()) }
    viewModel { TopUpPLNViewModel(get(), androidContext()) }
    viewModel { TopUpGameViewModel(get(), androidContext()) }
    viewModel { PembayaranBulananViewModel(get(), androidContext()) }
    viewModel { ChangeProfileViewModel(get(), androidContext()) }
    viewModel { OtherChangePinViewModel(get(), androidContext()) }
    viewModel { AllProductEcommerceViewModel(get(), get(), androidContext()) }
    viewModel { InfoPromoListViewModel() }
    viewModel { AllMenusViewModel(get(), androidContext()) }
    viewModel { DetailMenusViewModel(get(), androidContext()) }
    viewModel { ResetPwViewModel(get(), get(), get(), androidContext()) }
    viewModel { PembayaranTagihanViewModel(get(), androidContext()) }
    viewModel { GameMenuViewModel(get(), androidContext()) }
    viewModel { ChangePinWithoutLastViewModel(get(), get(), get(), androidContext()) }
    viewModel { TransactionPayViewModel(get(), androidContext()) }
    viewModel { CartViewModel(get(), get()) }
    viewModel { PembayaranCartViewModel(get(), get(), androidContext()) }
    viewModel { AddressBookViewModel(get(), get(), androidContext()) }
    viewModel { AddUpdateAddressBookViewModel(get(), get()) }
    viewModel { PayOnlineStoreViewModel(get(), androidContext()) }
    viewModel { EnterPinOnlineStoreViewModel(get(), androidContext()) }
    viewModel { DetailHistoryTokoViewModel(get(), androidContext()) }
    viewModel { HistoryDetailViewModel(get(), get(), get(), androidContext()) }
    viewModel { ChangePassViewModel(get(), androidContext()) }
    viewModel { HistoryDetailTransportViewModel(get(), androidContext()) }
    viewModel { PackageOmniViewModel(get(), get(), androidContext()) }
    viewModel { InjectVoucherViewModel(get(), get(), androidContext()) }
    viewModel { StatusProductViewModel(get(), get(), androidContext()) }
    viewModel { DirectProductPurchasesViewModel(get(), get()) }
    viewModel { CloseAccountViewModel(get(), get()) }
}