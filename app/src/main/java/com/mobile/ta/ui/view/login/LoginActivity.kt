package com.mobile.ta.ui.view.login

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.mobile.ta.R
import com.mobile.ta.config.ErrorCodeConstants
import com.mobile.ta.databinding.ActivityLoginBinding
import com.mobile.ta.ui.view.base.BaseActivity
import com.mobile.ta.ui.viewmodel.login.LoginViewModel
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.orFalse
import com.mobile.ta.utils.view.DialogHelper
import com.mobile.ta.utils.view.RouterUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(), View.OnClickListener {

    companion object {
        private const val INPUT_CREDENTIALS_TAG = "INPUT CREDENTIALS"
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var googleSignInOptions: GoogleSignInOptions

    private val viewModel by viewModels<LoginViewModel>()

    override val viewBindingInflater: (LayoutInflater) -> ActivityLoginBinding
        get() = ActivityLoginBinding::inflate

    override fun setupViews() {
        supportActionBar?.hide()
        loadingDialog = DialogHelper.createLoadingDialog(this)
        binding.apply {
            buttonSignIn.setOnClickListener(this@LoginActivity)
            buttonTeacherRole.setOnClickListener(this@LoginActivity)
        }
        setupGoogleSignIn()
    }

    override fun setupObserver() {
        super.setupObserver()

        viewModel.isAuthenticated.observe(this, Observer(::checkIsAuthenticated))

        viewModel.isRegistered.observe(this, Observer(::checkIsRegistered))

        viewModel.isValidCredentials.observe(this, Observer(::checkTeacherCredentials))
    }

    override fun onClick(view: View?) {
        binding.apply {
            when (view) {
                buttonSignIn -> launchSignInIntent()
                buttonTeacherRole -> openCredentialsBottomSheet()
            }
        }
    }

    override fun onIntentResult(data: Intent?) {
        viewModel.getAccountAndAuthenticateUser(data)
        DialogHelper.showDialog(loadingDialog)
    }

    private fun checkIsAuthenticated(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            viewModel.getIsUserRegistered()
        } else {
            DialogHelper.dismissDialog(loadingDialog)
            showErrorToast(R.string.fail_to_authenticate_message)
        }
    }

    private fun checkIsRegistered(isRegistered: Pair<Boolean, Int?>) {
        val isTeacher = viewModel.isValidCredentials.value.orFalse()

        showErrorMessageByCode(isRegistered.second)
        if (isRegistered.second.isNotNull()) {
            return
        }

        if (isRegistered.first) {
            goToHome(isTeacher)
        } else {
            goToSignUp(isTeacher)
        }
        DialogHelper.dismissDialog(loadingDialog)
    }

    private fun showErrorMessageByCode(errorCode: Int?) {
        val errorMessageId = when (errorCode) {
            ErrorCodeConstants.ERROR_CODE_STUDENT_NO_NEED_CREDENTIALS -> R.string.student_no_need_credentials_message
            ErrorCodeConstants.ERROR_CODE_TEACHER_NEED_CREDENTIALS -> R.string.teacher_need_credentials_message
            ErrorCodeConstants.ERROR_CODE_FAIL_TO_SIGN_IN -> R.string.fail_to_sign_in_message
            else -> null
        }
        errorMessageId?.let { errorMessage ->
            showErrorToast(errorMessage)
        }
    }

    private fun checkTeacherCredentials(isSuccess: Boolean) {
        if (isSuccess) {
            launchSignInIntent()
        }
    }

    private fun goToHome(isTeacher: Boolean) {
        RouterUtil.goToMain(this, isTeacher)
    }

    private fun goToSignUp(isTeacher: Boolean) {
        RouterUtil.goToRegistration(this, isTeacher)
    }

    private fun openCredentialsBottomSheet() {
        InputCredentialBottomSheetDialogFragment.newInstance(
            viewModel::checkTeacherCredential,
            viewModel.isValidCredentials
        ).show(supportFragmentManager, INPUT_CREDENTIALS_TAG)
    }

    private fun setupGoogleSignIn() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun launchSignInIntent() {
        intentLauncher.launch(googleSignInClient.signInIntent)
    }
}