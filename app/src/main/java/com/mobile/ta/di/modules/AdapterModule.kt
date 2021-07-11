package com.mobile.ta.di.modules

import com.mobile.ta.student.adapter.course.chapter.assignment.CourseQuestionAdapter
import com.mobile.ta.student.adapter.diff.CourseQuestionDiffCallback
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AdapterModule {
    @Provides
    @Singleton
    fun provideCourseQuestionDiffCallback(): CourseQuestionDiffCallback =
        CourseQuestionDiffCallback()

    @Provides
    fun provideCourseQuestionAdapter(diffCallback: CourseQuestionDiffCallback)
        : CourseQuestionAdapter = CourseQuestionAdapter(diffCallback)
}