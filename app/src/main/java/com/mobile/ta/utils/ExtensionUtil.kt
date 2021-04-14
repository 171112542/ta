package com.mobile.ta.utils

import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.config.Constants
import com.mobile.ta.model.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale

/**
 * Object helper
 */
fun String?.isNotNullOrBlank() = this.isNullOrBlank().not()

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
fun now(): Date = Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC))

fun Long.toDateString(pattern: String): String =
    SimpleDateFormat(pattern, Locale.ENGLISH).format(this * 1000)

fun Date.toDateString(pattern: String): String =
    SimpleDateFormat(pattern, Locale.ENGLISH).format(this)

@ExperimentalCoroutinesApi
suspend fun <T> CollectionReference.fetchData(
    mapper: (MutableList<DocumentSnapshot>) -> MutableList<T>
): Status<MutableList<T>> {
    lateinit var statusData: Status<MutableList<T>>
    get().addOnFailureListener {
        statusData = Status.error(it.message.orEmpty())
    }.addOnSuccessListener { data ->
        statusData = Status.success(mapper.invoke(data.documents))
    }.await()
    return statusData
}

@ExperimentalCoroutinesApi
suspend fun <T> DocumentReference.fetchData(mapper: (DocumentSnapshot) -> T): Status<T> {
    lateinit var statusData: Status<T>
    get().addOnFailureListener {
        statusData = Status.error(it.message.orEmpty())
    }.addOnSuccessListener { data ->
        statusData = Status.success(mapper.invoke(data))
    }.await()
    return statusData
}

@ExperimentalCoroutinesApi
suspend fun <T> Task<T>.fetchData(): Status<T> {
    lateinit var statusData: Status<T>
    addOnFailureListener {
        statusData = Status.error(it.message.orEmpty())
    }.addOnSuccessListener { data ->
        statusData = Status.success(data)
    }.await()
    return statusData
}