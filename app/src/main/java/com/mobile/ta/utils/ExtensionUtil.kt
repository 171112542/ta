package com.mobile.ta.utils

import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * View Helper
 */
fun EditText.text() = this.text.toString()

fun TextView.text() = this.text.toString()

/**
 * Date Time Converter
 */
fun Long.toDateString(pattern: String): String =
    SimpleDateFormat(pattern, Locale.ENGLISH).format(this)

/**
 * LiveData Extensions
 */
fun <T> MutableLiveData<T>.publishChanges() {
    this.value = this.value
}