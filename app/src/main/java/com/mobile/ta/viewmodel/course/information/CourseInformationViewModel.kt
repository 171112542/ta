package com.mobile.ta.viewmodel.course.information

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.ta.model.course.Course
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.user.User
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.model.user.course.chapter.UserChapter
import com.mobile.ta.repository.*
import com.mobile.ta.utils.mapper.UserCourseMapper.toHashMap
import com.mobile.ta.utils.wrapper.status.Status
import com.mobile.ta.viewmodel.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CourseInformationViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val chapterRepository: ChapterRepository,
    private val userChapterRepository: UserChapterRepository,
    private val userCourseRepository: UserCourseRepository,
    private val userRepository: UserRepository,
    authRepository: AuthRepository
) : BaseViewModel() {
    private val _course = MutableLiveData<Status<Course>>()
    val course: LiveData<Status<Course>>
        get() = _course
    private val _chapters = MutableLiveData<Status<MutableList<Chapter>>>()
    val chapters: LiveData<Status<MutableList<Chapter>>> get() = _chapters
    private val _userChapters = MutableLiveData<Status<MutableList<UserChapter>>>()
    val userChapters: LiveData<Status<MutableList<UserChapter>>> get() = _userChapters
    private val _userCourse = MutableLiveData<Status<UserCourse>>()
    val userCourse: LiveData<Status<UserCourse>> get() = _userCourse
    private val _enrollCourse = MutableLiveData<Status<Boolean>>()
    val enrollCourse: LiveData<Status<Boolean>> get() = _enrollCourse
    private val _relatedCourses = MutableLiveData<MutableList<Course>>()
    val relatedCourses: LiveData<MutableList<Course>> get() = _relatedCourses
    private val _creator = MutableLiveData<User>()
    val creator: LiveData<User> get() = _creator
    private val _preRequisiteCourses = MutableLiveData<MutableList<Course>>()
    val preRequisiteCourses: LiveData<MutableList<Course>> get() = _preRequisiteCourses
    private val loggedInUid = authRepository.getUser()?.uid

    fun getCourse(courseId: String) {
        launchViewModelScope {
            loggedInUid?.let { uid ->
                val userCourseResult =
                    userCourseRepository.getUserCourse(uid, courseId)
                _userCourse.postValue(userCourseResult)
            }
            val result = courseRepository.getCourseById(courseId)
            checkStatus(result, {
                getPreRequisiteCourses(it.preRequisiteCourse)
                getRelatedCourses(it.relatedCourse)
                getCreator(it.creatorId)
            })
            _course.postValue(result)
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

    fun getChapters(courseId: String) {
        launchViewModelScope {
            getUserChapters(courseId)
            val result = chapterRepository.getChapters(courseId)
            _chapters.postValue(result)
        }
    }

    private suspend fun getUserChapters(courseId: String) {
        loggedInUid?.let { uid ->
            val userChaptersResult =
                userChapterRepository.getFinishedUserChapters(uid, courseId)
            _userChapters.postValue(userChaptersResult)
        }
    }

    fun enrollCourse(courseId: String, enrollmentKey: String) {
        launchViewModelScope {
            course.value?.data?.let { course ->
                loggedInUid?.let { uid ->
                    if (course.enrollmentKey == enrollmentKey) {
                        val userCourse = UserCourse(
                            courseId,
                            course.title,
                            course.description,
                            course.imageUrl,
                            true,
                            false
                        )
                        _enrollCourse.postValue(
                            userCourseRepository.addUserCourse(
                                uid,
                                courseId,
                                userCourse.toHashMap(course.chapterSummaryList.first())
                            )
                        )
                    } else {
                        _enrollCourse.postValue(Status.error("Wrong enrollment key."))
                    }
                }
            }
        }
    }

    fun incrementTotalEnrolled(course: Course) {
        launchViewModelScope {
            course.totalEnrolled += 1
            courseRepository.updateTotalEnrolledCourse(course)
        }
    }

    private fun getPreRequisiteCourses(preRequisiteCourses: List<String>) {
        launchViewModelScope {
            val courses = mutableListOf<Course>()
            preRequisiteCourses.forEach {
                val course = courseRepository.getCourseById(it)
                checkStatus(course, { result ->
                    courses.add(result)
                })
            }
            _preRequisiteCourses.postValue(courses)
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