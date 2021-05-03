package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.config.CollectionConstants.CHAPTER_COLLECTION
import com.mobile.ta.config.CollectionConstants.COURSE_COLLECTION
import com.mobile.ta.config.CollectionConstants.QUESTION_COLLECTION
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.repository.ChapterRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.AssignmentQuestionMapper
import com.mobile.ta.utils.mapper.ChapterMapper
import com.mobile.ta.utils.mapper.CourseMapper
import com.mobile.ta.utils.wrapper.status.Status
import javax.inject.Inject

class ChapterRepositoryImpl @Inject constructor(database: FirebaseFirestore) : ChapterRepository {
    private val courseCollection = database.collection(COURSE_COLLECTION)

    override suspend fun getChapters(courseId: String): Status<MutableList<Chapter>> {
        return courseCollection.document(courseId).collection(CHAPTER_COLLECTION)
            .fetchData(ChapterMapper::mapToChapters)
    }

    override suspend fun getNthChapter(index: Int): Status<MutableList<Course>> {
        return courseCollection.whereEqualTo(ChapterMapper.ORDER_FIELD, index)
            .fetchData(CourseMapper::mapToCourses)
    }

    override suspend fun getChapterById(courseId: String, chapterId: String): Status<Chapter> {
        return courseCollection.document(courseId)
            .collection(CHAPTER_COLLECTION).document(chapterId)
            .fetchData(ChapterMapper::mapToChapter)
    }

    override suspend fun getQuestions(
        courseId: String,
        chapterId: String
    ): Status<MutableList<AssignmentQuestion>> {
        return courseCollection.document(courseId)
            .collection(CHAPTER_COLLECTION).document(chapterId)
            .collection(QUESTION_COLLECTION)
            .orderBy(AssignmentQuestionMapper.ORDER_FIELD)
            .fetchData(AssignmentQuestionMapper::mapToAssignmentQuestions)
    }
}
