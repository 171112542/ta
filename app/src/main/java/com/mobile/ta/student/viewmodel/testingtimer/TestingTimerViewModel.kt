package com.mobile.ta.student.viewmodel.testingtimer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.firebase.Timestamp
import com.mobile.ta.model.testing.TimeSpent
import com.mobile.ta.model.testing.TimeSpent.FragmentType
import com.mobile.ta.model.testing.serializeToJson
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.workmanager.TestingTimerWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class TestingTimerViewModel @Inject constructor(
    authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    private val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    private val courseId = savedStateHandle.get<String>("courseId") ?: ""
    private var loggedInUid: String = authRepository.getUser()?.uid ?: ""
    private lateinit var startTime: Timestamp
    private lateinit var endTime: Timestamp
    @FragmentType
    private lateinit var type: String

    fun saveType(@FragmentType type: String) {
        this.type = type
    }

    fun saveStartTime(startTime: Timestamp) {
        this.startTime = startTime
    }

    fun saveEndTime(endTime: Timestamp) {
        this.endTime = endTime
    }

    fun startSaveTimeSpentWork() {
        val timeSpent = TimeSpent(
            "",
            this.startTime,
            this.endTime,
            this.type
        )
        val saveTimeSpentWorkRequest =
            OneTimeWorkRequestBuilder<TestingTimerWorker>()
                .setInputData(workDataOf(
                    TestingTimerWorker.UID_KEY to loggedInUid,
                    TestingTimerWorker.COURSE_ID_KEY to courseId,
                    TestingTimerWorker.CHAPTER_ID_KEY to chapterId,
                    TestingTimerWorker.TIME_SPENT_JSON_KEY to timeSpent.serializeToJson()
                ))
                .build()
        WorkManager
            .getInstance(getApplication())
            .enqueue(saveTimeSpentWorkRequest)
    }
}