package com.mobile.ta.di.modules

import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.repository.*
import com.mobile.ta.repository.impl.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {
    @ExperimentalCoroutinesApi
    @Binds
    abstract fun bindDiscussionRepository(
        discussionRepositoryImpl: DiscussionRepositoryImpl
    ): DiscussionRepository

    @ExperimentalCoroutinesApi
    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @ExperimentalCoroutinesApi
    @Binds
    abstract fun bindCourseRepository(
        courseRepositoryImpl: CourseRepositoryImpl
    ): CourseRepository

    @ExperimentalCoroutinesApi
    @Binds
    abstract fun bindChapterRepository(
        chapterRepositoryImpl: ChapterRepositoryImpl
    ): ChapterRepository

    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}