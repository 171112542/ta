package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.adapter.CoursePagerAdapter
import com.mobile.ta.databinding.FragCourseBinding

class MyCourseFragment : Fragment() {
    private var _binding: FragCourseBinding? = null
    private val binding get() = _binding as FragCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragCourseBinding.inflate(inflater, container, false)
        val coursePagerAdapter = CoursePagerAdapter(this)
        binding.apply {
            courseViewPager.adapter = coursePagerAdapter
            (courseViewPager.getChildAt(0) as ViewGroup).clipChildren = false
            TabLayoutMediator(courseTabLayout, courseViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.course_ongoing_tab)
                    1 -> tab.text = getString(R.string.course_finished_tab)
                }
            }.attach()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}