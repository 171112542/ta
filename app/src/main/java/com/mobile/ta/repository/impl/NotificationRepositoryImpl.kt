package com.mobile.ta.repository.impl

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mobile.ta.config.CollectionConstants
import com.mobile.ta.model.notification.Notification
import com.mobile.ta.repository.NotificationRepository
import com.mobile.ta.utils.fetchData
import com.mobile.ta.utils.mapper.NotificationMapper
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.utils.wrapper.status.StatusType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
class NotificationRepositoryImpl @Inject constructor(
    val database: FirebaseFirestore
): NotificationRepository {
    private val userCollection by lazy {
        database.collection(CollectionConstants.USER_COLLECTION)
    }

    override suspend fun sendNotificationToken(userId: String, token: String): Status<Boolean> {
        return userCollection.document(userId)
            .update(mapOf(
                NotificationMapper.USER_NOTIFICATION_TOKEN_FIELD to token
            ))
            .fetchData()
    }

    override suspend fun deleteNotificationToken(userId: String): Status<Boolean> {
        return userCollection.document(userId)
            .update(mapOf(
                NotificationMapper.USER_NOTIFICATION_TOKEN_FIELD to FieldValue.delete()
            ))
            .fetchData()
    }

    override suspend fun getNotificationList(userId: String): Status<MutableList<Notification>> {
        return userCollection.document(userId)
            .collection(CollectionConstants.NOTIFICATION_COLLECTION)
            .orderBy(NotificationMapper.NOTIFICATION_NOTIFIED_AT_FIELD, Query.Direction.DESCENDING)
            .fetchData(NotificationMapper::mapToNotifications)
    }

    override suspend fun markAllNotificationsAsRead(userId: String): Status<Boolean> {
        val unreadNotificationList = userCollection.document(userId)
            .collection(CollectionConstants.NOTIFICATION_COLLECTION)
            .whereEqualTo(NotificationMapper.NOTIFICATION_HAS_READ_FIELD, false)
            .fetchData(NotificationMapper::mapToNotifications)
        if (unreadNotificationList.status == StatusType.SUCCESS) {
            return database.runBatch { batch ->
                unreadNotificationList.data?.forEach {
                    val documentReference = userCollection.document(userId)
                        .collection(CollectionConstants.NOTIFICATION_COLLECTION)
                        .document(it.id)
                    batch.update(
                        documentReference,
                        NotificationMapper.NOTIFICATION_HAS_READ_FIELD,
                        true
                    )
                }
            }.fetchData()
        }
        else {
            return Status.error("Cannot get notification list to update")
        }
    }
}