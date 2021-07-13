package com.mobile.ta.teacher.view.course.chapter.assignment

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.ta.R
import com.mobile.ta.databinding.FragCourseAssignmentBinding
import com.mobile.ta.databinding.TFragCourseAssignmentBinding
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.model.course.chapter.assignment.AssignmentQuestion
import com.mobile.ta.student.view.main.MainActivity
import com.mobile.ta.teacher.adapter.course.chapter.assignment.CourseQuestionAdapter
import com.mobile.ta.teacher.view.home.HomeFragmentDirections
import com.mobile.ta.teacher.view.main.TeacherMainActivity
import com.mobile.ta.teacher.viewmodel.course.chapter.assignment.CourseAssignmentViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.view.RVSeparator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CourseAssignmentFragment :
    BaseFragment<TFragCourseAssignmentBinding>(TFragCourseAssignmentBinding::inflate),
    View.OnClickListener {
    @Inject
    lateinit var courseQuestionAdapter: CourseQuestionAdapter
    private val viewmodel by viewModels<CourseAssignmentViewModel>()
    private lateinit var mMainActivity: TeacherMainActivity
    private var menuItems = mutableListOf<MenuItem>()
    private val args: CourseAssignmentFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupDrawer()
        setupNavigation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mMainActivity = (mActivity as TeacherMainActivity)
    }

    override fun onClick(v: View) {
        when (v) {
            binding.tFragCourseAssignmentBack -> {
                val actions = viewmodel.previousChapterSummary.value?.let {
                    getChapterDestination(
                        it.id,
                        it.type
                    )
                }
                actions?.let {
                    findNavController().navigate(it)
                }
            }
            binding.tFragCourseAssignmentNext -> {
                val actions = viewmodel.nextChapterSummary.value?.let {
                    getChapterDestination(
                        it.id,
                        it.type
                    )
                }
                if (actions != null) findNavController().navigate(actions)
                else findNavController().navigate(
                    CourseAssignmentFragmentDirections
                        .actionCourseAssignmentFragmentToCourseInformationFragment(
                            viewmodel.courseId
                        )
                )
            }
        }
    }

    private fun setupRecyclerView() {
        binding.tFragCourseAssignmentRv.adapter = courseQuestionAdapter
        binding.tFragCourseAssignmentRv.addItemDecoration(
            RVSeparator.getSpaceSeparator(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                resources
            )
        )
        viewmodel.assignment.observe(viewLifecycleOwner, {
            mMainActivity.supportActionBar?.title =
                if (it.type == ChapterType.PRACTICE)
                    getString(R.string.practice_fragment_toolbar_text)
                else
                    getString(R.string.quiz_fragment_toolbar_text)
            binding.tFragCourseAssignmentTitle.text = it.title
            courseQuestionAdapter.submitList(it.questions)
            binding.tFragCourseAssignmentLoading.visibility = View.GONE
            binding.tFragCourseAssignmentContent.visibility = View.VISIBLE
        })
    }

    private fun setupDrawer() {
        viewmodel.course.observe(viewLifecycleOwner, {
            setupMenu(it.chapterSummaryList)
        })
        binding.apply {
            val toggle = ActionBarDrawerToggle(
                mMainActivity,
                tFragCourseAssignmentDrawerLayout,
                mMainActivity.getToolbar(),
                R.string.open_drawer,
                R.string.close_drawer
            )
            tFragCourseAssignmentDrawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }
    }


    private fun setupMenu(chapters: List<ChapterSummary>) {
        binding.apply {
            tFragCourseAssignmentDrawerNavigation.menu.apply {
                menuItems.clear()
                clear()
                chapters.forEach {
                    add(it.title).isChecked = (args.chapterId == it.id)
                    menuItems.add(getItem(menuItems.count()))
                }
            }
            tFragCourseAssignmentDrawerNavigation.setNavigationItemSelectedListener {
                chapters[menuItems.indexOf(it)].let { chapter ->
                    val destination =
                        getChapterDestination(chapter.id, chapter.type as ChapterType)
                    findNavController().navigate(destination)
                }
                tFragCourseAssignmentDrawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }
    private fun setupNavigation() {
        viewmodel.nextChapterSummary.observe(viewLifecycleOwner, {
            binding.tFragCourseAssignmentNextGroup.visibility = View.VISIBLE
            binding.tFragCourseAssignmentNextText.text = it?.title ?: getString(R.string.finish_course_text)
            binding.tFragCourseAssignmentNext.setOnClickListener(this)
        })
        viewmodel.previousChapterSummary.observe(viewLifecycleOwner, {
            if (it == null) return@observe
            binding.tFragCourseAssignmentBackGroup.visibility = View.VISIBLE
            binding.tFragCourseAssignmentBackText.text = it.title
            binding.tFragCourseAssignmentBack.setOnClickListener(this)
        })
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
}