package com.pasukanlangit.id.ui_downline_home.utils

import com.pasukanlangit.id.domain_downline.model.MarkupPlan
import com.pasukanlangit.id.domain_downline.model.MarkupProductResponse
import com.pasukanlangit.id.domain_downline.model.ProductMarkup

fun List<MarkupProductResponse>.toParcelMarkupProduct(): MarkupProductResponseParcel =
    MarkupProductResponseParcel(
        dataProduct = this.map {
            DataProductMarkup(
                kodeProduct = it.kodeProduct,
                kodeProvider = it.kodeProvider,
                category = it.category,
                price = it.price,
                outlet_price = it.outlet_price,
                isActive = it.isActive,
                markup = it.markup,
                mainMarkup = it.mainMarkup
            )
        }
    )

fun MarkupProductResponseParcel.toListMarkupProductResponse(): List<MarkupProductResponse> =
    this.dataProduct.map {
        MarkupProductResponse(
            kodeProduct = it.kodeProduct,
            kodeProvider = it.kodeProvider,
            category = it.category,
            price = it.price,
            outlet_price = it.outlet_price,
            isActive = it.isActive,
            markup = it.markup,
            mainMarkup = it.mainMarkup
        )
    }

fun List<MarkupProductResponse>.toListProductMarkup(): List<ProductMarkup> =
    this.map {
        ProductMarkup(
            productCode = it.kodeProduct, markup = it.markup
        )
    }

fun List<SummaryProduct>.toSummaryAddProduct(): SummaryAddProduct =
    SummaryAddProduct(
        dataSummary = this.map {
            SummaryProduct(
                codeProduct = it.codeProduct,
                markup = it.markup,
                category = it.category,
                positionIndex = it.positionIndex
            )
        }
    )

fun List<SummaryProduct>.toListProductMarkupRequest(): List<ProductMarkup> =
    this.map {
        ProductMarkup(
            productCode = it.codeProduct, markup = it.markup
        )
    }

fun List<MarkupPlan>.toListMarkupPlanParcelable(): List<MarkupPlanParcelable> =
    this.map {
        MarkupPlanParcelable(
            codeMarkupPlan = it.codeMarkupPlan, description = it.description
        )
    }