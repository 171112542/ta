package com.mobile.ta.utils

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import java.text.SimpleDateFormat
import java.util.*

/**
 * Object helper
 */
fun String?.isNotNullOrBlank() = this.isNullOrBlank().not()

fun Boolean?.orFalse() = this ?: false

fun Boolean?.orTrue() = this ?: true

fun <T> T?.isNull() = this == null

fun <T> T?.isNotNull() = this != null

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
 * LiveData Extensions
 */
fun <T> MutableLiveData<T>.publishChanges() {
    this.value = this.value
}

fun String?.getOrDefault(context: Context): String =
    this ?: context.getString(R.string.default_string_field)

fun Int?.getOrDefaultInt(): Int =
    this ?: 0