package com.mobile.ta.config

object Constants {

    /**
     * Date Patterns
     */
    const val DD_MMMM_YYYY = "dd MMMM yyyy"

    /**
     * Error messages
     */
    const val NAME = "Name"
    const val DATE_OF_BIRTH = "Date of Birth"

    fun getEmptyErrorMessage(data: String) = "$data must be filled"
}