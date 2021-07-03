package com.mobile.ta.model.studentProgress

data class SubmittedAnswer(
    val question: String,
    val choices: List<String>,
    val correctAnswer: Int,
    val explanation: String,
    val order: Int,
    val selectedAnswer: Int
) {
    constructor() : this(
        "",
        listOf(),
        -1,
        "",
        -1,
        -1
    )
}