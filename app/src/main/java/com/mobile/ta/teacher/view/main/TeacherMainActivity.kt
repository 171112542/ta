package com.mobile.ta.teacher.view.main

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.mobile.ta.R
import com.mobile.ta.databinding.ActivityTeacherMainBinding
import com.mobile.ta.ui.view.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeacherMainActivity : BaseActivity<ActivityTeacherMainBinding>() {
    private lateinit var toolbar: Toolbar

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var hideBottomBar = false

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            hideBottomBar = when (destination.id) {
                R.id.homeFragment -> false
                R.id.profileFragment -> false
                else -> true
            }
            toggleBottomNavAnimation()
            showActionBar(destination.id)
            addNavigateUpListener(destination.id)
        }

    private fun addNavigateUpListener(destinationId: Int) {
        val isNeedNavigateUp = when (destinationId) {
            R.id.courseContentFragment -> false
            R.id.courseAssignmentFragment -> false
            R.id.courseSubmitFragment -> false
            else -> true
        }
        if (isNeedNavigateUp) {
            toolbar.setNavigationOnClickListener {
                navController.navigateUp(appBarConfiguration)
            }
        }
    }

    override val viewBindingInflater: (LayoutInflater) -> ActivityTeacherMainBinding
        get() = ActivityTeacherMainBinding::inflate

    override fun setupViews() {
        initVariables()
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
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
                R.id.profileFragment
            )
        )
        toolbar = binding.mainToolbar
    }

    private fun setupBottomNavMenu(navController: NavController) {
        val popupMenu = PopupMenu(this, null)
        popupMenu.inflate(R.menu.t_main_nav_menu)
        val menu = popupMenu.menu
        binding.actMainBottomNav.setupWithNavController(menu, navController)
    }

    private fun showActionBar(destinationId: Int) {
        val isVisible = when (destinationId) {
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