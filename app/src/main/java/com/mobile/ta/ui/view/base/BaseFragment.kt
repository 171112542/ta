package com.mobile.ta.ui.view.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.mobile.ta.R
import com.mobile.ta.utils.isNull
import com.mobile.ta.utils.view.RouterUtil
import com.mobile.ta.utils.wrapper.Inflate

abstract class BaseFragment<T : ViewBinding>(
    private val inflate: Inflate<T>
) : Fragment(), FirebaseAuth.AuthStateListener {

    private var _binding: T? = null
    protected val binding get() = _binding as T

    protected lateinit var mContext: Context
    protected lateinit var mActivity: AppCompatActivity

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    protected lateinit var intentLauncher: ActivityResultLauncher<Intent>

    protected var loadingDialog: Dialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
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

    open fun onIntentResult(data: Intent?) {}

    open fun onPermissionGranted() {}

    open fun onPermissionNotGranted() {
        showToast(R.string.permission_not_granted_message)
    }

    open fun runOnCreate() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = requireContext()
        mActivity = activity as AppCompatActivity
        runOnCreate()
    }

    open fun runOnCreateView() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = inflate.invoke(inflater, container, false)
        runOnCreateView()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAuthStateChanged(auth: FirebaseAuth) {
        redirectIfLoggedOut()
    }

    protected fun showToast(messageId: Int) {
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).show()
    }

    protected fun showToastWithCloseAction(messageId: Int) {
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT)
            .setAction(R.string.close_action) {
                it.visibility = View.GONE
            }.show()
    }

    protected fun checkPermission(permission: String) {
        if (checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    protected fun <T : Any> loadImage(image: T, imageView: ImageView) {
        Glide.with(mContext).load(image).into(imageView)
    }

    private fun redirectIfLoggedOut() {
        if (FirebaseAuth.getInstance().currentUser.isNull()) {
            RouterUtil.goToLogin(mContext)
        }
    }
}