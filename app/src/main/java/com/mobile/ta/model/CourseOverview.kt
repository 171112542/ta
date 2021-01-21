package com.mobile.ta.model

data class CourseOverview(
    val id: Int,
    val title: String,
    val description: String,
    val level: LevelTag,
    val type: TypeTag,
    val imageUrl: Int
)