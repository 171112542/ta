package com.mobile.ta.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.model.status.Status
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await

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

suspend fun CollectionReference.fetchDataSize(): Int {
    return get().await().size()
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
suspend fun <T> Task<T>.fetchData(): Status<Boolean> {
    return try {
        await()
        Status.success(true)
    } catch (ex: Exception) {
        Status.error(ex.message, false)
    }
}

@ExperimentalCoroutinesApi
suspend fun <T> Task<T>.fetchDataWithResult(): Status<T> {
    return try {
        val data = await()
        Status.success(data)
    } catch (ex: Exception) {
        Status.error(ex.message)
    }
}