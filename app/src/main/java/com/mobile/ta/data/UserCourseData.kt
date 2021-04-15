package com.mobile.ta.data

import com.mobile.ta.model.UserCourse

object UserCourseData {
    private var _userOngoingCourse = mutableListOf(
        UserCourse(
            "x5s1d",
            "Theory of Black Hole",
            "A black hole is a region of spacetime where gravity is so strong that nothing",
            "",
            5
        )
    )
    private var _userFinishedCouse = mutableListOf<UserCourse>()
    val userOngoingCourse = _userOngoingCourse
    val userFinishedCourse = _userFinishedCouse
}