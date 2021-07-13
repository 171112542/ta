package com.mobile.ta.ui.view.base

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    abstract val viewBindingInflater: (LayoutInflater) -> VB

    protected lateinit var binding: VB

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    protected lateinit var intentLauncher: ActivityResultLauncher<Intent>

    protected var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewBindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
        initializeLauncher()
        setupViews()
        setupObserver()
    }

    abstract fun setupViews()

    open fun setupObserver() {}

    open fun onIntentResult(data: Intent?) {}

    open fun onPermissionGranted() {}

    open fun onPermissionNotGranted() {
        showErrorToast(R.string.permission_not_granted_message)
    }

    private fun initializeLauncher() {
        intentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    onIntentResult(result.data)
                }
            }
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    onPermissionGranted()
                } else {
                    onPermissionNotGranted()
                }
            }
    }

    protected fun checkPermission(permission: String) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    protected fun showErrorToast(messageId: Int) {
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(R.color.design_default_color_error))
            .setTextColor(getColor(R.color.white))
            .show()
    }

    protected fun showToast(messageId: Int) {
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).show()
    }
}