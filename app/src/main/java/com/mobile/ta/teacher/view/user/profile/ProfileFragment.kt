package com.mobile.ta.teacher.view.user.profile

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.TFragProfileBinding
import com.mobile.ta.teacher.viewmodel.user.profile.ProfileViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.isNotNull
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
        setLoadingState(true)
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
                viewModel.fetchCoursePublishedCount(user.id)
                setCourseCreatedInfo(user.totalCourseCreated)
                setLoadingState(false)
            }
        })
        viewModel.coursePublished.observe(viewLifecycleOwner, Observer(::setCoursePublishedInfo))
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
            profileBio.text = if (bio.isNullOrBlank()) "Teacher" else bio
        }
    }

    private fun setAboutData(email: String, birthDate: String?) {
        binding.viewCardAboutSection.apply {
            profileAboutEmail.text = email

            birthDate?.let {
                profileAboutBirthDate.text = it
                groupAboutBirthDate.visibility = View.VISIBLE
            } ?: run {
                groupAboutBirthDate.visibility = View.GONE
            }
        }
    }

    private fun setCourseCreatedInfo(courseCreatedCount: Int?) {
        binding.profileInfoCourseCount.apply {
            setValueSection1(courseCreatedCount.toString())
            if (courseCreatedCount.isNotNull() && courseCreatedCount!! > 0) {
                setLabelSection1(
                    resources.getQuantityText(
                        R.plurals.profile_course_created_label,
                        courseCreatedCount
                    ).toString()
                )
            }
        }
    }

    private fun setCoursePublishedInfo(coursePublishedCount: Int) {
        binding.profileInfoCourseCount.apply {
            setValueSection2(coursePublishedCount.toString())
            if (coursePublishedCount > 0) {
                setLabelSection2(
                    resources.getQuantityText(
                        R.plurals.profile_course_published_label,
                        coursePublishedCount
                    ).toString()
                )
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        binding.progressBarProfileLoad.visibility = if (isLoading) {
            View.VISIBLE
        } else {
            View.GONE
        }
        binding.groupProfileLoad.visibility = if (isLoading) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun signOut() {
        viewModel.doLogOut()
        RouterUtil.goToLogin(mContext)
    }
}