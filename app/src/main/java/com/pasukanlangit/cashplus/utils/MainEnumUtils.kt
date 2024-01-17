package com.pasukanlangit.cashplus.utils

enum class CategoryProduct(val value: String, val title: String) {
    GAMES("#GAMES", "Voucher Game"),
    E_WALLET("#EMONEY", "E-Wallet"),
    VOUCHER("#VOUCHER", "Voucher"),
    BILL_PAYMENT("#PEMBAYARAN", "Pembayaran")
}

enum class MethodSendOTP(val value: String) {
    WHATSAPP("WHATSAPP"),
    EMAIL("EMAIL"),
    SMS("SMS")
}