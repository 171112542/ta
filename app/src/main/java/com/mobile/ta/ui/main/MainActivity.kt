package com.mobile.ta.ui.main

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.mobile.ta.R
import com.mobile.ta.databinding.ActivityMainBinding
import com.mobile.ta.ui.base.BaseActivity
import com.mobile.ta.ui.course.MyCourseFragmentDirections
import com.mobile.ta.ui.home.HomeFragmentDirections
import com.mobile.ta.ui.user.profile.ProfileFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    companion object {
        const val PARAM_FIRST_LAUNCH_FRAGMENT = "PARAM_FIRST_LAUNCH_FRAGMENT"
        const val PARAM_LOGIN_FRAGMENT = "LOGIN_FRAGMENT"
        const val PARAM_HOME_FRAGMENT = "HOME_FRAGMENT"
    }

    private lateinit var toolbar: Toolbar

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var hideBottomBar = false

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            hideBottomBar = when (destination.id) {
                R.id.homeFragment -> false
                R.id.myCourseFragment -> false
                R.id.profileFragment -> false
                else -> true
            }
            toggleBottomNavAnimation()
            showActionBar(destination.id)
        }

    override val viewBindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    override fun setupViews() {
        initVariables()
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupBottomNavMenu(navController)
        setContentView(binding.root)
        setStartFragment()
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun getToolbar() = toolbar

    private fun initVariables() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.act_main_host_fragment) as NavHostFragment).navController
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.myCourseFragment,
                R.id.profileFragment,
                R.id.courseContentFragment
            )
        )
        toolbar = binding.mainToolbar
    }

    private fun setStartFragment() {
//        intent.getStringExtra(PARAM_FIRST_LAUNCH_FRAGMENT)?.let { firstLaunchFragment ->
//            when (firstLaunchFragment) {
//                PARAM_HOME_FRAGMENT ->
//                    navController.navigate(HomeFragmentDirections.actionGlobalHomeFragment())
//                PARAM_LOGIN_FRAGMENT ->
//                    navController.navigate(LoginFragmentDirections.actionGlobalLoginFragment())
//            }
//        }
    }

    private fun setupBottomNavMenu(navController: NavController) {
        binding.actMainBottomNav.onItemSelected = { pos ->
            when (pos) {
                0 -> navController.navigate(HomeFragmentDirections.actionGlobalHomeFragment())
                1 -> navController.navigate(MyCourseFragmentDirections.actionGlobalMyCourseFragment())
                2 -> navController.navigate(ProfileFragmentDirections.actionGlobalProfileFragment())
            }
        }
    }

    private fun showActionBar(destinationId: Int) {
        val isVisible = when (destinationId) {
            R.id.loginFragment -> false
            R.id.registrationFragment -> false
            R.id.homeFragment -> false
            R.id.threeDFragment -> false
            R.id.searchFragment -> false
            else -> true
        }
        if (isVisible) {
            supportActionBar?.show()
        } else {
            supportActionBar?.hide()
        }
    }

    private fun toggleBottomNavAnimation() {
        val translateDpValue =
            if (hideBottomBar) resources.getDimension(R.dimen.bottom_nav_height) + 30f
            else 0f
        val translatePixelValue = translateDpValue * resources.displayMetrics.density

        val animator =
            ObjectAnimator.ofFloat(
                binding.actMainBottomNavContainer,
                View.TRANSLATION_Y,
                translatePixelValue
            )
        animator.duration = 500
        animator.start()
    }
}