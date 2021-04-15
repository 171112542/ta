package com.mobile.ta.di.modules

import com.mobile.ta.repository.DiscussionRepository
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

}