package com.mobile.ta.di.modules

import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.repository.impl.NotificationRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Module
@InstallIn(ServiceComponent::class)
class FCMModule {
    @Provides
    fun provideNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl)
        : NotificationRepository = notificationRepositoryImpl
}