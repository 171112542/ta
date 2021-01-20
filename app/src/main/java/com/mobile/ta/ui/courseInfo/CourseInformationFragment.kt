package com.mobile.ta.ui.courseInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
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

    private fun enrollCourse(enrollmentKey: String) {
        Snackbar.make(binding.root, "Course enrolled.", Snackbar.LENGTH_SHORT).show()
        findNavController().navigate(CourseInformationFragmentDirections
            .actionCourseInformationFragmentToCourseContentFragment())
    }

    private fun openInputEnrollmentKeyBottomSheet() {
//        InputEnrollmentKeyBottomSheetDialogFragment.newInstance()
        //        .show(parentFragmentManager, COURSE_INFORMATION_TAG)
    }

    private fun setupCourseMainInfo(image: String, title: String, createdBy: String) {
        with(binding) {
            textViewCourseInfoTitle.text = title
            textViewCourseInfoCreatedBy.text = getString(R.string.created_by, createdBy)

            context?.let {
                // glide to put image
            }
        }
    }
}