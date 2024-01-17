package com.pasukanlangit.id.domain_downline.utils

import com.pasukanlangit.id.domain_downline.model.DetailMarkupPlan
import com.pasukanlangit.id.domain_downline.model.ProductMarkup

enum class PagingDataType(val value: String) {
    NONE("NONE"),
    SEARCH_BY_NAME("NAME"),
    SEARCH_BY_PHONE("PHONE")
}

fun List<DetailMarkupPlan>.toListProductMarkup(): List<ProductMarkup> =
    this.map {
        ProductMarkup(productCode = it.codeProduct, markup = it.markup)
    }