package com.mobile.ta.utils.mapper

import com.mobile.ta.model.studentProgress.Student

object StudentMapper {
    const val ID_FIELD = "id"
    const val NAME_FIELD = "name"
    const val EMAIL_FIELD = "email"

    fun Student.toHashMap() : HashMap<String, Any> {
        return hashMapOf(
            ID_FIELD to id,
            NAME_FIELD to name,
            EMAIL_FIELD to email
        )
    }
}