package com.pasukanlangit.id.rebate.domain.usecase

data class RebateUseCases(
    val getRebate: GetRebate,
    val getRebatePerProduct: GetRebatePerProduct,
    val getRemainingRebate: GetRemainingRebate
)