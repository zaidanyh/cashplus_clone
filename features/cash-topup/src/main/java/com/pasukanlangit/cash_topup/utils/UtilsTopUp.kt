package com.pasukanlangit.cash_topup.utils

import android.os.Parcelable
import com.pasukanlangit.cash_topup.R
import kotlinx.parcelize.Parcelize

enum class MenuTopUp(val value: String, val payMethodCode: String) {
    BANK_TRANSFER("Bank Transfer", ""),
    VIRTUAL_ACCOUNT("Virtual Account", "02"),
    MERCHANT("Merchant", "03"),
    EWALLET("E-Wallet", "05"),
    OTHER("Lainnya", "08")
}

@Parcelize
data class MenuViaTopUpPayment(
    val key: Int,
    val img: Int,
    val menu: String,
    val bankCode: String,
    val payMethodCode: String? = null,
    val payName: String? = null
): Parcelable

object TopUpUtils {
    fun getMenuTopUp(): List<String> = listOf(
        MenuTopUp.BANK_TRANSFER.value, MenuTopUp.VIRTUAL_ACCOUNT.value, MenuTopUp.MERCHANT.value,
        MenuTopUp.EWALLET.value, MenuTopUp.OTHER.value
    )

    private fun allMenuPayment(): List<MenuViaTopUpPayment> =
        listOf(
            MenuViaTopUpPayment(
                key = 0, img = R.drawable.ic_bank_bca, menu = MenuTopUp.BANK_TRANSFER.value,
                bankCode = "BCA"
            ),
//            MenuViaTopUpPayment(
//                key = 1, img = R.drawable.ic_bank_bri, menu = MenuTopUp.BANK_TRANSFER.value,
//                bankCode = "BRI"
//            ),
//            MenuViaTopUpPayment(
//                key = 2, img = R.drawable.ic_bank_bni, menu = MenuTopUp.BANK_TRANSFER.value,
//                bankCode = "BNI"
//            ),
            MenuViaTopUpPayment(
                key = 3, img = R.drawable.ic_bank_mandiri, menu = MenuTopUp.BANK_TRANSFER.value,
                bankCode = "MANDIRI"
            ),

            // Virtual Account
            MenuViaTopUpPayment(
                key = 4, img = R.drawable.ic_bank_mandiri, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
                bankCode = "VA_BMRI", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
                payName = "Mandiri"
            ),
            MenuViaTopUpPayment(
                key = 5, img = R.drawable.ic_bank_bni, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
                bankCode = "VA_BNIN", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
                payName = "BNI"
            ),
            MenuViaTopUpPayment(
                key = 6, img = R.drawable.ic_bank_bri, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
                bankCode = "VA_BRIN", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
                payName = "BRI"
            ),
//            MenuViaTopUpPayment(
//                key = 7, img = R.drawable.ic_bank_bca, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
//                bankCode = "VA_CENA", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
//                payName = MenuTopUp.VIRTUAL_ACCOUNT.payMethodName + "BCA"
//            ),
            MenuViaTopUpPayment(
                key = 8, img = R.drawable.ic_bank_danamon, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
                bankCode = "VA_BDIN", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
                payName = "Danamon"
            ),
            MenuViaTopUpPayment(
                key = 9, img = R.drawable.ic_bank_cimbniaga, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
                bankCode = "VA_BNIA", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
                payName = "Cimb Niaga"
            ),
            MenuViaTopUpPayment(
                key = 10, img = R.drawable.ic_bank_hana, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
                bankCode = "VA_HNBN", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
                payName = "Hana Bank"
            ),
            MenuViaTopUpPayment(
                key = 11, img = R.drawable.ic_bank_permata, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
                bankCode = "VA_BBBA", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
                payName = "Permata"
            ),
            MenuViaTopUpPayment(
                key = 12, img = R.drawable.ic_bank_maybank, menu = MenuTopUp.VIRTUAL_ACCOUNT.value,
                bankCode = "VA_IBBK", payMethodCode = MenuTopUp.VIRTUAL_ACCOUNT.payMethodCode,
                payName = "Maybank"
            ),
            MenuViaTopUpPayment(
                key = 13, img = R.drawable.ic_merchan_alfmart, menu = MenuTopUp.MERCHANT.value,
                bankCode = "CVS_ALMA", payMethodCode = MenuTopUp.MERCHANT.payMethodCode,
                payName = "Alfamart"
            ),
            MenuViaTopUpPayment(
                key = 14, img = R.drawable.ic_ovo, menu = MenuTopUp.EWALLET.value,
                bankCode = "EWALLET_OVOE", payMethodCode = MenuTopUp.EWALLET.payMethodCode,
                payName = "OVO"
            ),
            MenuViaTopUpPayment(
                key = 15, img = R.drawable.image_qris, menu = MenuTopUp.OTHER.value,
                bankCode = "QRIS_QSHP", payMethodCode = MenuTopUp.OTHER.payMethodCode
            )
        )

    fun getMenuPayment(name: String): List<MenuViaTopUpPayment> {
        val allMenuPayment = allMenuPayment()
        return allMenuPayment.filter { value ->
            value.menu == name
        }
    }
}