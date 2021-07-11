package com.mobile.ta.student.view.user.settings

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.ta.databinding.FragmentSettingsBinding
import com.mobile.ta.student.viewmodel.user.settings.SettingsViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.view.RouterUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate),
    View.OnClickListener {

    private val viewModel: SettingsViewModel by viewModels()

    override fun runOnCreateView() {
        binding.apply {
            textViewGiveAFeedback.setOnClickListener(this@SettingsFragment)
            textViewLogOut.setOnClickListener(this@SettingsFragment)
        }
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                textViewGiveAFeedback -> goToAddFeedback()
                textViewLogOut -> processLogOut()
            }
        }
    }

    private fun goToAddFeedback() {
        findNavController().navigate(
            SettingsFragmentDirections.actionSettingsFragmentToFeedbackFragment()
        )
    }

    private fun processLogOut() {
        viewModel.doLogOut()
        RouterUtil.goToLogin(mContext)
    }
}