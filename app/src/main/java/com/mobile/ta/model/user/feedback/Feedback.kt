package com.mobile.ta.model.user.feedback

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.mobile.ta.utils.serializer.TimestampSerializer
import kotlinx.serialization.Serializable

data class Feedback(

    @DocumentId
    val id: String,

    val feedbackType: String,

    val description: String,

    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp
)