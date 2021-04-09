package com.mobile.ta.utils

import android.widget.EditText
import android.widget.TextView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.config.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
fun <T> CollectionReference.getCallbackFlowList(
    mapper: (MutableList<DocumentSnapshot>) -> MutableList<T>): Flow<MutableList<T>> {
    return callbackFlow {
        val listener =
            this@getCallbackFlowList.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    cancel(
                        cause = firebaseFirestoreException,
                        message = firebaseFirestoreException.message.orEmpty()
                    )
                    return@addSnapshotListener
                }
                val mappedDataList = mapper.invoke(querySnapshot?.documents ?: mutableListOf())
                offer(mappedDataList)
            }
        awaitClose {
            listener.remove()
        }
    }
}

@ExperimentalCoroutinesApi
fun <T> CollectionReference.getCallbackFlow(mapper: (DocumentSnapshot) -> T): Flow<T> {
    return callbackFlow {
        val listener =
            this@getCallbackFlow.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                firebaseFirestoreException?.let {
                    cancel(
                        cause = firebaseFirestoreException,
                        message = firebaseFirestoreException.message.orEmpty()
                    )
                    return@addSnapshotListener
                }
                querySnapshot?.documents?.first()?.let { data ->
                    offer(mapper.invoke(data))
                }
            }
        awaitClose {
            listener.remove()
        }
    }
}