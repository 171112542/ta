package com.mobile.ta.student.view.course.chapter.assignment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.databinding.FragCourseAssignmentBinding
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.student.adapter.course.chapter.assignment.CourseQuestionAdapter
import com.mobile.ta.student.adapter.course.chapter.assignment.CourseQuestionVHListener
import com.mobile.ta.student.view.main.MainActivity
import com.mobile.ta.student.viewmodel.course.chapter.assignment.CourseAssignmentViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseAssignmentFragment :
    BaseFragment<FragCourseAssignmentBinding>(FragCourseAssignmentBinding::inflate),
    CourseQuestionVHListener {
    @Inject
    lateinit var courseQuestionAdapter: CourseQuestionAdapter

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
        courseQuestionAdapter.setVhClickListener(this)
        binding.fragCourseAssignmentVp.adapter = courseQuestionAdapter
        viewmodel.assignment.observe(viewLifecycleOwner) {
            mActivity.supportActionBar?.title =
                if (it.type == ChapterType.PRACTICE)
                    getString(R.string.practice_fragment_toolbar_text)
                else
                    getString(R.string.quiz_fragment_toolbar_text)
            courseQuestionAdapter.submitList(it.questions.toMutableList())
            courseQuestionAdapter.setQuestionType(it.type)
            binding.fragCourseAssignmentTitle.text = it.title
            binding.fragCourseAssignmentContent.visibility = View.VISIBLE
            binding.fragCourseAssignmentLoading.visibility = View.GONE
        }
        viewmodel.navigateToSubmitResultPage.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(
                    CourseAssignmentFragmentDirections.actionCourseAssignmentFragmentToCourseSubmitFragment(
                        courseId = viewmodel.courseId,
                        chapterId = viewmodel.assignmentId
                    )
                )
        }
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
        viewmodel.course.observe(viewLifecycleOwner, {
            setupMenu(it.chapterSummaryList)
        })
        viewmodel.studentProgress.observe(viewLifecycleOwner, {
            viewmodel.course.value?.chapterSummaryList?.let {
                setupMenu(it)
            }
        })
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
                val finishedChapterIds = viewmodel.studentProgress.value?.finishedChapterIds
                val lastFinishedChapterIndex = if (finishedChapterIds.isNullOrEmpty()) -1
                else {
                    finishedChapterIds.maxOf { current ->
                        chapters.indexOfFirst { it.id == current }
                    }
                }
                chapters.forEachIndexed { index, it ->
                    add(it.title).isChecked = (args.chapterId == it.id)
                    menuItems.add(getItem(menuItems.count()).apply {
                        isEnabled = (lastFinishedChapterIndex + 1 >= index)
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