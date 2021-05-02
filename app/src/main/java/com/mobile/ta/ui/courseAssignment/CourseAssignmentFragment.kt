package com.mobile.ta.ui.courseAssignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.adapter.CourseQuestionVHListener
import com.mobile.ta.adapter.CourseQuestionAdapter
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.databinding.FragCourseAssignmentBinding
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.viewmodel.courseAssignment.CourseAssignmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseAssignmentFragment
    : Fragment(), CourseQuestionVHListener {
    private var _binding: FragCourseAssignmentBinding? = null
    private val binding get() = _binding as FragCourseAssignmentBinding
    private val viewmodel by viewModels<CourseAssignmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragCourseAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        val adapter = CourseQuestionAdapter(CourseQuestionDiffCallback(), this)
        binding.fragCourseAssignmentVp.adapter = adapter
        viewmodel.questions.observe(viewLifecycleOwner) {
            binding.fragCourseAssignmentTitle.text = viewmodel.chapterTitle
            adapter.submitList(it)
            binding.fragCourseAssignmentContent.visibility = View.VISIBLE
            binding.fragCourseAssignmentLoading.visibility = View.GONE
        }
        viewmodel.allQuestionsAnswered.observe(viewLifecycleOwner) {
            if (it) {
                adapter.enableSubmitResult()
            }
        }
        viewmodel.navigateToSubmitResultPage.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(
                    CourseAssignmentFragmentDirections.actionCourseAssignmentFragmentToCourseSubmitFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.chapterId
                    )
                )
        }
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.fragCourseAssignmentTab, binding.fragCourseAssignmentVp) { tab, position ->
            tab.text = (position + 1).toString()
        }.attach()
    }

    override fun onSubmitAnswerListener(assignmentQuestion: AssignmentQuestion, selectedIndex: Int) {
        viewmodel.addSelectedAnswer(assignmentQuestion, selectedIndex)
    }

    override fun onShowResultListener() {
        if (checkNotNull(viewmodel.allQuestionsAnswered.value) && viewmodel.allQuestionsAnswered.value == false) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.confirmation_dialog_show_result_title))
                .setMessage(getString(R.string.confirmation_dialog_show_result_message))
                .setNegativeButton(getString(R.string.dialog_no_text)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton(getString(R.string.dialog_yes_text)) { _, _ ->
                    viewmodel.submitAnswer()
                    binding.fragCourseAssignmentLoading.visibility = View.VISIBLE
                    binding.fragCourseAssignmentContent.visibility = View.GONE
                }
                .show()
        }
        else {
            viewmodel.submitAnswer()
            binding.fragCourseAssignmentLoading.visibility = View.VISIBLE
            binding.fragCourseAssignmentContent.visibility = View.GONE
        }
    }
}