package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.ta.databinding.FragCourseSubmitBinding
import com.mobile.ta.viewmodel.CoursePracticeViewModel
import com.mobile.ta.viewmodel.CourseSubmitViewModel

class CourseSubmitFragment : Fragment() {
    private var _binding: FragCourseSubmitBinding? = null
    private val binding get() = _binding as FragCourseSubmitBinding
    private val viewmodel by viewModels<CourseSubmitViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragCourseSubmitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPage()
    }

    private fun setupPage() {
        viewmodel.result.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.fragCourseSubmitLoading.visibility = View.GONE
                binding.fragCourseSubmitContent.visibility = View.VISIBLE
                val totalAnswerCount = it["totalAnswerCount"] as Long
                val correctAnswerCount = it["correctAnswerCount"] as Long
                binding.fragCourseSubmitScore.text = "$correctAnswerCount/$totalAnswerCount"
            }
        }
        binding.fragCourseSubmitRetry.setOnClickListener {
            viewmodel.retry()
            findNavController().navigate(CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment())
        }
        binding.fragCourseSubmitNextChapter.setOnClickListener {
            findNavController().navigate(CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCourseContentFragment())
        }
    }
}