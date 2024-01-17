package com.pasukanlangit.id.library_core.datasource.utils

import com.google.gson.Gson
import com.pasukanlangit.id.library_core.datasource.network.dto.ErrorResponse

interface ErrorParser {
    fun parse(errorString: String?): String?
}
class GlobalErrorParser(
    private val gson: Gson
): ErrorParser {

    override fun parse(errorString: String?): String? {
        val errorBody =  gson.fromJson(errorString, ErrorResponse::class.java)
        return mapErrorMessage(errorBody)
    }

    private fun mapErrorMessage(error: ErrorResponse): String?{
        val message = error.msg ?: error.message ?: return null
        if(message == "invalid user or signature error"){
            return "Nomer Telepon atau Password Salah"
        }
        if(message.contains("terdaftar",true)){
            return "Nomer yang kamu gunakan sudah terdaftar"
        }

//        if(rc == "14"){
//            return "Reset Password Code atau Nomer Hp tidak valid"
//        }
        return message
    }
}