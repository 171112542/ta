package com.mobile.ta.ui.course.information

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
import com.mobile.ta.adapter.course.information.CourseInformationContentAdapter
import com.mobile.ta.adapter.course.information.RelatedCourseAdapter
import com.mobile.ta.databinding.FragmentCourseInformationBinding
import com.mobile.ta.databinding.ItemSimpleTagChipBinding
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.information.Creator
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.*
import com.mobile.ta.utils.view.ImageUtil
import com.mobile.ta.utils.wrapper.status.StatusType
import com.mobile.ta.view.DividerItemDecorator
import com.mobile.ta.viewmodel.course.information.CourseInformationViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CourseInformationFragment :
    BaseFragment<FragmentCourseInformationBinding>(FragmentCourseInformationBinding::inflate),
    View.OnClickListener {

    private val dateFormat = SimpleDateFormat("yy/dd/MM", Locale.ROOT)

    companion object {
        private const val COURSE_INFORMATION_TAG = "COURSE_INFORMATION"
    }

    private val args: CourseInformationFragmentArgs by navArgs()
    private val viewModel: CourseInformationViewModel by viewModels()

    private val courseContentAdapter by lazy {
        CourseInformationContentAdapter(::goToChapter, ::changeChapterProgress)
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
        setupPrerequisiteCourseRecyclerView()
        setupRelatedCourseRecyclerView()
        binding.apply {
            courseInformationEnroll.setOnClickListener(this@CourseInformationFragment)
            viewModel.course.observe(viewLifecycleOwner, {
                if (it.status == StatusType.SUCCESS) {
                    it?.data?.let { course ->
                        courseInformationTotalEnrolled.text = course.totalEnrolled.toString()
                        course.updatedAt?.let { updatedAt ->
                            courseInformationLastUpdated.text =
                                dateFormat.format(updatedAt.toDate())
                        }
                        setupCourseMainInfo(
                            course.imageUrl,
                            course.title,
                            course.creator.name.getOrDefault(mContext)
                        )
                        setupCourseAboutInfo(
                            course.description,
                            listOf(course.level.toString(), course.type.toString())
                        )
                        setupCreatorInfo(course.creator)
                    }
                }
                courseInformationProgressBarContainer.isVisible = false
            })
            viewModel.userCourse.observe(viewLifecycleOwner, {
                if (it.status == StatusType.SUCCESS) {
                    courseInformationEnroll.text =
                        if (it.data.isNull()) getString(R.string.enroll)
                        else getString(R.string.continue_studying)
                }
            })
            viewModel.preRequisiteCourses.observe(viewLifecycleOwner, { preRequisiteCourses ->
                if (preRequisiteCourses.isNotEmpty()) {
                    prerequisiteCourseAdapter.submitList(preRequisiteCourses)
                }
                courseInformationPrerequisiteCourseList.isVisible =
                    preRequisiteCourses.isNotEmpty()
                courseInformationPrerequisiteCourseEmpty.isVisible =
                    preRequisiteCourses.isEmpty()
            })
            viewModel.relatedCourses.observe(viewLifecycleOwner, { relatedCourses ->
                if (relatedCourses.isNotEmpty()) {
                    relatedCourseAdapter.submitList(relatedCourses)
                }
                courseInformationRelatedCourseList.isVisible =
                    relatedCourses.isNotEmpty()
                courseInformationRelatedCourseEmpty.isVisible = relatedCourses.isEmpty()
            })
        }
        viewModel.chapters.observe(viewLifecycleOwner, {
            if (it.status == StatusType.SUCCESS) {
                courseContentAdapter.submitList(it.data)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.getChapters(args.courseId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCourse(args.courseId)
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
        viewModel.enrollCourse(args.courseId, enrollmentKey)
        viewModel.enrollCourse.observe(viewLifecycleOwner, {
            if (it.status == StatusType.SUCCESS && it.data == true) {
                viewModel.getCourse(args.courseId)
                viewModel.getChapters(args.courseId)
                Snackbar.make(binding.root, "Course enrolled.", Snackbar.LENGTH_SHORT).show()
                viewModel.course.value?.data?.let { course ->
                    viewModel.incrementTotalEnrolled(
                        course
                    )
                }
            } else {
                Snackbar.make(
                    binding.root,
                    "Failed to enroll. ${it.message}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun changeChapterProgress(chapterId: String, textView: TextView) {
        viewModel.userChapters.observe(viewLifecycleOwner, {
            if (it.status == StatusType.SUCCESS) {
                textView.apply {
                    if (it.data?.find { chapter -> chapter.id == chapterId } != null) {
                        text = getString(R.string.completed_progress)
                        setTextColor(mContext.resolveColorAttr(R.attr.colorPrimary))
                        setCompoundDrawablesRelativeWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.ic_done_black_24dp,
                            0
                        )
                    } else {
                        text = getString(R.string.not_completed_progress)
                        setTextColor(ContextCompat.getColor(mContext, R.color.grey))
                        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    }
                    isVisible = true
                }
            }
        })
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

    private fun goToChapter(chapterId: String, type: ChapterType, position: Int) {
        viewModel.userCourse.value?.data?.let {
            viewModel.userChapters.value?.data?.let { userChapters ->
                if (userChapters.size >= position) {
                    findNavController().navigate(
                        getChapterDestination(chapterId, type)
                    )
                } else {
                    Toast.makeText(
                        mContext,
                        getString(R.string.require_previous_chapter),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun getChapterDestination(chapterId: String, type: ChapterType): NavDirections {
        return when (type) {
            ChapterType.CONTENT -> CourseInformationFragmentDirections.actionCourseInformationFragmentToCourseContentFragment(
                args.courseId, chapterId
            )
            else -> CourseInformationFragmentDirections.actionCourseInformationFragmentToCoursePracticeFragment(
                args.courseId, chapterId
            )
        }
    }

    private fun goToOtherCourseInfo(id: String) {
        findNavController().navigate(
            CourseInformationFragmentDirections.actionCourseInformationFragmentSelf(
                id
            )
        )
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
                ImageUtil.loadImage(mContext, it, courseInformationImage)
            }
        }
    }

    private fun setupCourseAboutInfo(about: String, tags: List<String>) {
        binding.apply {
            courseInformationAboutCourse.text = about
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
                ImageUtil.loadImage(mContext, it, courseInformationAboutCreatorImage)
            }
        }
    }

    private fun setupContentRecyclerView() {
        binding.courseInformationContentList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = courseContentAdapter
            addItemDecoration(
                DividerItemDecorator(
                    context,
                    LinearLayoutManager.HORIZONTAL
                ).apply {
                    setDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.drawable_dashed_item_decoration,
                            null
                        )!!
                    )
                })
        }
    }

    private fun setupPrerequisiteCourseRecyclerView() {
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
            R.id.course_information_enroll -> {
                viewModel.userCourse.value?.let {
                    if (it.data.isNull()) {
                        openInputEnrollmentKeyBottomSheet()
                    } else {
                        val lastAccessedCourse = it.data?.lastAccessedChapter as ChapterSummary
                        if (lastAccessedCourse.type == ChapterType.CONTENT) {
                            findNavController().navigate(
                                CourseInformationFragmentDirections.actionCourseInformationFragmentToCourseContentFragment(
                                    args.courseId, lastAccessedCourse.id
                                )
                            )
                        } else {
                            findNavController().navigate(
                                CourseInformationFragmentDirections.actionCourseInformationFragmentToCoursePracticeFragment(
                                    args.courseId, lastAccessedCourse.id
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}