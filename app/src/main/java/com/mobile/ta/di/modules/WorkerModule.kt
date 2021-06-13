package com.mobile.ta.di.modules

import com.mobile.ta.repository.TestingTimerRepository
import com.mobile.ta.repository.impl.TestingTimerRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
class WorkerModule {
    @Provides
    fun provideTimerRepository(timerRepositoryImpl: TestingTimerRepositoryImpl)
            : TestingTimerRepository = timerRepositoryImpl
}