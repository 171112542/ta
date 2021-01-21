package com.mobile.ta.ui.courseInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
import com.mobile.ta.adapter.courseInfo.CourseInfoChapterAdapter
import com.mobile.ta.adapter.courseInfo.CourseInfoPrerequisitesCourseAdapter
import com.mobile.ta.adapter.diff.CourseInfoChapterDiffCallback
import com.mobile.ta.adapter.diff.CourseInfoPrerequisitesCourseDiffCallback
import com.mobile.ta.databinding.FragmentCourseInformationBinding
import com.mobile.ta.model.courseInfo.Creator
import com.mobile.ta.utils.ImageUtil

class CourseInformationFragment : Fragment() {

    companion object {
        private const val COURSE_INFORMATION_TAG = "COURSE_INFORMATION"
    }

    private lateinit var binding: FragmentCourseInformationBinding

    private val args: CourseInformationFragmentArgs by navArgs()
    private val courseContentAdapter by lazy {
        CourseInfoChapterAdapter(CourseInfoChapterDiffCallback(), this::goToCourseContent)
    }
    private val recommendedCourseAdapter by lazy {
        CourseInfoPrerequisitesCourseAdapter(
            CourseInfoPrerequisitesCourseDiffCallback(),
            this::goToOtherCourseInfo
        )
    }
    private val relatedCourseAdapter by lazy {
        CourseInfoPrerequisitesCourseAdapter(
            CourseInfoPrerequisitesCourseDiffCallback(),
            this::goToOtherCourseInfo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseInformationBinding.inflate(inflater, container, false)
        binding.buttonEnrollCourse.setOnClickListener {
            openInputEnrollmentKeyBottomSheet()
        }
        setupContentRecyclerView()
        setupRecommendedCourseRecyclerView()
        setupRelatedCourseRecyclerView()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        args.id
    }

    private fun enrollCourse(enrollmentKey: String) {
        Snackbar.make(binding.root, "Course enrolled.", Snackbar.LENGTH_SHORT).show()
        findNavController().navigate(
            CourseInformationFragmentDirections
                .actionCourseInformationFragmentToCourseContentFragment()
        )
    }

    private fun goToCourseContent(id: String) {
//        findNavController().navigate(
//            CourseInformationFragmentDirections.actionCourseInformationFragmentToCourseContentFragment(
//                id
//            )
//        )
    }

    private fun goToOtherCourseInfo(id: String) {
        findNavController().navigate(
            CourseInformationFragmentDirections.actionCourseInformationFragmentSelf(
                id
            )
        )
    }

    private fun loadImage(imageUrl: String, imageView: ImageView) {
        context?.let {
            ImageUtil.loadImage(it, imageUrl, imageView)
        }
    }

    private fun openInputEnrollmentKeyBottomSheet() {
        InputEnrollmentKeyBottomSheetDialogFragment.newInstance(::enrollCourse)
            .show(parentFragmentManager, COURSE_INFORMATION_TAG)
    }

    private fun setupCourseMainInfo(image: String, title: String, createdBy: String) {
        with(binding) {
            textViewCourseInfoTitle.text = title
            textViewCourseInfoCreatedBy.text = getString(R.string.created_by, createdBy)
            loadImage(image, imageViewCourse)
        }
    }

    private fun setupCreatorInfo(creator: Creator) {
        with(binding) {
            textViewCourseInfoCreatorName.text = creator.name
            textViewCourseInfoCreatorDescription.text = creator.description
            textViewCourseInfoCreatorEmail.text = creator.email
            textViewCourseInfoCreatorCourse.text = resources.getQuantityString(
                R.plurals.creator_course_count,
                creator.courseCreated,
                creator.courseCreated
            )
            loadImage(creator.photo, imageViewCourseInfoCreator)
        }
    }

    private fun setupContentRecyclerView() {
        with(binding.recyclerViewCourseInfoContent) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = courseContentAdapter
        }
    }

    private fun setupRecommendedCourseRecyclerView() {
        with(binding.recyclerViewCourseInfoRecommended) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendedCourseAdapter
        }
    }

    private fun setupRelatedCourseRecyclerView() {
        with(binding.recyclerViewCourseInfoRelated) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = relatedCourseAdapter
        }
    }
}