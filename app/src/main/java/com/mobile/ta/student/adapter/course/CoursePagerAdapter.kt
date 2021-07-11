package com.mobile.ta.student.adapter.course

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.ta.student.view.course.CourseTabFragment

class CoursePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return CourseTabFragment().apply {
            arguments = Bundle().let { bundle ->
                bundle.putInt(EXTRA_POSITION, position)
                bundle
            }
        }
    }

    companion object {
        const val EXTRA_POSITION = "EXTRA_POSITION"
    }
}