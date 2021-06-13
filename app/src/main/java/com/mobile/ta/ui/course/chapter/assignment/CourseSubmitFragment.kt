package com.mobile.ta.ui.course.chapter.assignment

import android.os.Bundle
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
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.ui.main.MainActivity
import com.mobile.ta.utils.ThemeUtil.getThemeColor
import com.mobile.ta.viewmodel.course.chapter.assignment.CourseSubmitViewModel
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
                findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.chapterId
                    )
                )
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
                chapters.forEachIndexed { index, it ->
                    add(it.title).isChecked = (viewmodel.chapterId == it.id)
                    menuItems.add(getItem(menuItems.count()).apply {
                        isEnabled = (viewmodel.userChapters.value?.size ?: 0 >= index)
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

    private fun getChapterDestination(chapterId: String, type: ChapterType): NavDirections {
        return when (type) {
            ChapterType.CONTENT -> CourseSubmitFragmentDirections
                .actionCourseSubmitFragmentToCourseContentFragment(
                    viewmodel.courseId, chapterId
                )
            else -> CourseSubmitFragmentDirections
                .actionCourseSubmitFragmentToCoursePracticeFragment(
                    viewmodel.courseId, chapterId
                )
        }
    }

    private fun setupPage() {
        viewmodel.submittedAssignment.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitLoading.visibility = View.GONE
            binding.fragCourseSubmitContent.visibility = View.VISIBLE
            binding.fragCourseSubmitTitle.text = viewmodel.chapter.title
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
            val nextChapterType = viewmodel.nextChapterSummary?.type ?: return@observe
            val nextChapterId = viewmodel.nextChapterSummary?.id ?: return@observe
            val destination = getChapterDestination(nextChapterId, nextChapterType)
            findNavController().navigate(destination)
        }
        viewmodel.userChapters.observe(viewLifecycleOwner, {
            viewmodel.course.value?.chapterSummaryList?.let { chapterSummary ->
                setupMenu(chapterSummary)
            }
        })
    }
}