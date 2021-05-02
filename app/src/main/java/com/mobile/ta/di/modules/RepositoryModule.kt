package com.mobile.ta.di.modules

import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.repository.UserRepository
import com.mobile.ta.repository.impl.CourseRepositoryImpl
import com.mobile.ta.repository.impl.DiscussionRepositoryImpl
import com.mobile.ta.repository.impl.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
}