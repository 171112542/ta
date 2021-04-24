package com.mobile.ta.model.status

data class Status<T>(
    val status: StatusType,
    val data: T?,
    val message: String? = null
) {

    companion object {
        fun <T> success(data: T?) = Status(StatusType.SUCCESS, data)

        fun <T> error(message: String?, data: T? = null) = Status(StatusType.FAILED, data, message)
    }
}
