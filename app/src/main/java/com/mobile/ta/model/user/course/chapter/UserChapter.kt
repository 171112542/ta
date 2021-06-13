package com.mobile.ta.model.user.course.chapter

import com.google.firebase.firestore.DocumentId

data class UserChapter(
    @DocumentId
    val id: String,
    val title: String,
    val finished: Boolean
) {
    constructor() :
        this(
            "",
            "",
            false
        )
}
