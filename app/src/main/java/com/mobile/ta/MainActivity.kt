package com.mobile.ta

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mobile.ta.databinding.ActivityMainBinding
import com.mobile.ta.ui.HomeFragmentDirections
import com.mobile.ta.ui.MyCourseFragmentDirections
import com.mobile.ta.ui.ProfileFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var hideBottomBar = false
    private val destinationChangedListener = NavController.OnDestinationChangedListener { _, destination, _ ->
        hideBottomBar = when (destination.id) {
            R.id.homeFragment -> false
            R.id.myCourseFragment -> false
            R.id.profileFragment -> false
            else -> true
        }
        toggleBottomNavAnimation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        navController =
            (supportFragmentManager.findFragmentById(R.id.act_main_host_fragment) as NavHostFragment).navController

        // Delete from this line...
        navController.navigate(HomeFragmentDirections.actionGlobalHomeFragment())
        // ...until this line later
        setContentView(binding.root)
        setupBottomNavMenu(navController)
    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(destinationChangedListener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(destinationChangedListener)
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
            ObjectAnimator.ofFloat(binding.actMainBottomNavContainer, View.TRANSLATION_Y, translatePixelValue)
        animator.duration = 500
        animator.start()
    }
}