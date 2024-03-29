package com.mobile.ta.utils

import com.google.firebase.Timestamp

object TimestampUtil {

    fun getTimeDifferenceString(
        thenTimestamp: Timestamp,
        nowTimestamp: Timestamp = Timestamp.now()
    ): String {
        val difference = nowTimestamp.seconds - thenTimestamp.seconds
        return when {
            difference < 60 -> "< 1 minute ago"
            difference < 3600 -> "${(difference / 60)} minutes ago"
            difference < 86400 -> "${(difference / 3600)} hours ago"
            else -> "${(difference / 86400)} days ago"
        }
    }

    fun getTimeRemainingString(
        futureTimestamp: Timestamp,
        nowTimestamp: Timestamp = Timestamp.now()
    ): String {
        val difference = futureTimestamp.seconds - nowTimestamp.seconds
        return when {
            difference < 60 -> "one more minute"
            difference < 3600 -> "${difference / 60} more minutes"
            else -> "${difference / 3600} more hours"
        }
    }
}