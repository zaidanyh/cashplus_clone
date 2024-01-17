package com.pasukanlangit.id.library_core.utils

enum class TrxStatus {
    PENDING,
    SUCCESS,
    FAILED
}
object TrxUtil {
    fun getTrxStatusByCode(value: String?) : TrxStatus {
        val responseCodeSuccess = arrayOf(
            "200", "601", "901", "700"
        )
        return when (value) {
            in responseCodeSuccess -> {
                TrxStatus.SUCCESS
            }
            "1200" -> {
                TrxStatus.PENDING
            }
            else -> {
                TrxStatus.FAILED
            }
        }
    }
}