package com.mobile.ta.data

import com.mobile.ta.model.User
import java.text.SimpleDateFormat

object UserData {
    val dobDateFormat = SimpleDateFormat("yyyy-MM-dd")
    val labelDobDateFormat = SimpleDateFormat("MMMM dd, yyyy")
    private var _user = User(
        "John",
        true,
        "john@gmail.com",
        "082112345678",
        null,
        dobDateFormat.parse("2001-02-01"),
        "Something about me? nothing special"
    )

    fun getUserData(): User {
        return _user
    }

    fun setUserData(user: User) {
        _user = user
    }
}