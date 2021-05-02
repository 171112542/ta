package com.mobile.ta.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.model.status.Status
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.AssignmentQuestionMapper
import com.mobile.ta.utils.mapper.ChapterMapper
import com.mobile.ta.utils.mapper.CourseMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class ChapterRepository @Inject constructor(
    db: FirebaseFirestore
) {
    private val courses = db.collection(CollectionConstants.COURSE_COLLECTION)

    suspend fun getNthChapter(index: Int): Status<MutableList<Course>> {
        return courses.whereEqualTo(ChapterMapper.ORDER_FIELD, index)
            .fetchData(CourseMapper::mapToCourses)
    }

    suspend fun getChapterById(courseId: String, chapterId: String): Status<Chapter> {
        return courses.document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .fetchData(ChapterMapper::mapToChapter)
    }

    suspend fun getQuestions(courseId: String, chapterId: String): Status<MutableList<AssignmentQuestion>> {
        return courses.document(courseId)
            .collection(CollectionConstants.CHAPTER_COLLECTION).document(chapterId)
            .collection(CollectionConstants.QUESTION_COLLECTION)
            .orderBy(AssignmentQuestionMapper.ORDER_FIELD)
            .fetchData(AssignmentQuestionMapper::mapToAssignmentQuestions)
    }
}