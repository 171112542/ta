package com.mobile.ta.student.viewmodel.course.chapter.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.repository.AuthRepository
import com.mobile.ta.repository.StudentProgressRepository
import com.mobile.ta.student.view.course.CourseTabFragment.CourseTabType
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.wrapper.status.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseTabViewModel @Inject constructor(
    private val studentProgressRepository: StudentProgressRepository,
    authRepository: AuthRepository
) : BaseViewModel() {
    private val _studentProgress = MutableLiveData<Status<MutableList<StudentProgress>>>()
    val studentProgress: LiveData<Status<MutableList<StudentProgress>>> get() = _studentProgress
    private val loggedInUid: String? = authRepository.getUser()?.uid

    fun getUserCourse(type: CourseTabType) {
        launchViewModelScope {
            val isFinished = type == CourseTabType.FINISHED_TAB
            loggedInUid?.let {
                _studentProgress.postValue(
                    studentProgressRepository.getStudentProgressByFinished(
                        it,
                        isFinished
                    )
                )
            }
        }
    }
}