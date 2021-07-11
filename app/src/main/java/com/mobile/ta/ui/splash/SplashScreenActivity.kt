package com.mobile.ta.ui.splash

import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mobile.ta.databinding.ActivitySplashScreenBinding
import com.mobile.ta.ui.base.BaseActivity
import com.mobile.ta.utils.view.RouterUtil
import com.mobile.ta.student.viewmodel.splash.SplashScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashScreenActivity : BaseActivity<ActivitySplashScreenBinding>() {

    override val viewBindingInflater: (LayoutInflater) -> ActivitySplashScreenBinding
        get() = ActivitySplashScreenBinding::inflate

    private val viewModel: SplashScreenViewModel by viewModels()

    override fun setupViews() {
        checkForActiveAccount()
    }

    override fun setupObserver() {
        viewModel.isAuthenticated.observe(this, Observer(::checkIsAuthenticated))
        viewModel.isTeacher.observe(this, Observer(::goToHome))
    }

    private fun checkForActiveAccount() {
        Log.d("Account", GoogleSignIn.getLastSignedInAccount(this)?.idToken.toString())
        GoogleSignIn.getLastSignedInAccount(this)?.idToken?.let { token ->
            viewModel.authenticateUser(token)
        } ?: run {
            goToLogin()
        }
    }

    private fun checkIsAuthenticated(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            viewModel.checkUserRole()
        } else {
            goToLogin()
        }
    }

    private fun goToHome(isTeacher: Boolean) {
        RouterUtil.goToMain(this, isTeacher)
    }

    private fun goToLogin() {
        RouterUtil.goToLogin(this)
    }
}