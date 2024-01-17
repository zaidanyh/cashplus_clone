package com.pasukanlangit.id.library_core.datasource.utils

import java.math.BigInteger
import java.text.NumberFormat
import kotlin.math.roundToInt

fun getNumberRupiah(number: Double?): String {
    if(number == null) return "Rp. 0"
    return "Rp ${NumberFormat.getIntegerInstance().format(number.roundToInt())}"
}
fun getNumberRupiah(number: Int?): String {
    if(number == null) return "Rp. 0"
    return "Rp ${NumberFormat.getIntegerInstance().format(number)}"
}

fun getNumberRupiah(number: BigInteger?): String {
    if (number == null) return ""
    return "Rp ${NumberFormat.getNumberInstance().format(number)}"
}

fun getNumberRupiahWithoutRp(number: Double?): String {
    if(number == null) return "0"
    return NumberFormat.getIntegerInstance().format(number.roundToInt())
}
fun getNumberRupiahWithoutRp(number: Int?): String {
    if(number == null) return "0"
    return NumberFormat.getIntegerInstance().format(number)
}
fun getNumberRupiahWithoutRp(number: BigInteger?): String {
    if (number == null) return "0"
    return NumberFormat.getCurrencyInstance().format(number).replace("$", "")
}
