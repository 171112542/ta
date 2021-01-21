package com.mobile.ta.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mobile.ta.MainActivity
import com.mobile.ta.databinding.FragmentLoginBinding
import com.mobile.ta.ui.HomeFragmentDirections

class LoginFragment : Fragment() {

    companion object {
        private const val INPUT_CREDENTIALS_TAG = "INPUT CREDENTIALS"
    }

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        with(binding) {
            buttonSignIn.setOnClickListener {
                goToHome()
            }
            buttonTeacherRole.setOnClickListener {
                openCredentialsBottomSheet()
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as MainActivity).hideToolbar()
    }

    private fun goToHome() {
        findNavController().navigate(HomeFragmentDirections.actionGlobalHomeFragment())
    }

    private fun goToSignUp() {
        findNavController()
            .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment())
    }

    private fun onSubmitListener(credentials: String) {
        // TODO: Add submit functionality
        goToHome()
    }

    private fun openCredentialsBottomSheet() {
        InputCredentialBottomSheetDialogFragment.newInstance(this::onSubmitListener)
            .show(parentFragmentManager, INPUT_CREDENTIALS_TAG)
    }
}