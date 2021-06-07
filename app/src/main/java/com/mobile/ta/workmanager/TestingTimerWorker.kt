package com.mobile.ta.workmanager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mobile.ta.model.testing.TimeSpent
import com.mobile.ta.model.testing.deserializeFromJson
import com.mobile.ta.repository.TestingTimerRepository
import com.mobile.ta.utils.wrapper.status.StatusType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class TestingTimerWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val testingTimerRepository: TestingTimerRepository
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val uid = inputData.getString(UID_KEY) as String
        val courseId = inputData.getString(COURSE_ID_KEY) as String
        val chapterId = inputData.getString(CHAPTER_ID_KEY) as String
        val timeSpentJson = inputData.keyValueMap[TIME_SPENT_JSON_KEY] as String
        val timeSpent = TimeSpent().deserializeFromJson(timeSpentJson)
        val status = testingTimerRepository.saveTimeSpent(
            uid,
            courseId,
            chapterId,
            timeSpent
        )

        return@withContext if (status.status == StatusType.SUCCESS) {
            Result.success()
        } else {
            Result.retry()
        }
    }

    companion object {
        const val UID_KEY = "uid"
        const val COURSE_ID_KEY = "courseId"
        const val CHAPTER_ID_KEY = "chapterId"
        const val TIME_SPENT_JSON_KEY = "timeSpentJson"
    }
}