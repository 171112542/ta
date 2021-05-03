package com.mobile.ta.adapter.user.profile

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mobile.ta.ui.user.profile.ProfileAboutTabFragment
import com.mobile.ta.ui.user.profile.ProfileFeedbackTabFragment

class ProfilePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileAboutTabFragment()
            else -> ProfileFeedbackTabFragment()
        }
    }
}