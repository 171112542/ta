package com.mobile.ta.ui.splash

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.mobile.ta.databinding.ActivitySplashScreenBinding
import com.mobile.ta.ui.base.BaseActivity
import com.mobile.ta.ui.main.MainActivity
import com.mobile.ta.viewmodel.splash.SplashScreenViewModel
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
        viewModel.isAuthenticated.observe(this, { isAuthenticated ->
            checkIsAuthenticated(isAuthenticated)
        })
    }

    private fun checkForActiveAccount() {
        GoogleSignIn.getLastSignedInAccount(this)?.idToken?.let { token ->
            viewModel.authenticateUser(token)
        } ?: run {
            goToLoginPage()
        }
    }

    private fun checkIsAuthenticated(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            goToHome()
        } else {
            goToLoginPage()
        }
    }

    private fun goToHome() {
        launchMainActivity(MainActivity.PARAM_HOME_FRAGMENT)
    }

    private fun goToLoginPage() {
        launchMainActivity(MainActivity.PARAM_LOGIN_FRAGMENT)
    }

    private fun launchMainActivity(firstLaunchFragment: String) {
        val mainActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(MainActivity.PARAM_FIRST_LAUNCH_FRAGMENT, firstLaunchFragment)
        }
        startActivity(mainActivityIntent)
        finish()
    }
}