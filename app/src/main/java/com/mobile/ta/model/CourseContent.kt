package com.mobile.ta.model

interface Content {
    val content: String
}

data class ContentText(
    val id: String?,
    val type: Int,
    override val content: String
) : Content {
    companion object {
        val TYPE_SUBHEADING = 1
        val TYPE_SUBSUBHEADING = 2
        val TYPE_TEXT = 3
    }
}

data class ContentImage(
    val type: Int,
    override val content: String,
    val caption: String
) : Content {
    companion object {
        val TYPE_TWO_D = 0
        val TYPE_THREE_D = 1
    }
}