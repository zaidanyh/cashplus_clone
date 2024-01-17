package com.pasukanlangit.cashplus.di.nobu

import com.google.gson.Gson
import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.nobu.datasource.NobuRepositoryImpl
import com.pasukanlangit.id.nobu.datasource.network.NobuServices
import com.pasukanlangit.id.nobu.datasource.network.dto.error.NobuErrorParser
import com.pasukanlangit.id.nobu.domain.NobuRepository
import com.pasukanlangit.id.nobu.domain.usecase.*
import com.pasukanlangit.id.nobu.presentation.StateViewModel
import com.pasukanlangit.id.nobu.presentation.creation.ActivationViewModel
import com.pasukanlangit.id.nobu.presentation.history.HistoryNobuViewModel
import com.pasukanlangit.id.nobu.presentation.scan.ScanViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private fun provideNobuServices(retrofit: Retrofit): NobuServices =
    retrofit.create(NobuServices::class.java)

private fun provideErrorParser(gson: Gson): NobuErrorParser =
    NobuErrorParser(gson)

private fun provideNobuRepository(nobuServices: NobuServices, sharedPrefDataSource: SharedPrefDataSource, nobuErrorParser: NobuErrorParser): NobuRepository =
    NobuRepositoryImpl(
        service = nobuServices,
        sharedPref = sharedPrefDataSource,
        nobuErrorParser = nobuErrorParser
    )

private fun provideNobuUseCases(nobuRepository: NobuRepository): NobuUseCases =
    NobuUseCases(
        getProfile = GetProfile(nobuRepository),
        getIsBindedUseCase = IsBindedUseCase(nobuRepository),
        getSecurityQuestions = GetSecurityQuestions(nobuRepository),
        getTermCondition = GetTermCondition(nobuRepository),
        nobuAccountCreation = NobuAccountCreation(nobuRepository),
        nobuAccountBinding = GetAccountBinding(nobuRepository),
        verifyOtpUseCase = VerifyOtpUseCase(nobuRepository),
        sendResultScan = SendResultScan(nobuRepository),
        historyNobuUseCase = GetHistoryTrxNobu(nobuRepository),
        unBindAccount = UnBindAccountUseCase(nobuRepository)
    )

val NobuModule = module {
    single { provideNobuServices(get()) }
    single { provideErrorParser(get()) }
    single { provideNobuRepository(get(), get(), get()) }
    single { provideNobuUseCases(get()) }

    viewModel { StateViewModel(get()) }
    viewModel { ActivationViewModel(get()) }
    viewModel { ScanViewModel(get()) }
    viewModel { HistoryNobuViewModel(get()) }
}