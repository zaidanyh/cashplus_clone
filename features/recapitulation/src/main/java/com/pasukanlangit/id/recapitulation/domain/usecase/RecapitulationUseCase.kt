package com.pasukanlangit.id.recapitulation.domain.usecase

data class RecapitulationUseCase(
    val allRecapTrans: GetAllRecapTrans,
    val mutationDeposit: GetMutationBalance,
    val summaryMutationDeposit: GetSummaryMutationBalance
)