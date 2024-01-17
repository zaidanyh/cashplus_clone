package com.pasukanlangit.cashplus.di.recapitulation

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.recapitulation.datasource.RecapRepositoryImpl
import com.pasukanlangit.id.recapitulation.datasource.network.RecapServices
import com.pasukanlangit.id.recapitulation.domain.RecapRepository
import com.pasukanlangit.id.recapitulation.domain.usecase.*
import com.pasukanlangit.id.recapitulation.presentation.deposit.RecapDepositViewModel
import com.pasukanlangit.id.recapitulation.presentation.profit.RecapProfitViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private fun provideRecapServices(retrofit: Retrofit): RecapServices =
    retrofit.create(RecapServices::class.java)

private fun provideRecapRepository(services: RecapServices, errorParser: GlobalErrorParser, sharedPref: SharedPrefDataSource): RecapRepository =
    RecapRepositoryImpl(
        service = services, errorParser = errorParser, sharedPref = sharedPref
    )

private fun provideRecapUseCase(repository: RecapRepository): RecapitulationUseCase =
    RecapitulationUseCase(
        allRecapTrans = GetAllRecapTrans(repository = repository),
        mutationDeposit = GetMutationBalance(repository = repository),
        summaryMutationDeposit = GetSummaryMutationBalance(repository = repository)
    )

val recapitulationModule = module {
    single { provideRecapServices(get()) }
    single { provideRecapRepository(get(), get(), get()) }
    single { provideRecapUseCase(get()) }

    viewModel { RecapProfitViewModel(get(), get()) }
    viewModel { RecapDepositViewModel(get(), get()) }
}