package com.pasukanlangit.id.domain_downline.usecases

data class DownLineUseCases(
    val getSummaryDownLine: GetSummaryDownLine,
    val getDownLineTrxCount: GetDownLineTrxCount,
    val setAllProductMarkup: SetAllProductMarkup,
    val resetMarkup: ResetMarkup,
    val getProvinces: GetProvinces,
    val getCity: GetCity,
    val getDistrict: GetDistrict,
    val getVillage: GetVillage,
    val registerDownLine: RegisterDownLine,
    val transferDepositDownLine: TransferDepositDownLine,
    val getMutationDownLine: GetMutationDownLine,
    val getDownlineListForTransfer: GetDownlineListForTransfer,
    val getDetailTransferDownLine: GetDetailTransferDownLine,
    val getSummaryTransferDownLine: GetSummaryTransferDownLine,
    val getNearAgent: GetNearAgent,
    val generateQRCode: GenerateQRCode,
    val scanQRAgent: ScanQRAgent,
    val getMarkupPlans: GetMarkupPlan,
    val getDetailMarkupPlan: GetDetailMarkupPlan,
    val createReplaceMarkupPlan: CreateReplaceMarkupPlan,
    val applyMarkupPlan: ApplyMarkupPlan,
    val unApplyMarkupPlan: UnApplyMarkupPlan,
    val deleteMarkupPlan: DeleteMarkupPlan,
    val findProductMarkup: FindProductMarkupUseCase,
    val updateLatLong: UpdateLatLongUseCase,
    val balanceCheck: BalanceCheck
)
