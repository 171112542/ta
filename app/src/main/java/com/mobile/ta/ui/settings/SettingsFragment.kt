package com.mobile.ta.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.ta.databinding.FragmentSettingsBinding
import com.mobile.ta.ui.main.MainActivity

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        with(binding) {
            textViewGiveAFeedback.setOnClickListener {
                goToAddFeedback()
            }
            textViewLogOut.setOnClickListener {
                goToLogIn()
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).showToolbar()
    }

    private fun goToAddFeedback() {
        findNavController().navigate(
            SettingsFragmentDirections.actionSettingsFragmentToFeedbackFragment()
        )
    }

    private fun goToLogIn() {
        findNavController().navigate(SettingsFragmentDirections.actionGlobalLoginFragment())
    }
}