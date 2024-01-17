package com.pasukanlangit.id.nobu.datasource.network.dto.error

import com.google.gson.Gson
import com.pasukanlangit.id.library_core.datasource.utils.ErrorParser

class NobuErrorParser(
    private val gson: Gson
): ErrorParser {
    override fun parse(errorString: String?): String? {
        val errorBody = gson.fromJson(errorString, NobuErrorResponse::class.java)
        return mapErrorMessage(errorBody)
    }

    private fun mapErrorMessage(error: NobuErrorResponse): String? {
        val message = error.msg ?: error.message ?: return null
        if(message == "invalid user or signature error"){
            return "Nomer Telepon atau Password Salah"
        }
        if (!error.is_binded.isNullOrEmpty()) {
            return "Belum terbinding"
        }
        if (message.contains("account not found", true)) {
            return "Akun tidak ditemukan"
        }
        if (message.contains("insufficient", true)) return "Saldo anda tidak mencukupi"
        if (error.rc == "4000702") return "Data anda tidak memenuhi syarat, silahkan ubah data anda."
        if (error.rc == "4030614") return "No.HP atau Email yang anda masukkan sudah terdaftar"
        if (error.rc == "4030615") return "No. Hp dan Email sudah terdaftar"
        return message
    }
}