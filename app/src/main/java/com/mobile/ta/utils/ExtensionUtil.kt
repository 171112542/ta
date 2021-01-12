package com.mobile.ta.utils

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Date Time Converter
 */
fun Long.toDateString(pattern: String): String =
    SimpleDateFormat(pattern, Locale.ENGLISH).format(this)