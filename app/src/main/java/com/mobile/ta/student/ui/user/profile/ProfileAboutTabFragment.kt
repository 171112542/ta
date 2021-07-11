package com.mobile.ta.student.ui.user.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentProfileAboutTabBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.toDateString
import com.mobile.ta.student.viewmodel.user.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileAboutTabFragment :
    BaseFragment<FragmentProfileAboutTabBinding>(FragmentProfileAboutTabBinding::inflate) {

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.user.observe(viewLifecycleOwner, {
            it?.let { user ->
                setAboutData(
                    user.email,
                    user.birthDate?.toDateString(Constants.MMMM_DD_YYYY)
                )
            }
        })
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
}