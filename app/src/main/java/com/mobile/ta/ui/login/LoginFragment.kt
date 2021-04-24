package com.mobile.ta.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mobile.ta.MainActivity
import com.mobile.ta.R
import com.mobile.ta.databinding.FragmentLoginBinding
import com.mobile.ta.ui.BaseFragment
import com.mobile.ta.ui.HomeFragmentDirections
import com.mobile.ta.utils.orFalse
import com.mobile.ta.viewmodel.login.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate),
    View.OnClickListener {

    companion object {
        private const val INPUT_CREDENTIALS_TAG = "INPUT CREDENTIALS"
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var googleSignInOptions: GoogleSignInOptions

    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    private val viewModel by viewModels<LoginViewModel>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signInLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    viewModel.getAccountAndAuthenticateUser(result.data)
                }
            }
    }

    override fun runOnCreateView() {
        binding.apply {
            buttonSignIn.setOnClickListener(this@LoginFragment)
            buttonTeacherRole.setOnClickListener(this@LoginFragment)
        }
        (activity as MainActivity).hideToolbar()
        setupGoogleSignIn()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isAuthenticated.observe(viewLifecycleOwner, { isAuthenticated ->
            checkIsAuthenticated(isAuthenticated)
        })
        viewModel.isRegistered.observe(viewLifecycleOwner, { isRegistered ->
            checkIsRegistered(isRegistered)
        })
        viewModel.teacherCredentials.observe(viewLifecycleOwner, { credentials ->
            checkTeacherCredentials(credentials.first)
        })
    }

    override fun onClick(view: View?) {
        binding.apply {
            when (view) {
                buttonSignIn -> launchSignInIntent()
                buttonTeacherRole -> openCredentialsBottomSheet()
            }
        }
    }

    private fun checkIsAuthenticated(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            viewModel.getIsUserRegistered()
        } else {
            showToast(R.string.fail_to_authenticate_message)
        }
    }

    private fun checkIsRegistered(isRegistered: Boolean) {
        if (isRegistered) {
            goToHome()
        } else {
            goToSignUp(viewModel.teacherCredentials.value?.first.orFalse())
        }
    }

    private fun checkTeacherCredentials(isSuccess: Boolean) {
        if (isSuccess) {
            launchSignInIntent()
        } else {
            showToast(R.string.wrong_credentials_message)
        }
    }

    private fun goToHome() {
        findNavController().navigate(HomeFragmentDirections.actionGlobalHomeFragment())
    }

    private fun goToSignUp(isTeacher: Boolean) {
        findNavController()
            .navigate(LoginFragmentDirections.actionLoginFragmentToRegistrationFragment(isTeacher))
    }

    private fun openCredentialsBottomSheet() {
        InputCredentialBottomSheetDialogFragment.newInstance(viewModel::checkTeacherCredential)
            .show(parentFragmentManager, INPUT_CREDENTIALS_TAG)
    }

    // TODO: Move to splash screen or main activity
    private fun processSignIn() {
        GoogleSignIn.getLastSignedInAccount(mContext)?.let { account ->
            viewModel.onSuccessGetAccount(account)
        } ?: run {
            launchSignInIntent()
        }
    }

    private fun setupGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(mContext, googleSignInOptions)
    }

    private fun launchSignInIntent() {
        signInLauncher.launch(googleSignInClient.signInIntent)
    }
}