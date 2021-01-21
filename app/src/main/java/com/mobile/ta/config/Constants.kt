package com.mobile.ta.config

object Constants {

    /**
     * Date Patterns
     */
    const val DD_MMMM_YYYY = "dd MMMM yyyy"
    const val DD_MMMM_YYYY_HH_MM_SS = "dd MMMM yyyy, HH:mm:SS"

    /**
     * Error messages
     */
    const val NAME = "Name"
    const val DATE_OF_BIRTH = "Date of Birth"
    const val CREDENTIALS = "Credentials"
    const val TITLE = "Title"
    const val QUESTION = "Question"
    const val REPLY = "Reply"
    const val FEEDBACK = "Feedback"
    const val ENROLLMENT_KEY = "Enrollment key"

    fun getEmptyErrorMessage(data: String) = "$data must be filled"
}