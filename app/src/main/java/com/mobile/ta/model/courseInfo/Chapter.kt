package com.mobile.ta.model.courseInfo

data class Chapter(
    var id: String,
    var name: String,
    var content: String,
    var order: Int,
    var progress: Float
)
