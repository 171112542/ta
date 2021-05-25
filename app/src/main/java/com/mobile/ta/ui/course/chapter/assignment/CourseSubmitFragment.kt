package com.mobile.ta.ui.course.chapter.assignment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mobile.ta.R
import com.mobile.ta.databinding.FragCourseSubmitBinding
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.ui.main.MainActivity
import com.mobile.ta.viewmodel.course.chapter.assignment.CourseSubmitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseSubmitFragment :
    BaseFragment<FragCourseSubmitBinding>(FragCourseSubmitBinding::inflate) {
    private val viewmodel by viewModels<CourseSubmitViewModel>()
    private lateinit var mMainActivity: MainActivity
    private var menuItems = mutableListOf<MenuItem>()
    private val args: CourseSubmitFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPage()
        setupDrawer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivity = (mActivity as MainActivity)
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
        }
    }


    private fun setupMenu(chapters: List<ChapterSummary>) {
        binding.apply {
            fragCourseSubmitDrawerNavigation.menu.apply {
                menuItems.clear()
                clear()
                chapters.forEachIndexed { index, it ->
                    add(it.title).isChecked = (args.chapterId == it.id)
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
            ChapterType.CONTENT -> CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCourseContentFragment(
                args.courseId, chapterId
            )
            else -> CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                args.courseId, chapterId
            )
        }
    }

    private fun setupPage() {
        viewmodel.submittedAssignment.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitLoading.visibility = View.GONE
            binding.fragCourseSubmitContent.visibility = View.VISIBLE
            val totalAnswerCount = it.totalAnswerCount
            val correctAnswerCount = it.correctAnswerCount
            binding.fragCourseSubmitTitle.text = viewmodel.chapterTitle
            binding.fragCourseSubmitScore.text =
                getString(R.string.assignment_result_text, correctAnswerCount, totalAnswerCount)
        }
        viewmodel.showRetryButton.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitRetry.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewmodel.showNextChapterButton.observe(viewLifecycleOwner) {
            binding.fragCourseSubmitNextChapter.visibility = if (it) View.VISIBLE else View.GONE
        }
        binding.fragCourseSubmitRetry.setOnClickListener {
            viewmodel.retry()
            findNavController().navigate(
                CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                    courseId = viewmodel.courseId,
                    chapterId = viewmodel.chapterId
                )
            )
        }
        binding.fragCourseSubmitNextChapter.setOnClickListener {
            viewmodel.navigateToNextChapter()
        }
        viewmodel.navigateToNextChapter.observe(viewLifecycleOwner) {
            if (!it) return@observe
            val nextChapterType = viewmodel.nextChapterSummary?.type ?: return@observe
            val nextChapterId = viewmodel.nextChapterSummary?.id ?: return@observe
            when (nextChapterType) {
                ChapterType.PRACTICE -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = nextChapterId
                    )
                )
                ChapterType.QUIZ -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCoursePracticeFragment(
                        courseId = viewmodel.courseId,
                        chapterId = nextChapterId
                    )
                )
                ChapterType.CONTENT -> findNavController().navigate(
                    CourseSubmitFragmentDirections.actionCourseSubmitFragmentToCourseContentFragment(
                        courseId = viewmodel.courseId,
                        chapterId = nextChapterId
                    )
                )
            }
        }
        viewmodel.course.observe(viewLifecycleOwner, {
            setupMenu(it.chapterSummaryList)
        })
    }
}