package com.mobile.ta.adapter

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.ta.ui.CourseTabFragment
import com.mobile.ta.ui.ProfileAboutTabFragment
import com.mobile.ta.ui.ProfileFeedbackTabFragment

class CoursePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return CourseTabFragment().apply {
            arguments = Bundle().let{ bundle ->
                bundle.putInt("position", position)
                bundle
            }
        }
    }
}