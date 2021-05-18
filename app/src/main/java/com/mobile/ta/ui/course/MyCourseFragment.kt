package com.mobile.ta.ui.course

import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.adapter.course.CoursePagerAdapter
import com.mobile.ta.databinding.FragCourseBinding
import com.mobile.ta.ui.base.BaseFragment

class MyCourseFragment : BaseFragment<FragCourseBinding>(FragCourseBinding::inflate) {
    override fun runOnCreateView() {
        super.runOnCreateView()
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
    }
}