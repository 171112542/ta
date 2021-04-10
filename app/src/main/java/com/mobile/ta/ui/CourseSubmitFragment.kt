package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.ta.databinding.FragCourseSubmitBinding

class CourseSubmitFragment : Fragment() {
    private var _binding: FragCourseSubmitBinding? = null
    private val binding get() = _binding as FragCourseSubmitBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragCourseSubmitBinding.inflate(inflater, container, false)
        return binding.root
    }
}