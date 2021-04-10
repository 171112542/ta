package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.adapter.CoursePracticeAdapter
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.data.CoursePracticeData
import com.mobile.ta.databinding.FragCoursePracticeBinding

class CoursePracticeFragment : Fragment() {
    private var _binding: FragCoursePracticeBinding? = null
    private val binding get() = _binding as FragCoursePracticeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragCoursePracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        val adapter = CoursePracticeAdapter(CourseQuestionDiffCallback())
        binding.fragCoursePracticeVp.adapter = adapter
        adapter.submitList(CoursePracticeData.data)
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.fragCoursePracticeTab, binding.fragCoursePracticeVp) { tab, position ->
            tab.text = (position + 1).toString()
        }.attach()
    }
}