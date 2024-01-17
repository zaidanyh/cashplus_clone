package com.pasukanlangit.cashplus.di.kyc

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.kyc_datasource.KycRepositoryImpl
import com.pasukanlangit.id.kyc_datasource.network.KycApiService
import com.pasukanlangit.id.kyc_domain.domain.KycRepository
import com.pasukanlangit.id.kyc_domain.domain.usecase.*
import com.pasukanlangit.id.kyc_presentation.pages.completing.CompletingDataViewModel
import com.pasukanlangit.id.kyc_presentation.pages.init.KYCViewModel
import com.pasukanlangit.id.network.utils.CashGoldErrorParser
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

//Provide API Service
private fun provideKycService(retrofit: Retrofit): KycApiService =
    retrofit.create(KycApiService::class.java)

//Provide Repository
private fun provideKycRepository(apiService:KycApiService, sharedprefDataSource: SharedPrefDataSource, errorParser: CashGoldErrorParser): KycRepository =
    KycRepositoryImpl(
        api = apiService,
        sharedPref = sharedprefDataSource,
        errorParser = errorParser
    )

//Provide UseCase
private fun provideKycUseCase(kycRepository: KycRepository): KycUseCase =
    KycUseCase(
        eKycProfile = EKycProfile(repository = kycRepository),
        eKycStatus = EKycStatusUseCase(repository = kycRepository),
        eKycUploadVerify = EKycUploadVerifyUseCase(repository = kycRepository),
        eKycOnlyVerify = EKycVerifyUseCase(repository = kycRepository)
    )

@FlowPreview
val kycModule = module {
    single { provideKycService(get()) }
    single { provideKycRepository(get(), get(), get()) }
    single { provideKycUseCase(get()) }

    viewModel { KYCViewModel(get()) }
    viewModel { CompletingDataViewModel(get()) }
}