package com.mobile.ta.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.ta.ui.ProfileAboutTabFragment
import com.mobile.ta.ui.ProfileFeedbackTabFragment

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ProfileAboutTabFragment()
            else -> return ProfileFeedbackTabFragment()
        }
    }
}