package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.mobile.ta.databinding.FragmentThreeDBinding

class ThreeDFragment : Fragment() {
    private lateinit var binding : FragmentThreeDBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThreeDBinding.inflate(inflater, container, false)
        binding.apply {
            webview.settings.allowContentAccess = true
            webview.settings.javaScriptEnabled = true
            webview.loadUrl("file:///android_asset/sketchfab-ex.html")
            webview.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return true
                }
            }
        }
        return binding.root
    }
}