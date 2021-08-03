package com.mobile.ta.student.view.course.chapter.assignment

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.mobile.ta.R
import com.mobile.ta.databinding.FragCourseSubmitBinding
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.student.view.main.MainActivity
import com.mobile.ta.student.viewmodel.course.chapter.assignment.CourseSubmitViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.ThemeUtil.getThemeColor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseSubmitFragment :
    BaseFragment<FragCourseSubmitBinding>(FragCourseSubmitBinding::inflate),
    View.OnClickListener {
    private val viewmodel by viewModels<CourseSubmitViewModel>()
    private lateinit var mMainActivity: MainActivity
    private var menuItems = mutableListOf<MenuItem>()

    override fun runOnCreateView() {
        changeToolbarColors(R.attr.colorPrimary, R.attr.colorOnPrimary)
        binding.fragCourseSubmitRetry.setOnClickListener(this)
        binding.fragCourseSubmitNextChapter.setOnClickListener(this)
        binding.fragCourseSubmitFinishCourse.setOnClickListener(this)
    }

    override fun runOnCreate() {
        mMainActivity = mActivity as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPage()
        setupDrawer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        changeToolbarColors(R.attr.colorOnPrimary, R.attr.colorOnBackground)
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fragCourseSubmitRetry -> {
                viewmodel.retry()
            }
            binding.fragCourseSubmitNextChapter -> {
                viewmodel.navigateToNextChapter()
            }
            binding.fragCourseSubmitFinishCourse -> {
                findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCourseInformationFragment(
                        courseId = viewmodel.courseId
                    )
                )
            }
        }
    }

    private fun changeToolbarColors(backgroundResId: Int, componentsResId: Int) {
        mMainActivity.getToolbar().let {
            it.setBackgroundColor(
                getThemeColor(this.requireContext(), backgroundResId)
            )
            it.setTitleTextColor(
                getThemeColor(this.requireContext(), componentsResId)
            )
            it.navigationIcon!!.setTint(
                getThemeColor(this.requireContext(), R.attr.colorOnPrimary)
            )
        }
    }

    private fun setupDrawer() {
        binding.apply {
            val toggle = ActionBarDrawerToggle(
                mMainActivity,
                fragCourseSubmitDrawerLayout,
                mMainActivity.getToolbar(),
                R.string.open_drawer,
                R.string.close_drawer
            )
            fragCourseSubmitDrawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            toggle.drawerArrowDrawable.color =
                getThemeColor(requireContext(), R.attr.colorOnPrimary)
        }
    }

    private fun setupMenu(chapters: List<ChapterSummary>) {
        binding.apply {
            fragCourseSubmitDrawerNavigation.menu.apply {
                menuItems.clear()
                clear()
                val finishedChapterIds = viewmodel.studentProgress.value?.finishedChapterIds
                val lastFinishedChapterIndex = if (finishedChapterIds.isNullOrEmpty()) -1
                else {
                    finishedChapterIds.maxOf { current ->
                        chapters.indexOfFirst { it.id == current }
                    }
                }
                chapters.forEachIndexed { index, it ->
                    add(it.title).isChecked = (viewmodel.assignmentId == it.id)
                    menuItems.add(getItem(menuItems.count()).apply {
                        isEnabled = (lastFinishedChapterIndex + 1 >= index)
                    })
                }
            }
            fragCourseSubmitDrawerNavigation.setNavigationItemSelectedListener {
                chapters[menuItems.indexOf(it)].let { chapter ->
                    val destination =
                        getChapterDestination(chapter.id, chapter.type as ChapterType)
                    findNavController().navigate(destination)
                }
                fragCourseSubmitDrawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun getChapterDestination(
        chapterId: String,
        type: ChapterType,
        isRetryPractice: Boolean = false
    ): NavDirections {
        return when (type) {
            ChapterType.CONTENT -> CourseSubmitFragmentDirections
                .actionCourseSubmitFragmentToCourseContentFragment(
                    viewmodel.courseId, chapterId
                )
            else -> CourseSubmitFragmentDirections
                .actionCourseSubmitFragmentToCoursePracticeFragment(
                    viewmodel.courseId, chapterId, isRetryPractice
                )
        }
    }

    private fun setupPage() {
        viewmodel.studentAssignmentResult.observe(viewLifecycleOwner) {
            mMainActivity.supportActionBar?.title =
                if (it.type == ChapterType.PRACTICE)
                    getString(R.string.practice_result_fragment_toolbar_text)
                else
                    getString(R.string.quiz_result_fragment_toolbar_text)
            binding.fragCourseSubmitLoading.visibility = View.GONE
            binding.fragCourseSubmitContent.visibility = View.VISIBLE
            binding.fragCourseSubmitTitle.text = it.title
            binding.fragCourseSubmitScore.text = it.score.toString()
            binding.fragCourseSubmitPassingText.text =
                getString(
                    R.string.passing_grade_desc_text,
                    it.passingGrade
                )
        }
        viewmodel.showPassingGradeText.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitPassingText.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewmodel.showPassingGradeAndNextRetryText.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitPassingNextRetryText.visibility = if (it.first) View.VISIBLE else View.GONE
            binding.fragCourseSubmitPassingText.text =
                getString(
                    R.string.passing_grade_next_retry_desc_text,
                    60.toString(),
                    it.second
                )
        }
        viewmodel.showRetryButton.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitRetry.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewmodel.showFinishCourseButton.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitFinishCourse.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewmodel.showNextChapterButton.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitNextChapter.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewmodel.navigateToNextChapter.observe(viewLifecycleOwner) {
            if (!it) return@observe
            val nextChapterType = viewmodel.nextChapterSummary.value?.type ?: return@observe
            val nextChapterId = viewmodel.nextChapterSummary.value?.id ?: return@observe
            val destination = getChapterDestination(nextChapterId, nextChapterType)
            findNavController().navigate(destination)
        }
        viewmodel.navigateToRetryPractice.observe(viewLifecycleOwner) {
            if (!it) return@observe
            val destination = getChapterDestination(
                viewmodel.assignmentId,
                ChapterType.PRACTICE,
                isRetryPractice = true
            )
            findNavController().navigate(destination)
        }
        viewmodel.studentProgress.observe(viewLifecycleOwner, {
            viewmodel.course.value?.chapterSummaryList?.let { chapterSummary ->
                setupMenu(chapterSummary)
            }
        })
    }
}