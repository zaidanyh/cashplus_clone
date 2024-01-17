package com.pasukanlangit.id.ui_downline_home.priceplan.product

import com.pasukanlangit.id.domain_downline.model.ProductMarkup

sealed class ProductPricePlanEvent {
    data class GetProductPricePlan(
        val codeMarkup: String,
    ): ProductPricePlanEvent()
    object RemoveHeadMessage: ProductPricePlanEvent()
    data class ChangeMarkupProduct(
        val markupCode: String,
        val markupDesc: String,
        val productCode: String,
        val markupValue: String
    ): ProductPricePlanEvent()
    data class DeleteMarkupProduct(
        val markupCode: String,
        val markupDesc: String,
        val productCode: String,
        val dataProduct: List<ProductMarkup>
    ): ProductPricePlanEvent()
    object RemoveMessageChangeMarkup: ProductPricePlanEvent()
}