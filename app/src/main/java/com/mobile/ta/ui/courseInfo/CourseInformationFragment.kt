package com.mobile.ta.ui.courseInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.ta.databinding.FragmentCourseInformationBinding

class CourseInformationFragment : Fragment() {

    companion object {
        private const val COURSE_INFORMATION_TAG = "COURSE_INFORMATION"
    }

    private lateinit var binding: FragmentCourseInformationBinding

//    private val args: CourseInform

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseInformationBinding.inflate(inflater, container, false)
        binding.buttonEnrollCourse.setOnClickListener {
            openInputEnrollmentKeyBottomSheet()
        }
        return binding.root
    }

    private fun openInputEnrollmentKeyBottomSheet() {
//        InputEnrollmentKeyBottomSheetDialogFragment.newInstance()
    //        .show(parentFragmentManager, COURSE_INFORMATION_TAG)
    }
}