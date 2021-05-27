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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.adapter.course.chapter.assignment.CourseQuestionAdapter
import com.mobile.ta.adapter.course.chapter.assignment.CourseQuestionVHListener
import com.mobile.ta.adapter.diff.CourseQuestionDiffCallback
import com.mobile.ta.databinding.FragCourseAssignmentBinding
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.ui.main.MainActivity
import com.mobile.ta.viewmodel.course.chapter.assignment.CourseAssignmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseAssignmentFragment :
    BaseFragment<FragCourseAssignmentBinding>(FragCourseAssignmentBinding::inflate),
    CourseQuestionVHListener {
    private val viewmodel by viewModels<CourseAssignmentViewModel>()
    private lateinit var mMainActivity: MainActivity
    private var menuItems = mutableListOf<MenuItem>()
    private val args: CourseAssignmentFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
        setupDrawer()
    }

    private fun setupViewPager() {
        val adapter = CourseQuestionAdapter(CourseQuestionDiffCallback(), this)
        binding.fragCourseAssignmentVp.adapter = adapter
        viewmodel.questions.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.setQuestionType(viewmodel.chapter.type)
            binding.fragCourseAssignmentTitle.text = viewmodel.chapterTitle
            binding.fragCourseAssignmentContent.visibility = View.VISIBLE
            binding.fragCourseAssignmentLoading.visibility = View.GONE
        }
        viewmodel.navigateToSubmitResultPage.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(
                    CourseAssignmentFragmentDirections.actionCourseAssignmentFragmentToCourseSubmitFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.chapterId
                    )
                )
        }
        viewmodel.course.observe(viewLifecycleOwner, {
            setupMenu(it.chapterSummaryList)
        })
        viewmodel.userChapters.observe(viewLifecycleOwner, {
            viewmodel.course.value?.chapterSummaryList?.let {
                setupMenu(it)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivity = (mActivity as MainActivity)
    }

    private fun setupTabLayout() {
        TabLayoutMediator(
            binding.fragCourseAssignmentTab,
            binding.fragCourseAssignmentVp
        ) { tab, position ->
            tab.text = (position + 1).toString()
        }.attach()
    }

    private fun setupDrawer() {
        binding.apply {
            val toggle = ActionBarDrawerToggle(
                mMainActivity,
                fragCourseAssignmentDrawerLayout,
                mMainActivity.getToolbar(),
                R.string.open_drawer,
                R.string.close_drawer
            )
            fragCourseAssignmentDrawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
    }


    private fun setupMenu(chapters: List<ChapterSummary>) {
        binding.apply {
            fragCourseAssignmentDrawerNavigation.menu.apply {
                menuItems.clear()
                clear()
                chapters.forEachIndexed { index, it ->
                    add(it.title).isChecked = (args.chapterId == it.id)
                    menuItems.add(getItem(menuItems.count()).apply {
                        isEnabled = (viewmodel.userChapters.value?.size ?: 0 >= index)
                    })
                }
            }
            fragCourseAssignmentDrawerNavigation.setNavigationItemSelectedListener {
                chapters[menuItems.indexOf(it)].let { chapter ->
                    val destination =
                        getChapterDestination(chapter.id, chapter.type as ChapterType)
                    findNavController().navigate(destination)
                }
                fragCourseAssignmentDrawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }

    private fun getChapterDestination(chapterId: String, type: ChapterType): NavDirections {
        return when (type) {
            ChapterType.CONTENT -> CourseAssignmentFragmentDirections.actionCourseAssignmentFragmentToCourseContentFragment(
                args.courseId, chapterId
            )
            else -> CourseAssignmentFragmentDirections.actionCourseAssignmentFragmentSelf(
                args.courseId, chapterId
            )
        }
    }

    override fun onSubmitAnswerListener(
        assignmentQuestion: AssignmentQuestion,
        selectedIndex: Int
    ) {
        viewmodel.addSelectedAnswer(assignmentQuestion, selectedIndex)
    }

    override fun onShowResultListener() {
        viewmodel.submitAnswer()
        binding.fragCourseAssignmentLoading.visibility = View.VISIBLE
        binding.fragCourseAssignmentContent.visibility = View.GONE
    }
}