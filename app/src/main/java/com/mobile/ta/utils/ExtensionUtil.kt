package com.mobile.ta.utils

import android.text.format.Time
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.config.Constants
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Object helper
 */
fun String?.isNotNullOrBlank() = this.isNullOrBlank().not()

fun Boolean?.orFalse() = this ?: false

fun Boolean?.orTrue() = this ?: true

fun <T> T?.isNull() = this == null

/**
 * View Helper
 */
fun EditText.text() = this.text.toString()

fun EditText.notBlankValidate(errorObject: String): Boolean {
    var isError = true
    val text = this.text()
    this.error = when {
        text.isBlank() -> Constants.getEmptyErrorMessage(errorObject)
        else -> {
            isError = false
            null
        }
    }
    return isError.not()
}

fun TextView.text() = this.text.toString()

/**
 * Date Time Converter
 */
fun now(): Date {
    val offset = TimeZone.getTimeZone(
        Time.getCurrentTimezone()).rawOffset + TimeZone.getTimeZone(Time.getCurrentTimezone()).dstSavings
    return Date(System.currentTimeMillis() - offset)
}

fun Long.toDateString(pattern: String, isMillis: Boolean = false): String =
    SimpleDateFormat(pattern, Locale.ENGLISH).format(
        this * if (isMillis) {
            1000
        } else {
            1
        }
    )

fun Date.toDateString(pattern: String): String =
    SimpleDateFormat(pattern, Locale.ENGLISH).format(this)

/**
 * LiveData Extensionsp
 */
fun <T> MutableLiveData<T>.publishChanges() {
    this.value = this.value
}