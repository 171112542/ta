package com.mobile.ta.utils.serializer

import android.annotation.SuppressLint
import com.google.firebase.Timestamp
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.SimpleDateFormat

object TimestampSerializer : KSerializer<Timestamp> {
    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ")

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Timestamp", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Timestamp {
        return Timestamp(decoder.decodeString().toLong(), 0)
    }

    override fun serialize(encoder: Encoder, value: Timestamp) {
        encoder.encodeString(dateFormat.format(value.toDate()))
    }
}