package com.mobile.ta.utils.mapper

import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.model.course.chapter.assignment.Assignment

object AssignmentMapper {
    fun mapToAssignment(documentSnapshot: DocumentSnapshot): Assignment {
        return documentSnapshot.toObject(Assignment::class.java) ?: Assignment()
    }
}