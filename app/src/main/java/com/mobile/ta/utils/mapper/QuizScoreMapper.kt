package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.course.chapter.assignment.QuizScore

object QuizScoreMapper {
    const val userId = "userId"
    const val userEmail = "userEmail"
    const val score = "score"
    const val submittedDate = "submittedDate"

    fun mapToQuizScores(querySnapshot: QuerySnapshot): MutableList<QuizScore> {
        return querySnapshot.toObjects(QuizScore::class.java)
    }
}