package com.mobile.ta.model.user.course.chapter

import com.google.firebase.firestore.DocumentId
import java.sql.Timestamp

data class UserChapter(
    @DocumentId
    val id: String,
    val title: String
) {
    constructor() :
        this(
            "",
            ""
        )
}
