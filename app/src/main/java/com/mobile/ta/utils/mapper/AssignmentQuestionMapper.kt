package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.QuerySnapshot
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion

object AssignmentQuestionMapper {
    const val ORDER_FIELD = "order"

    fun mapToAssignmentQuestions(querySnapshot: QuerySnapshot): MutableList<AssignmentQuestion> {
        return querySnapshot.toObjects(AssignmentQuestion::class.java)
    }
}