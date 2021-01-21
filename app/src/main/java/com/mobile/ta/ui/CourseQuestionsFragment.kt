package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.adapter.CourseQuestionAdapter
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.data.CourseQuestionData
import com.mobile.ta.databinding.FragCourseQuestionsBinding

class CourseQuestionsFragment : Fragment() {
    private var _binding: FragCourseQuestionsBinding? = null
    private val binding get() = _binding as FragCourseQuestionsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragCourseQuestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        val adapter = CourseQuestionAdapter(CourseQuestionDiffCallback())
        binding.fragCourseQuestionsVp.adapter = adapter
        adapter.submitList(CourseQuestionData.data)
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.fragCourseQuestionsTab, binding.fragCourseQuestionsVp) { tab, position ->
            tab.text = (position + 1).toString()
        }.attach()
    }
}