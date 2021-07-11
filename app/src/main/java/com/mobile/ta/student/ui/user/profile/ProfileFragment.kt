package com.mobile.ta.student.ui.user.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.student.adapter.user.profile.ProfilePagerAdapter
import com.mobile.ta.databinding.FragProfileBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.view.ImageUtil
import com.mobile.ta.utils.view.RouterUtil
import com.mobile.ta.student.viewmodel.user.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragProfileBinding>(FragProfileBinding::inflate),
    View.OnClickListener {

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun runOnCreateView() {
        val profilePagerAdapter = ProfilePagerAdapter(this)
        binding.apply {
            profileEditButton.setOnClickListener(this@ProfileFragment)
            profileViewPager.adapter = profilePagerAdapter
            (profileViewPager.getChildAt(0) as ViewGroup).clipChildren = false
            TabLayoutMediator(profileTabLayout, profileViewPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.about_tab)
                    1 -> tab.text = getString(R.string.feedback_tab)
                }
            }.attach()
        }
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserData()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile_setting_item -> goToSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        binding.apply {
            when (view) {
                profileEditButton -> goToEditProfile()
            }
        }
    }

    private fun setupObserver() {
        viewModel.fetchUserData()
        viewModel.user.observe(viewLifecycleOwner, {
            it?.let { user ->
                setProfileData(user.photo, user.name, user.bio)
                viewModel.fetchUserCourseCount(user.id)
            }
        })
        viewModel.userCourseCount.observe(viewLifecycleOwner, {
            it?.let { courseCount ->
                setCourseInfo(courseCount.first, courseCount.second)
            }
        })
        viewModel.isAuthenticated.observe(viewLifecycleOwner, {
            if (it.not()) {
                RouterUtil.goToLogin(mContext)
            }
        })
    }

    private fun goToEditProfile() {
        findNavController().navigate(
            ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(
                viewModel.user.value!!
            )
        )
    }

    private fun goToSettings() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToSettingsFragment())
    }

    private fun setProfileData(photo: String?, name: String, bio: String?) {
        binding.apply {
            photo?.let {
                ImageUtil.loadImage(mContext, it, profilePhotoImageView)
            }
            profileName.text = name
            bio?.let {
                profileBio.text = it
            } ?: run {
                profileBio.visibility = View.GONE
            }
        }
    }

    private fun setCourseInfo(courseCount: Int, finishedCourseCount: Int) {
        binding.apply {
            textViewProfileCourse.text = courseCount.toString()
            textViewProfileFinished.text = finishedCourseCount.toString()

            textViewProfileCourseLabel.text =
                resources.getQuantityText(R.plurals.profile_course_label, courseCount)
        }
    }
}