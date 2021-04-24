package com.mobile.ta.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<T : ViewBinding>(
    private val inflate: Inflate<T>
) : Fragment() {
    private var _binding: T? = null
    protected val binding get() = _binding as T
    protected lateinit var mContext: Context
    protected lateinit var mActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = requireContext()
        mActivity = activity as AppCompatActivity
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

    protected fun showToast(messageId: Int) {
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).show()
    }
}