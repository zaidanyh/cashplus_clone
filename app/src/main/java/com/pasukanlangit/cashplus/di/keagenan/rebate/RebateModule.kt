package com.pasukanlangit.cashplus.di.keagenan.rebate

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.rebate.datasource.RebateRepositoryImpl
import com.pasukanlangit.id.rebate.datasource.network.RebateService
import com.pasukanlangit.id.rebate.domain.RebateRepository
import com.pasukanlangit.id.rebate.domain.usecase.GetRebate
import com.pasukanlangit.id.rebate.domain.usecase.GetRebatePerProduct
import com.pasukanlangit.id.rebate.domain.usecase.GetRemainingRebate
import com.pasukanlangit.id.rebate.domain.usecase.RebateUseCases
import com.pasukanlangit.id.rebate.presentation.RebateViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit


private fun provideRebateService(retrofit: Retrofit): RebateService =
    retrofit.create(RebateService::class.java)

private fun provideRebateRepository(rebateService: RebateService, sharedprefDataSource: SharedPrefDataSource, errorParser: GlobalErrorParser): RebateRepository =
    RebateRepositoryImpl(
        service = rebateService,
        sharedPref = sharedprefDataSource,
        errorParser = errorParser
    )

private fun provideRebateUseCases(rebateRepository: RebateRepository): RebateUseCases =
    RebateUseCases(
        getRebate = GetRebate(repository = rebateRepository),
        getRebatePerProduct = GetRebatePerProduct(repository = rebateRepository),
        getRemainingRebate = GetRemainingRebate(repository = rebateRepository)
    )

val rebateModule = module {
    single { provideRebateService(get()) }
    single { provideRebateRepository(get(), get(), get())}
    single { provideRebateUseCases(get()) }

    viewModel { RebateViewModel(get()) }
}