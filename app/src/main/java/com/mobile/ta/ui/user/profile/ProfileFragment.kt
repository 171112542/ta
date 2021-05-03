package com.mobile.ta.ui.user.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.adapter.user.profile.ProfilePagerAdapter
import com.mobile.ta.databinding.FragProfileBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.ui.login.LoginFragmentDirections
import com.mobile.ta.viewmodel.user.profile.ProfileViewModel
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
            }
        })
        viewModel.isAuthenticated.observe(viewLifecycleOwner, {
            if (it.not()) {
                findNavController().navigate(LoginFragmentDirections.actionGlobalLoginFragment())
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
                loadImage(it, profilePhotoImageView)
            }
            profileName.text = name
            bio?.let {
                profileBio.text = it
            } ?: run {
                profileBio.visibility = View.GONE
            }
        }
    }

    private fun setCourseInfo(courseCount: String, finishedCourseCount: String) {
        binding.apply {
            textViewProfileCourse.text = courseCount
            textViewProfileFinished.text = finishedCourseCount
        }
    }
}