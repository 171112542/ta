package com.mobile.ta.di.modules

import com.mobile.ta.teacher.adapter.course.chapter.assignment.CourseQuestionAdapter
import com.mobile.ta.teacher.adapter.course.students.StudentProgressAdapter
import com.mobile.ta.teacher.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.teacher.adapter.diff.StudentProgressDiffCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TeacherAdapterModule {
    @Provides
    @Singleton
    fun provideCourseQuestionDiffCallback(): CourseQuestionDiffCallback =
        CourseQuestionDiffCallback()

    @Provides
    fun provideCourseQuestionAdapter(diffCallback: CourseQuestionDiffCallback)
            : CourseQuestionAdapter = CourseQuestionAdapter(diffCallback)

    @Provides
    fun provideStudentProgressDiffCallback(): StudentProgressDiffCallback =
        StudentProgressDiffCallback()

    @Provides
    fun provideStudentProgressAdapter(diffCallback: StudentProgressDiffCallback)
        : StudentProgressAdapter = StudentProgressAdapter(diffCallback)
}