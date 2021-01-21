package com.mobile.ta.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.mobile.ta.databinding.FragmentThreeDBinding
import com.mobile.ta.viewmodel.course.ThreeDViewModel
import java.util.Timer
import java.util.TimerTask

class ThreeDFragment : Fragment() {
    private lateinit var binding : FragmentThreeDBinding
    private lateinit var mContext: Context
    private val viewModel: ThreeDViewModel by viewModels()
    private val args: ThreeDFragmentArgs by navArgs()
    private var hasStartedTimer = false
    private lateinit var timerTask: TimerTask

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThreeDBinding.inflate(inflater, container, false)
        mContext = requireContext()
        binding.apply {
            threeDWebView.settings.allowContentAccess = true
            threeDWebView.settings.javaScriptEnabled = true
            threeDWebView.loadUrl("file:///android_asset/sketchfab-ex.html")
            threeDWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return true
                }
            }
            threeDWebView.addJavascriptInterface(SketchfabWebAppInterface(
                args.sketchfabId,
                viewModel,
            ), "Android")
            threeDBackInfoLabel.bringToFront()
            threeDBackButton.setOnClickListener {
                it.findNavController().navigateUp()
            }

            val timer = Timer()
            viewModel.backButtonState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                threeDBackButton.visibility = if (it) View.VISIBLE else View.GONE
                threeDBackInfoLabel.visibility = if (!it) View.VISIBLE else View.GONE
                if (it) {
                    cancelTimer()
                    startTimer(timer)
                } else {
                    cancelTimer()
                }
            })
        }
        return binding.root
    }

    private fun startTimer(timer: Timer) {
        timerTask = object : TimerTask() {
            override fun run() {
                hasStartedTimer = false
                viewModel.setBackButtonState(false)
            }
        }
        timer.schedule(timerTask, 5000)
        hasStartedTimer = true
    }
    private fun cancelTimer() {
        if (hasStartedTimer) {
            hasStartedTimer = false
            timerTask.cancel()
        }
    }
    class SketchfabWebAppInterface(private val sketchfabId: String, private val viewModel: ThreeDViewModel) {
        @JavascriptInterface
        fun getSketchfabId() : String {
            return sketchfabId
        }
        @JavascriptInterface
        fun toggleBackButton() {
            viewModel.toggleBackButtonState()
        }
    }
}