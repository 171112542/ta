package com.mobile.ta.model

data class CourseQuestion(
    val id: Int,
    val question: String,
    val choices: List<String>,
    val correctAnswer: Int,
    val explanation: String
)