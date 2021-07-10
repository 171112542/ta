package com.mobile.ta.utils.mapper

import com.mobile.ta.model.studentProgress.ShortCourse
import java.util.HashMap

object ShortCourseMapper {
    const val ID_FIELD = "id"
    const val IMAGE_URL_FIELD = "imageUrl"
    const val TITLE_FIELD = "title"

    fun ShortCourse.toHashMap() : HashMap<String, Any?> {
        return hashMapOf(
            ID_FIELD to id,
            IMAGE_URL_FIELD to imageUrl,
            TITLE_FIELD to title
        )
    }
}