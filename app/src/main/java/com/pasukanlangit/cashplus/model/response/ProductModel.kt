package com.pasukanlangit.cashplus.model.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class ProductModel (
    val kode_provider :String,
    val kode_produk: String,
    val dsc : String,
    val short_dsc: String,
    val category: String,
    val price : String,
    val outlet_price: String,
    val admin: String,
    val img_url: String,
    val img_url_2: String,
    val opr_name : String,
    val product_type: String
) : Parcelable