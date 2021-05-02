package com.mobile.ta

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.mobile.ta.databinding.ActivityMainBinding
import com.mobile.ta.ui.home.HomeFragmentDirections
import com.mobile.ta.ui.MyCourseFragmentDirections
import com.mobile.ta.ui.profile.ProfileFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    lateinit var toolbar: Toolbar
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navController =
            (supportFragmentManager.findFragmentById(R.id.act_main_host_fragment) as NavHostFragment).navController
        toolbar = binding.mainToolbar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.myCourseFragment,
                R.id.profileFragment,
                R.id.courseContentFragment
            )
        )

        setContentView(binding.root)
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        setupBottomNavMenu(navController)
        setContentView(binding.root)
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

    fun hideToolbar() {
        toolbar.visibility = View.GONE
    }

    private fun showActionBar(destinationId: Int) {
        val isVisible = when (destinationId) {
            R.id.loginFragment -> false
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

    fun showToolbar(title: String? = null, isMain: Boolean? = null) {
        with(toolbar) {
            visibility = View.VISIBLE
            title?.let {
                this.title = it
            }
//            isMain?.let {
//                navigationIcon = null
//            }
        }
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