package com.mobile.ta.model

import java.util.Locale

enum class TypeTag {
    MATH, PHYSICS, BIOLOGY, CHEMISTRY;

    override fun toString(): String {
        return name[0].toString() + name.substring(1).toLowerCase(Locale.ROOT)
    }
}