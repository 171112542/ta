package com.mobile.ta.ui.course.chapter.assignment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.ta.R
import com.mobile.ta.databinding.FragCourseSubmitBinding
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.viewmodel.course.chapter.assignment.CourseSubmitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseSubmitFragment :
    BaseFragment<FragCourseSubmitBinding>(FragCourseSubmitBinding::inflate) {
    private val viewmodel by viewModels<CourseSubmitViewModel>()

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
            findNavController().navigate(
                CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                    courseId = viewmodel.courseId,
                    chapterId = viewmodel.chapterId
                )
            )
        }
        binding.fragCourseSubmitNextChapter.setOnClickListener {
            viewmodel.navigateToNextChapter()
        }
        viewmodel.navigateToNextChapter.observe(viewLifecycleOwner) {
            if (!it) return@observe
            val nextChapterType = viewmodel.nextChapterSummary?.type ?: return@observe
            val nextChapterId = viewmodel.nextChapterSummary?.id ?: return@observe
            when (nextChapterType) {
                ChapterType.PRACTICE -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = nextChapterId
                    )
                )
                ChapterType.QUIZ -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = nextChapterId
                    )
                )
                ChapterType.CONTENT -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCourseContentFragment(
                        courseId = viewmodel.courseId,
                        chapterId = nextChapterId
                    )
                )
            }
        }
    }
}