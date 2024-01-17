package com.pasukanlangit.id.ui_downline_home.priceplan

import com.pasukanlangit.id.domain_downline.model.ProductMarkup

sealed class PricePlanEvent {
    object GetListPricePlan: PricePlanEvent()
    object RemoveListPricePlanMessage: PricePlanEvent()
    data class CreateReplaceMarkupPlan(
        val markupCode: String,
        val description: String,
        val isAddProduct: Boolean = false
    ): PricePlanEvent()
    object RemoveCreateMessage: PricePlanEvent()
    data class ApplyMarkupPlan(
        val markupCode: String,
        val downLinePhone: String
    ): PricePlanEvent()
    object RemoveApplyMessage: PricePlanEvent()
    data class UnApplyMarkupPlan(
        val downLinePhone: String
    ): PricePlanEvent()
    object RemoveUnApplyMessage: PricePlanEvent()
    data class DeleteMarkupPlan(
        val markupCode: String
    ): PricePlanEvent()
    object RemoveDeleteMessage: PricePlanEvent()
}