package com.mobile.ta.model.course.information

import java.util.*

enum class LevelTag {
    JUNIOR_ONE, JUNIOR_TWO, JUNIOR_THREE, SENIOR_ONE, SENIOR_TWO, SENIOR_THREE;

    override fun toString(): String {
        var result = name.replace("_", "-")
        result = result.replace("ONE", "1")
        result = result.replace("TWO", "2")
        result = result.replace("THREE", "3")
        result = result.split("-").joinToString(" - ")
        return result[0].toString() + result.substring(1).toLowerCase(Locale.ROOT)
    }
}