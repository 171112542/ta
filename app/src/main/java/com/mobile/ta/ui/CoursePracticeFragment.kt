package com.mobile.ta.ui

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
import com.mobile.ta.adapter.CourseQuestionAdapter
import com.mobile.ta.adapter.CourseQuestionVHListener
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.databinding.FragCoursePracticeBinding
import com.mobile.ta.model.CourseQuestion
import com.mobile.ta.viewmodel.CoursePracticeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoursePracticeFragment
    : Fragment(), CourseQuestionVHListener {
    private var _binding: FragCoursePracticeBinding? = null
    private val binding get() = _binding as FragCoursePracticeBinding
    private val viewmodel by viewModels<CoursePracticeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragCoursePracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        val adapter = CourseQuestionAdapter(CourseQuestionDiffCallback(), this)
        binding.fragCoursePracticeVp.adapter = adapter
        viewmodel.questions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.fragCoursePracticeContent.visibility = View.VISIBLE
            binding.fragCoursePracticeLoading.visibility = View.GONE
        }
        viewmodel.allQuestionsAnswered.observe(viewLifecycleOwner) {
            if (it) adapter.enableSubmitResult()
        }
        viewmodel.navigateToSubmitResultPage.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(
                    CoursePracticeFragmentDirections.actionCoursePracticeFragmentToCourseSubmitFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.chapterId
                    )
                )
        }
    }

    private fun setupTabLayout() {
        TabLayoutMediator(
            binding.fragCoursePracticeTab,
            binding.fragCoursePracticeVp
        ) { tab, position ->
            tab.text = (position + 1).toString()
        }.attach()
    }

    override fun onSubmitAnswerListener(courseQuestion: CourseQuestion, selectedIndex: Int) {
        viewmodel.addSelectedAnswer(courseQuestion, selectedIndex)
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
                    binding.fragCoursePracticeLoading.visibility = View.VISIBLE
                    binding.fragCoursePracticeContent.visibility = View.GONE
                }
                .show()
        } else {
            viewmodel.submitAnswer()
            binding.fragCoursePracticeLoading.visibility = View.VISIBLE
            binding.fragCoursePracticeContent.visibility = View.GONE
        }
    }
}