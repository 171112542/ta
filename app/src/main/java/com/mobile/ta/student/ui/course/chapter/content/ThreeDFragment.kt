package com.mobile.ta.student.ui.course.chapter.content

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.webkit.*
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.Timestamp
import com.mobile.ta.R
import com.mobile.ta.databinding.FragmentThreeDBinding
import com.mobile.ta.model.testing.TimeSpent
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.HandlerUtil
import com.mobile.ta.student.viewmodel.course.chapter.content.ThreeDViewModel
import com.mobile.ta.student.viewmodel.testingtimer.TestingTimerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ThreeDFragment : BaseFragment<FragmentThreeDBinding>(FragmentThreeDBinding::inflate),
    View.OnClickListener {
    private val viewModel: ThreeDViewModel by viewModels()
    private val timerViewModel: TestingTimerViewModel by viewModels()
    private val args: ThreeDFragmentArgs by navArgs()

    @SuppressLint("SetJavaScriptEnabled")
    override fun runOnCreateView() {
        super.runOnCreateView()
        binding.apply {
            threeDWebView.settings.apply {
                javaScriptEnabled = true
            }
            mContext.assets.open("three_d/sketchfab.html").bufferedReader().use {
                threeDWebView.loadDataWithBaseURL(
                    "file:///android_asset/three_d/",
                    it.readText(),
                    "text/html",
                    "UTF-8",
                    null
                )
            }
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
                    threeDProgressBarContainer.isVisible = false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    threeDProgressBarContainer.isVisible = false
                }
            }
            threeDWebView.addJavascriptInterface(
                SketchfabWebAppInterface(
                    args.sketchfabId,
                    viewModel::toggleBackButtonState,
                    mActivity
                ), "Android"
            )

            threeDBackButton.setOnClickListener(this@ThreeDFragment)
            viewModel.isVisibleBackButton.observe(viewLifecycleOwner, {
                threeDBackButton.isVisible = it
                threeDBackInfoLabel.isVisible = !it
            })
        }
        timerViewModel.saveStartTime(Timestamp.now())
        timerViewModel.saveType(TimeSpent.THREE_D)
    }

    override fun onDestroyView() {
        viewModel.clearJob()
        timerViewModel.saveEndTime(Timestamp.now())
        timerViewModel.startSaveTimeSpentWork()
        super.onDestroyView()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.three_d_back_button -> v.findNavController().navigateUp()
        }
    }

    class SketchfabWebAppInterface(
        private val sketchfabId: String,
        private val toggleBackButtonState: () -> Unit,
        private val activity: Activity
    ) {
        @JavascriptInterface
        fun getSketchfabId(): String {
            return sketchfabId
        }

        @JavascriptInterface
        fun toggleBackButton() {
            HandlerUtil.runOnUiThread(activity) {
                toggleBackButtonState.invoke()
            }
        }
    }
}