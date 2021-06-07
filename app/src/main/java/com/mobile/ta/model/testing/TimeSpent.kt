package com.mobile.ta.model.testing

import androidx.annotation.StringDef
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.gson.Gson

data class TimeSpent(
    @DocumentId
    val id: String,
    val startTime: Timestamp,
    val endTime: Timestamp,
    @FragmentType
    val contentType: String
) {
    companion object {
        const val THREE_D = "3d"
        const val CONTENT = "content"
    }

    constructor(): this("", Timestamp.now(), Timestamp.now(), CONTENT)

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(THREE_D, CONTENT)
    annotation class FragmentType
}

fun TimeSpent.serializeToJson(): String {
    val gson = Gson()
    return gson.toJson(this)
}

fun TimeSpent.deserializeFromJson(jsonString: String): TimeSpent {
    val gson = Gson()
    return gson.fromJson(jsonString, TimeSpent::class.java)
}