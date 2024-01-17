package com.pasukanlangit.id.ui_downline_home.priceplan.findproduct

import com.pasukanlangit.id.domain_downline.model.ProductMarkup
import com.pasukanlangit.id.ui_downline_home.priceplan.PricePlanEvent

sealed class FindProductEvent {
    data class GetProductPricePlanBySearch(
        val codeMarkup: String,
        val keyword: String,
        val mainMarkup: String
    ): FindProductEvent()
    object RemoveHeadMessageSearch: FindProductEvent()

    data class AddProductMarkup(
        val productCode: String,
        val markupValue: String,
        val category: String,
        val positionIndex: Int?
    ): FindProductEvent()
    data class RemoveMarkupProduct(
        val productCode: String
    ): FindProductEvent()
    data class ChangeProductMarkup(
        val productCode: String,
        val markupValue: String
    ): FindProductEvent()
    object ClearProductMarkup: FindProductEvent()

    data class CreateReplace(
        val markupCode: String,
        val description: String,
        val products: List<ProductMarkup>? = null
    ): FindProductEvent()
    object RemoveCreateMessage: FindProductEvent()
}