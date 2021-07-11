package com.mobile.ta.student.view.course

import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.databinding.FragCourseBinding
import com.mobile.ta.student.adapter.course.CoursePagerAdapter
import com.mobile.ta.ui.view.base.BaseFragment

class MyCourseFragment : BaseFragment<FragCourseBinding>(FragCourseBinding::inflate) {
    private val args: MyCourseFragmentArgs by navArgs()

    override fun runOnCreateView() {
        super.runOnCreateView()
        val coursePagerAdapter = CoursePagerAdapter(this)
        binding.apply {
            courseViewPager.adapter = coursePagerAdapter
            courseViewPager.setCurrentItem(args.tab, false)
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