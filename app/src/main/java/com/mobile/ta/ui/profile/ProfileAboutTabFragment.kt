package com.mobile.ta.ui.profile

import androidx.fragment.app.activityViewModels
import com.mobile.ta.databinding.FragmentProfileAboutTabBinding
import com.mobile.ta.ui.BaseFragment
import com.mobile.ta.viewmodel.profile.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileAboutTabFragment :
    BaseFragment<FragmentProfileAboutTabBinding>(FragmentProfileAboutTabBinding::inflate) {

    private val viewModel: ProfileViewModel by activityViewModels()

    override fun runOnCreateView() {
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.user.observe(viewLifecycleOwner, {
            it?.let { user ->
//                setAboutData(user.email, user.phone)
            }
        })
    }

    private fun setAboutData(email: String, phone: String, birthDate: String) {
        binding.apply {
            profileAboutEmail.text = email
            profileAboutPhone.text = phone
//            profileAboutBirthDate.text = labelDobDateFormat.format(it.dob)
        }
    }
}