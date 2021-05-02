package com.mobile.ta.model.course.chapter.info

data class CourseInformation(
    var id: String,
    var name: String,
    var description: String,
    var tags: MutableMap<String, Tag> = mutableMapOf(),
    var photo: String,
    var chapter: MutableMap<String, Chapter>,
    var creatorId: String,
    var creatorName: String
)