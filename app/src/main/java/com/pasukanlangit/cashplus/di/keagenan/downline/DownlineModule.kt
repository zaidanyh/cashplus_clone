package com.pasukanlangit.cashplus.di.keagenan.downline

import com.pasukanlangit.id.core.datasource.sharedpref.SharedPrefDataSource
import com.pasukanlangit.id.datasource_downline.DownLineRepositoryImpl
import com.pasukanlangit.id.datasource_downline.network.DownLineService
import com.pasukanlangit.id.domain_downline.DownLineRepository
import com.pasukanlangit.id.domain_downline.usecases.*
import com.pasukanlangit.id.downline_nearby_agent.NearbyAgentViewModel
import com.pasukanlangit.id.library_core.datasource.utils.GlobalErrorParser
import com.pasukanlangit.id.ui_downline_home.DownLineViewModel
import com.pasukanlangit.id.ui_downline_home.mintasaldoqr.ScanQRConfirmationViewModel
import com.pasukanlangit.id.ui_downline_home.priceplan.PricePlanViewModel
import com.pasukanlangit.id.ui_downline_home.priceplan.findproduct.FindProductViewModel
import com.pasukanlangit.id.ui_downline_home.priceplan.product.ProductPricePlanViewModel
import com.pasukanlangit.id.ui_downline_home.subdownline.SubDownLineViewModel
import com.pasukanlangit.id.ui_downline_mutasi_summary_detail.MutationSumDetViewModel
import com.pasukanlangit.id.ui_downline_register.RegisterDownLineViewModel
import com.pasukanlangit.id.ui_downline_transfersaldo.TransferSaldoAgenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

private fun provideDownLineService(retrofit: Retrofit): DownLineService =
    retrofit.create(DownLineService::class.java)

private fun provideDownLineRepository(downlineService: DownLineService, sharedprefDataSource: SharedPrefDataSource, errorParser: GlobalErrorParser): DownLineRepository =
    DownLineRepositoryImpl(
        api = downlineService,
        sharedPref = sharedprefDataSource,
        errorParser = errorParser
    )

private fun provideDownLineUseCases(downLineRepository: DownLineRepository): DownLineUseCases =
    DownLineUseCases(
        getSummaryDownLine = GetSummaryDownLine(repository = downLineRepository),
        getDownLineTrxCount = GetDownLineTrxCount(repository = downLineRepository),
        setAllProductMarkup = SetAllProductMarkup(repository = downLineRepository),
        resetMarkup = ResetMarkup(repository = downLineRepository),
        getProvinces = GetProvinces(repository = downLineRepository),
        getCity = GetCity(repository = downLineRepository),
        getDistrict = GetDistrict(repository = downLineRepository),
        getVillage = GetVillage(repository = downLineRepository),
        registerDownLine = RegisterDownLine(repository = downLineRepository),
        transferDepositDownLine = TransferDepositDownLine(repository = downLineRepository),
        getMutationDownLine = GetMutationDownLine(repository = downLineRepository),
        getDownlineListForTransfer = GetDownlineListForTransfer(repository = downLineRepository),
        getDetailTransferDownLine = GetDetailTransferDownLine(repository = downLineRepository),
        getSummaryTransferDownLine = GetSummaryTransferDownLine(repository = downLineRepository),
        getNearAgent = GetNearAgent(repository = downLineRepository),
        generateQRCode = GenerateQRCode(repository = downLineRepository),
        scanQRAgent = ScanQRAgent(repository = downLineRepository),
        getMarkupPlans = GetMarkupPlan(repository = downLineRepository),
        getDetailMarkupPlan = GetDetailMarkupPlan(repository = downLineRepository),
        createReplaceMarkupPlan = CreateReplaceMarkupPlan(repository = downLineRepository),
        applyMarkupPlan = ApplyMarkupPlan(repository = downLineRepository),
        unApplyMarkupPlan = UnApplyMarkupPlan(repository = downLineRepository),
        deleteMarkupPlan = DeleteMarkupPlan(repository = downLineRepository),
        findProductMarkup = FindProductMarkupUseCase(repository = downLineRepository),
        updateLatLong = UpdateLatLongUseCase(repository = downLineRepository),
        balanceCheck = BalanceCheck(repository = downLineRepository)
    )

val downLineModule = module {
    single { provideDownLineService(get()) }
    single { provideDownLineRepository(get(), get(), get())}
    single { provideDownLineUseCases(downLineRepository = get()) }

    viewModel { DownLineViewModel(get(), get()) }
    viewModel { RegisterDownLineViewModel(get()) }
    viewModel { TransferSaldoAgenViewModel(get(), get()) }
    viewModel { MutationSumDetViewModel(get()) }
    viewModel { NearbyAgentViewModel(get(), get()) }
    viewModel { SubDownLineViewModel(get()) }
    viewModel { ScanQRConfirmationViewModel(get()) }
    viewModel { PricePlanViewModel(get()) }
    viewModel { ProductPricePlanViewModel(get()) }
    viewModel { FindProductViewModel(get()) }
}