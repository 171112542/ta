package com.mobile.ta.teacher.view.user.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.TFragProfileBinding
import com.mobile.ta.teacher.viewmodel.user.profile.ProfileViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.toDateString
import com.mobile.ta.utils.view.ImageUtil
import com.mobile.ta.utils.view.RouterUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<TFragProfileBinding>(TFragProfileBinding::inflate),
    View.OnClickListener {

    private val viewModel: ProfileViewModel by viewModels()

    override fun runOnCreateView() {
        binding.apply {
            profileEditButton.setOnClickListener(this@ProfileFragment)
            buttonSignOut.setOnClickListener(this@ProfileFragment)
        }
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    override fun onClick(view: View?) {
        binding.apply {
            when (view) {
                profileEditButton -> goToEditProfile()
                buttonSignOut -> signOut()
            }
        }
    }

    private fun setupObserver() {
        viewModel.fetchUserData()
        viewModel.user.observe(viewLifecycleOwner, {
            it?.let { user ->
                setProfileData(user.photo, user.name, user.bio)
                setAboutData(
                    user.email,
                    user.birthDate?.toDateString(Constants.MMMM_DD_YYYY)
                )
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
                signOut()
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

    private fun setAboutData(email: String, birthDate: String?) {
        binding.apply {
            profileAboutEmail.text = email

            birthDate?.let {
                profileAboutBirthDate.text = it
                groupAboutBirthDate.visibility = View.VISIBLE
            } ?: run {
                groupAboutBirthDate.visibility = View.GONE
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

    private fun signOut() {
        viewModel.doLogOut()
        RouterUtil.goToLogin(mContext)
    }
}