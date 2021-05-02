package com.mobile.ta.viewmodel

import androidx.lifecycle.*
import com.google.firebase.firestore.DocumentSnapshot
import com.mobile.ta.repo.UserRepository
import com.mobile.ta.repo.UserRepository.Companion.CORRECT_ANSWER_COUNT_FIELD
import com.mobile.ta.repo.UserRepository.Companion.TOTAL_ANSWER_COUNT_FIELD
import com.mobile.ta.repository.CourseRepository
import com.mobile.ta.repository.impl.CourseRepositoryImpl.Companion.CHAPTER_NEXT_CHAPTER_ID_FIELD
import com.mobile.ta.repository.impl.CourseRepositoryImpl.Companion.CHAPTER_TYPE_FIELD
import com.mobile.ta.repository.impl.CourseRepositoryImpl.Companion.CHAPTER_TYPE_FIELD_PRACTICE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseSubmitViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val courseId = savedStateHandle.get<String>("courseId") ?: ""
    private val chapterId = savedStateHandle.get<String>("chapterId") ?: ""
    private lateinit var chapter: DocumentSnapshot

    private var _result: MutableLiveData<MutableMap<String, Long>> = MutableLiveData(mutableMapOf())
    val result: LiveData<MutableMap<String, Long>>
        get() = _result
    private var _navigateToNextChapter: MutableLiveData<Boolean> = MutableLiveData(false)
    val navigateToNextChapter: LiveData<Boolean>
        get() = _navigateToNextChapter
    private var _showNextChapterButton: MutableLiveData<Boolean> = MutableLiveData(false)
    val showNextChapterButton: LiveData<Boolean>
        get() = _showNextChapterButton
    private var _showRetryButton: MutableLiveData<Boolean> = MutableLiveData(false)
    val showRetryButton: LiveData<Boolean>
        get() = _showRetryButton
    lateinit var nextChapterType: String
    var nextChapterId: String? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            chapter = courseRepository.getChapterById(chapterId)
            nextChapterId = chapter[CHAPTER_NEXT_CHAPTER_ID_FIELD] as String?
            val submittedChapter =
                userRepository
                    .getSubmittedChapter(courseId, chapterId)

            _result.postValue(
                mutableMapOf(
                    CORRECT_ANSWER_COUNT_FIELD to submittedChapter[CORRECT_ANSWER_COUNT_FIELD] as Long,
                    TOTAL_ANSWER_COUNT_FIELD to submittedChapter[TOTAL_ANSWER_COUNT_FIELD] as Long
                )
            )
            _showRetryButton.postValue(chapter[CHAPTER_TYPE_FIELD] as String == CHAPTER_TYPE_FIELD_PRACTICE)
            if (nextChapterId != null) _showNextChapterButton.postValue(true)
        }
    }

    fun retry() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.resetSubmittedChapter(courseId, chapterId)
        }
    }

    fun navigateToNextChapter() {
        viewModelScope.launch(Dispatchers.IO) {
            val nextChapter = courseRepository.getChapterById(chapterId)
            nextChapterType = nextChapter[CHAPTER_TYPE_FIELD] as String
            _navigateToNextChapter.postValue(true)
        }
    }
}