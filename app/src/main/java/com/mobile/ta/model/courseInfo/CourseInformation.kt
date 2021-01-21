package com.mobile.ta.model.courseInfo

data class CourseInformation(
    var id: Int,
    var name: String,
    var description: String,
    var tags: MutableMap<String, Tag> = mutableMapOf(),
    var photo: String,
    var chapter: MutableMap<String, Chapter>,
    var creatorId: String
)