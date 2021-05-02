package com.mobile.ta.di.modules

import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.repository.DiscussionRepository
import com.mobile.ta.repository.impl.AuthRepositoryImpl
import com.mobile.ta.repository.impl.CourseRepositoryImpl
import com.mobile.ta.repository.impl.DiscussionRepositoryImpl
import dagger.Binds
import dagger.Module
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
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindCourseRepository(
        courseRepositoryImpl: CourseRepositoryImpl
    ): CourseRepository
}