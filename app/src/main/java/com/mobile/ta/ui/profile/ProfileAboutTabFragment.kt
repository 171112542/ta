package com.mobile.ta.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentProfileAboutTabBinding
import com.mobile.ta.ui.BaseFragment
import com.mobile.ta.utils.toDateString
import com.mobile.ta.viewmodel.profile.ProfileViewModel
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
                setAboutData(user.email, user.phoneNumber, user.birthDate?.toDateString(Constants.MMMM_DD_YYYY))
            }
        })
    }

    private fun setAboutData(email: String, phone: String?, birthDate: String?) {
        binding.apply {
            profileAboutEmail.text = email

            phone?.let {
                profileAboutPhone.text = it
                groupAboutPhone.visibility = View.VISIBLE
            } ?: run {
                groupAboutPhone.visibility = View.GONE
            }

            birthDate?.let {
                profileAboutBirthDate.text = it
                groupAboutBirthDate.visibility = View.VISIBLE
            } ?: run {
                groupAboutBirthDate.visibility = View.GONE
            }
        }
    }
}