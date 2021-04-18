package com.mobile.ta.model

class Status<T>(val status: StatusType, val data: T?, val message: String?) {
    enum class StatusType {
        SUCCESS, FAILED
    }

    companion object {
        fun <T> success(data: T?): Status<T> {
            return Status(StatusType.SUCCESS, data, null)
        }

        fun <T> error(message: String?, data: T?): Status<T> {
            return Status(StatusType.FAILED, data, message)
        }
    }
}