package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.ta.MainActivity
import com.mobile.ta.databinding.FragCourseBinding

class MyCourseFragment : Fragment() {
    private var _binding: FragCourseBinding? = null
    private val binding get() = _binding as FragCourseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragCourseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).showToolbar(isMain = true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}