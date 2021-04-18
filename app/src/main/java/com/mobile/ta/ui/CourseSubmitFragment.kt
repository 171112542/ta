package com.mobile.ta.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.ta.databinding.FragCourseSubmitBinding
import com.mobile.ta.repo.CourseRepository.Companion.CHAPTER_TYPE_FIELD_CONTENT
import com.mobile.ta.repo.CourseRepository.Companion.CHAPTER_TYPE_FIELD_PRACTICE
import com.mobile.ta.repo.CourseRepository.Companion.CHAPTER_TYPE_FIELD_QUIZ
import com.mobile.ta.repo.UserRepository
import com.mobile.ta.repo.UserRepository.Companion.CORRECT_ANSWER_COUNT_FIELD
import com.mobile.ta.repo.UserRepository.Companion.TOTAL_ANSWER_COUNT_FIELD
import com.mobile.ta.viewmodel.CoursePracticeViewModel
import com.mobile.ta.viewmodel.CourseSubmitViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseSubmitFragment : Fragment() {
    private var _binding: FragCourseSubmitBinding? = null
    private val binding get() = _binding as FragCourseSubmitBinding
    private val viewmodel by viewModels<CourseSubmitViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragCourseSubmitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPage()
    }

    private fun setupPage() {
        viewmodel.result.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.fragCourseSubmitLoading.visibility = View.GONE
                binding.fragCourseSubmitContent.visibility = View.VISIBLE
                val totalAnswerCount = it[TOTAL_ANSWER_COUNT_FIELD] as Long
                val correctAnswerCount = it[CORRECT_ANSWER_COUNT_FIELD] as Long
                binding.fragCourseSubmitScore.text = "$correctAnswerCount/$totalAnswerCount"
            }
        }
        viewmodel.showRetryButton.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitRetry.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewmodel.showNextChapterButton.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitNextChapter.visibility = if (it) View.VISIBLE else View.GONE
        }
        binding.fragCourseSubmitRetry.setOnClickListener {
            viewmodel.retry()
            findNavController().navigate(CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment())
        }
        binding.fragCourseSubmitNextChapter.setOnClickListener {
            viewmodel.navigateToNextChapter()
        }
        viewmodel.navigateToNextChapter.observe(viewLifecycleOwner) {
            if (!it) return@observe
            when(viewmodel.nextChapterType) {
                CHAPTER_TYPE_FIELD_PRACTICE -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.nextChapterId ?: ""
                    )
                )
                CHAPTER_TYPE_FIELD_QUIZ -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.nextChapterId ?: ""
                    )
                )
                CHAPTER_TYPE_FIELD_CONTENT -> findNavController().navigate(CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCourseContentFragment())
            }
        }
    }
}