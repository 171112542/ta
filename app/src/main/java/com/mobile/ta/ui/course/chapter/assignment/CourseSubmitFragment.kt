package com.mobile.ta.ui.course.chapter.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.ta.R
import com.mobile.ta.databinding.FragCourseSubmitBinding
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.viewmodel.course.chapter.assignment.CourseSubmitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseSubmitFragment : Fragment() {
    private var _binding: FragCourseSubmitBinding? = null
    private val binding get() = _binding as FragCourseSubmitBinding
    private val viewmodel by viewModels<CourseSubmitViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragCourseSubmitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPage()
    }

    private fun setupPage() {
        viewmodel.submittedAssignment.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitLoading.visibility = View.GONE
            binding.fragCourseSubmitContent.visibility = View.VISIBLE
            val totalAnswerCount = it.totalAnswerCount
            val correctAnswerCount = it.correctAnswerCount
            binding.fragCourseSubmitTitle.text = viewmodel.chapterTitle
            binding.fragCourseSubmitScore.text =
                getString(R.string.assignment_result_text, correctAnswerCount, totalAnswerCount)
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
            when (viewmodel.nextChapterType) {
                ChapterType.PRACTICE -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.nextChapterId ?: ""
                    )
                )
                ChapterType.QUIZ -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.nextChapterId ?: ""
                    )
                )
                ChapterType.CONTENT -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCourseContentFragment()
                )
            }
        }
    }
}