package com.mobile.ta.ui.coursecontent

import android.annotation.SuppressLint
import android.view.View
import android.webkit.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.mobile.ta.R
import com.mobile.ta.databinding.FragmentThreeDBinding
import com.mobile.ta.ui.BaseFragment
import com.mobile.ta.viewmodel.courseContent.ThreeDViewModel

class ThreeDFragment : BaseFragment<FragmentThreeDBinding>(FragmentThreeDBinding::inflate),
    View.OnClickListener {
    private val viewModel: ThreeDViewModel by viewModels()
    private val args: ThreeDFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun runOnCreateView() {
        binding.apply {
            threeDWebView.settings.apply {
                javaScriptEnabled = true
            }
            threeDWebView.loadUrl("file:///android_asset/sketchfab-ex.html")
            threeDWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    return true
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                }
            }
            threeDWebView.addJavascriptInterface(
                SketchfabWebAppInterface(
                    args.sketchfabId,
                    viewModel::toggleBackButtonState,
                ), "Android"
            )

            threeDBackButton.setOnClickListener(this@ThreeDFragment)
            viewModel.isVisibleBackButton.observe(viewLifecycleOwner, {
                threeDBackButton.isVisible = it
                threeDBackInfoLabel.isVisible = !it
            })
        }
    }

    override fun onDestroyView() {
        viewModel.clearJob()
        super.onDestroyView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.three_d_back_button -> v.findNavController().navigateUp()
        }
    }

    class SketchfabWebAppInterface(
        private val sketchfabId: String,
        private val toggleBackButtonState: () -> Unit
    ) {
        @JavascriptInterface
        fun getSketchfabId(): String {
            return sketchfabId
        }

        @JavascriptInterface
        fun toggleBackButton() {
            toggleBackButtonState.invoke()
        }
    }
}