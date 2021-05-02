package com.mobile.ta.ui.course.information

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
import com.mobile.ta.adapter.courseInfo.CourseInfoChapterAdapter
import com.mobile.ta.adapter.courseInfo.RelatedCourseAdapter
import com.mobile.ta.databinding.FragmentCourseInformationBinding
import com.mobile.ta.databinding.ItemSimpleTagChipBinding
import com.mobile.ta.model.course.information.Creator
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.ImageUtil
import com.mobile.ta.utils.getOrDefault
import com.mobile.ta.utils.getOrDefaultInt
import com.mobile.ta.utils.wrapper.status.StatusType
import com.mobile.ta.viewmodel.course.information.CourseInformationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseInformationFragment :
    BaseFragment<FragmentCourseInformationBinding>(FragmentCourseInformationBinding::inflate),
    View.OnClickListener {

    companion object {
        private const val COURSE_INFORMATION_TAG = "COURSE_INFORMATION"
    }

    private val args: CourseInformationFragmentArgs by navArgs()
    private val viewModel: CourseInformationViewModel by viewModels()

    private val courseContentAdapter by lazy {
        CourseInfoChapterAdapter(this::goToCourseContent)
    }
    private val prerequisiteCourseAdapter by lazy {
        RelatedCourseAdapter(
            this::goToOtherCourseInfo
        )
    }
    private val relatedCourseAdapter by lazy {
        RelatedCourseAdapter(
            this::goToOtherCourseInfo
        )
    }

    override fun runOnCreateView() {
        super.runOnCreateView()
        setupContentRecyclerView()
        setupPreqrequisiteCourseRecyclerView()
        setupRelatedCourseRecyclerView()
        viewModel.course.observe(viewLifecycleOwner, {
            if (it.status == StatusType.SUCCESS) {
                it?.data?.let { course ->
                    setupCourseMainInfo(
                        course.imageUrl,
                        course.title as String,
                        course.creator?.name.getOrDefault(mContext)
                    )
//                    setupCourseAboutInfo(
//                        course.description ?: "-",
//                        listOf(course)
//                    )
                    setupCreatorInfo(course.creator as Creator)
                    binding.apply {
                        course.prerequisiteCourse?.let {
                            if (it.isNotEmpty()) {
                                prerequisiteCourseAdapter.submitList(it)
                            }
                            courseInformationPrerequisiteCourseList.isVisible = it.isNotEmpty()
                            courseInformationPrerequisiteCourseEmpty.isVisible =
                                it.isEmpty()
                        }
                        course.relatedCourse?.let {
                            if (it.isNotEmpty()) {
                                relatedCourseAdapter.submitList(it)
                            }
                            courseInformationRelatedCourseList.isVisible = it.isNotEmpty()
                            courseInformationRelatedCourseEmpty.isVisible = it.isEmpty()
                        }
                    }
                }
            }
        })
        viewModel.chapters.observe(viewLifecycleOwner, {
            if (it.status == StatusType.SUCCESS) {
                courseContentAdapter.submitList(it.data)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCourse(args.courseId)
        viewModel.getChapters(args.courseId)
    }

    private fun createTagChip(index: Int, text: String, color: Int): Chip {
        val chipBinding = ItemSimpleTagChipBinding.inflate(
            layoutInflater,
            binding.courseInformationTagsGroup, false
        ).root
        chipBinding.apply {
            this.id = index
            this.text = text
            this.setChipBackgroundColorResource(color)
        }
        return chipBinding
    }

    private fun enrollCourse(enrollmentKey: String) {
        Snackbar.make(binding.root, "Course enrolled.", Snackbar.LENGTH_SHORT).show()
        findNavController().navigate(
            CourseInformationFragmentDirections.actionCourseInformationFragmentToCourseContentFragment()
        )
    }

    private fun getTagColor(index: Int): Int {
        val colors = arrayListOf(
            R.color.black,
            R.color.accent_green,
            R.color.accent_orange,
            R.color.accent_blue,
            R.color.accent_pink
        )
        return colors[index % colors.size]
    }

    private fun goToCourseContent(id: String) {
        findNavController().navigate(
            CourseInformationFragmentDirections.actionCourseInformationFragmentToCourseContentFragment()
        )
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

    private fun setupCourseMainInfo(image: String?, title: String, createdBy: String) {
        binding.apply {
            courseInformationTitle.text = title
            courseInformationCreatorName.text = createdBy
            image?.let {
                loadImage(it, courseInformationImage)
            }
        }
    }

    private fun setupCourseAboutInfo(about: String, tags: List<String>) {
        binding.apply {
            courseInformationAboutCreatorDescription.text = about
            courseInformationTagsGroup.apply {
                removeAllViews()
                tags.forEachIndexed { index, tag ->
                    addView(createTagChip(index, tag, getTagColor(index)))
                }
            }
        }
    }

    private fun setupCreatorInfo(creator: Creator) {
        binding.apply {
            courseInformationAboutCreatorName.text = creator.name
            courseInformationAboutCreatorDescription.text = creator.description
            courseInformationAboutCreatorEmail.text = creator.email
            courseInformationAboutCreatorTotalCourse.text = resources.getQuantityString(
                R.plurals.creator_course_count,
                creator.totalCourseCreated.getOrDefaultInt(),
                creator.totalCourseCreated.getOrDefaultInt()
            )
            creator.imageUrl?.let {
                loadImage(it, courseInformationAboutCreatorImage)
            }
        }
    }

    private fun setupContentRecyclerView() {
        binding.courseInformationContentList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = courseContentAdapter
        }
    }

    private fun setupPreqrequisiteCourseRecyclerView() {
        binding.courseInformationPrerequisiteCourseList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = prerequisiteCourseAdapter
        }
    }

    private fun setupRelatedCourseRecyclerView() {
        binding.courseInformationRelatedCourseList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = relatedCourseAdapter
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.course_information_enroll -> openInputEnrollmentKeyBottomSheet()
        }
    }
}