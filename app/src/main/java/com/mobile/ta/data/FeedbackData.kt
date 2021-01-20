package com.mobile.ta.data

import com.mobile.ta.model.Feedback
import java.text.SimpleDateFormat

object FeedbackData {
    val createdFeedbackDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val labelFeedbackDateFormat = SimpleDateFormat("MMMM dd, yyyy h:ma")
    private var _feedbacks = mutableListOf(
        Feedback(
            "SUGGSTN - 02000012",
            "Others",
            "Something wrong in the logout button",
            createdFeedbackDateFormat.parse("2020-01-10")
        ),
        Feedback(
            "SUGGSTN - 02000013",
            "Others",
            "Something wrong in the logout button",
            createdFeedbackDateFormat.parse("2020-01-10")
        ),
    )
    val feedbacks: List<Feedback>
        get() = _feedbacks
}