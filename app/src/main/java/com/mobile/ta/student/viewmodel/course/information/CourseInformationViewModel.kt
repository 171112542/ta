package com.mobile.ta.student.viewmodel.course.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.studentProgress.ShortCourse
import com.mobile.ta.model.studentProgress.Student
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.model.user.User
import com.mobile.ta.repository.*
import com.mobile.ta.ui.viewmodel.base.BaseViewModel
import com.mobile.ta.utils.isNull
import com.mobile.ta.utils.wrapper.status.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseInformationViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val studentProgressRepository: StudentProgressRepository,
    private val userRepository: UserRepository,
    authRepository: AuthRepository
) : BaseViewModel() {
    private val _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>>
        get() = _course
    private val _studentProgress = MutableLiveData<Status<StudentProgress>>()
    val studentProgress: LiveData<Status<StudentProgress>> get() = _studentProgress
    private val _enrollCourse = MutableLiveData<Status<Boolean>>()
    val enrollCourse: LiveData<Status<Boolean>> get() = _enrollCourse
    private val _relatedCourses = MutableLiveData<MutableList<Course>>()
    val relatedCourses: LiveData<MutableList<Course>> get() = _relatedCourses
    private val _creator = MutableLiveData<User>()
    val creator: LiveData<User> get() = _creator
    private val _prerequisiteCourses = MutableLiveData<MutableList<Course>>()
    val prerequisiteCourses: LiveData<MutableList<Course>> get() = _prerequisiteCourses
    private val loggedInUid = authRepository.getUser()?.uid

    fun getCourse(courseId: String) {
        launchViewModelScope {
            getStudentProgress(courseId)
            val result = courseRepository.getCourseById(courseId)
            checkStatus(result, {
                getPreRequisiteCourses(it.prerequisiteCourse)
                getRelatedCourses(it.relatedCourse)
                getCreator(it.creatorId)
            })
            _course.postValue(result)
        }
    }

    fun getStudentProgress(courseId: String) {
        loggedInUid?.let { uid ->
            launchViewModelScope {
                val studentProgress =
                    studentProgressRepository.getStudentProgress(uid, courseId)
                _studentProgress.postValue(studentProgress)
            }
        }
    }

    private fun getCreator(creator: String) {
        launchViewModelScope {
            val user = userRepository.getUserById(creator)
            checkStatus(user, {
                _creator.postValue(it)
            })
        }
    }

    fun enrollCourse(courseId: String, enrollmentKey: String) {
        launchViewModelScope {
            course.value?.data?.let { course ->
                loggedInUid?.let { uid ->
                    if (course.enrollmentKey == enrollmentKey) {
                        val user = userRepository.getUserById(uid)
                        if (user.data.isNull()) return@launchViewModelScope
                        val studentProgress = StudentProgress(
                            course = ShortCourse(
                                course.id,
                                course.imageUrl,
                                course.title,
                                course.description
                            ),
                            lastAccessedChapter = course.chapterSummaryList.first(),
                            student = Student(
                                uid,
                                user.data?.name.orEmpty(),
                                user.data?.email.orEmpty()
                            ),
                            totalChapterCount = course.chapterSummaryList.size
                        )
                        _enrollCourse.postValue(
                            studentProgressRepository.addStudentProgress(
                                uid,
                                courseId,
                                studentProgress
                            )
                        )
                    } else {
                        _enrollCourse.postValue(Status.error("Wrong enrollment key."))
                    }
                }
            }
        }
    }

    fun incrementTotalEnrolled() {
        course.value?.data?.let {
            launchViewModelScope {
                val totalEnrolled = it.totalEnrolled + 1
                courseRepository.updateTotalEnrolledCourse(it.id, totalEnrolled)
            }
        }
    }

    private fun getPreRequisiteCourses(prerequisiteCourses: List<String>) {
        launchViewModelScope {
            val courses = mutableListOf<Course>()
            prerequisiteCourses.forEach {
                val course = courseRepository.getCourseById(it)
                checkStatus(course, { result ->
                    courses.add(result)
                })
            }
            _prerequisiteCourses.postValue(courses)
        }
    }

    private fun getRelatedCourses(relatedCourses: List<String>) {
        launchViewModelScope {
            val courses = mutableListOf<Course>()
            relatedCourses.forEach {
                val course = courseRepository.getCourseById(it)
                checkStatus(course, { result ->
                    courses.add(result)
                })
            }
            _relatedCourses.postValue(courses)
        }
    }
}