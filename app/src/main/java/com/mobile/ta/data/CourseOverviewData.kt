package com.mobile.ta.data

import com.mobile.ta.model.CourseOverview
import com.mobile.ta.model.LevelTag

object CourseOverviewData {
    val data = arrayOf(
        CourseOverview(
            id = 1,
            title = "Title 1",
            description = "Description 1",
            level = LevelTag.JUNIOR_ONE,
            imageUrl = "https://imgur.com"
        ),
        CourseOverview(
            id = 2,
            title = "Title 2",
            description = "Description 2",
            level = LevelTag.JUNIOR_THREE,
            imageUrl = "https://imgur.com"
        ),
        CourseOverview(
            id = 3,
            title = "Title 3",
            description = "Description 3",
            level = LevelTag.SENIOR_TWO,
            imageUrl = "https://imgur.com"
        ),
        CourseOverview(
            id = 4,
            title = "Title 4",
            description = "Description 4",
            level = LevelTag.SENIOR_THREE,
            imageUrl = "https://imgur.com"
        )
    )
}