package com.pasukanlangit.cashplus.utils

data class MyResponse<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?): MyResponse<T> {
            return MyResponse(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(msg: String, data: T?): MyResponse<T> {
            return MyResponse(
                Status.ERROR,
                data,
                msg
            )
        }

        fun <T> loading(data: T?): MyResponse<T> {
            return MyResponse(
                Status.LOADING,
                data,
                null
            )
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}