package com.mobile.ta.data

import com.mobile.ta.model.courseInfo.Chapter
import com.mobile.ta.model.courseInfo.CourseInformation
import com.mobile.ta.model.courseInfo.Creator
import com.mobile.ta.model.courseInfo.Tag

object CourseInformationData {

    private val tags = mutableMapOf(
        getTag("ONE", "SENIOR-1"),
        getTag("TWO", "Physics"),
        getTag("THREE", "Gravity")
    )
    private val chapters = mutableMapOf(
        getChapter(
            "ONE",
            "About Black Hole",
            "This chapter contains about Black Hole overview and history",
            1,
            20
        ),
        getChapter(
            "TWO",
            "Black Hole Types",
            "This chapter contains about some types of Black Hole",
            2,
            0
        )
    )

    val data = CourseInformation(
        id = "COURSE_ID",
        name = "Theory of Black Hole",
        description = "Black holes are some of the strangest and most fascinating objects in outer space. They're extremely dense, with such strong gravitational attraction that even light cannot escape their grasp if it comes near enough. \n" +
            "\n" +
            "Albert Einstein first predicted the existence of black holes in 1916, with his general theory of relativity. The term \"black hole\" was coined many years later in 1967 by American astronomer John Wheeler. After decades of black holes being known only as theoretical objects, the first physical black hole ever discovered was spotted in 1971.",
        tags = tags,
        photo = "https://images.newscientist.com/wp-content/uploads/2019/04/08111018/screenshot-2019-04-08-10.24.34.jpg",
        chapter = chapters,
        creatorId = "CREATOR_ID",
        creatorName = "Mariah Elizabeth"
    )
    val creator = Creator(
        id = "CREATOR_ID",
        name = "Mariah Elizabeth",
        photo = "https://smartbiography.com/wp-content/uploads/2020/01/Moriah-Elizabeth.jpg",
        description = "Mariah Elizabeth is a experienced content creator and enthusiast in Black Hole theory",
        email = "mariah.elizabeth@gmail.com",
        courseCreated = 5
    )

    private fun getChapter(
        id: String, name: String, content: String, order: Int, progress: Int
    ): Pair<String, Chapter> {
        return Pair(id, Chapter(id, name, content, order, progress.toFloat()))
    }

    private fun getTag(id: String, name: String): Pair<String, Tag> {
        return Pair(id, Tag(id, name))
    }
}