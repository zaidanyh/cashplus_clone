package com.pasukanlangit.id.network.utils

import com.google.gson.Gson
import com.pasukanlangit.id.library_core.datasource.utils.ErrorParser
import com.pasukanlangit.id.network.dto.CashGoldError

class CashGoldErrorParser(
    private val gson: Gson
): ErrorParser {
    override fun parse(errorString: String?): String? {
        val errorBody =  gson.fromJson(errorString, CashGoldError::class.java)
        return mapErrorMessage(errorBody)
    }

    fun mapErrorMessage(error: CashGoldError): String?{
        val message = error.message ?: error.msg ?: return null

        return "$message. ${error.data?.error?.message ?: ""}"
    }
}