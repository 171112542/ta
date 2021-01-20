package com.mobile.ta.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.mobile.ta.data.UserData.dobDateFormat
import com.mobile.ta.data.UserData.labelDobDateFormat
import com.mobile.ta.databinding.FragmentProfileAboutTabBinding
import com.mobile.ta.viewmodel.profile.ProfileViewModel

class ProfileAboutTabFragment : Fragment() {
    private lateinit var binding: FragmentProfileAboutTabBinding
    private val viewModel: ProfileViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileAboutTabBinding.inflate(inflater, container, false)
        binding.apply {
            viewModel.user.observe(viewLifecycleOwner, Observer {
                profileAboutEmail.text = it.email
                profileAboutPhone.text = it.phone
                profileAboutBirthDate.text = labelDobDateFormat.format(it.dob)
            })
        }
        return binding.root
    }
}