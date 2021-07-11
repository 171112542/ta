package com.mobile.ta.teacher.view.course.information

import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.mobile.ta.R
import com.mobile.ta.databinding.FragmentCourseInformationBinding
import com.mobile.ta.databinding.ItemSimpleTagChipBinding
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.user.User
import com.mobile.ta.teacher.adapter.course.information.CourseInformationContentAdapter
import com.mobile.ta.teacher.adapter.course.information.RelatedCourseAdapter
import com.mobile.ta.teacher.viewmodel.course.information.CourseInformationViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.*
import com.mobile.ta.utils.view.DividerItemDecorator
import com.mobile.ta.utils.view.ImageUtil
import com.mobile.ta.utils.wrapper.status.StatusType
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CourseInformationFragment :
    BaseFragment<FragmentCourseInformationBinding>(FragmentCourseInformationBinding::inflate),
    View.OnClickListener {

    private val dateFormat = SimpleDateFormat("yy/dd/MM", Locale.ROOT)

    private val args: CourseInformationFragmentArgs by navArgs()
    private val viewModel: CourseInformationViewModel by viewModels()

    private val courseContentAdapter by lazy {
        CourseInformationContentAdapter(::goToChapter)
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
            courseInformationEnroll.apply {
                text = getString(R.string.view_student_result_label)
                setOnClickListener(this@CourseInformationFragment)
            }
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
                            course.title
                        )
                        setupCourseAboutInfo(
                            course.description,
                            listOf(course.level.toString(), course.type.toString())
                        )
                        courseContentAdapter.submitList(course.chapterSummaryList)
                    }
                }
                courseInformationProgressBarContainer.isVisible = false
            })
            viewModel.prerequisiteCourses.observe(viewLifecycleOwner, { prerequisiteCourses ->
                if (prerequisiteCourses.isNotEmpty()) {
                    prerequisiteCourseAdapter.submitList(prerequisiteCourses)
                }
                courseInformationPrerequisiteCourseList.isVisible =
                    prerequisiteCourses.isNotEmpty()
                courseInformationPrerequisiteCourseEmpty.isVisible =
                    prerequisiteCourses.isEmpty()
            })
            viewModel.creator.observe(viewLifecycleOwner, {
                setupCreatorInfo(it)
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

    private fun goToChapter(chapterId: String, type: ChapterType) {
        findNavController().navigate(
            getChapterDestination(chapterId, type)
        )
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

    private fun setupCourseMainInfo(image: String?, title: String) {
        binding.apply {
            courseInformationTitle.text = title
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

    private fun setupCreatorInfo(creator: User) {
        binding.apply {
            courseInformationCreatorName.text = creator.name
            courseInformationAboutCreatorName.text = creator.name
            courseInformationAboutCreatorDescription.text = creator.bio
            courseInformationAboutCreatorEmail.text = creator.email
            courseInformationAboutCreatorTotalCourse.text = resources.getQuantityString(
                R.plurals.creator_course_count,
                creator.totalCourseCreated.getOrDefaultInt(),
                creator.totalCourseCreated.getOrDefaultInt()
            )
            creator.photo?.let {
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
                //TODO: Navigate to student result
            }
        }
    }
}