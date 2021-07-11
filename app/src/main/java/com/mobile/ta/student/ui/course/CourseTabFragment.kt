package com.mobile.ta.student.ui.course

import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.R
import com.mobile.ta.student.adapter.course.CoursePagerAdapter.Companion.EXTRA_POSITION
import com.mobile.ta.student.adapter.user.course.StudentProgressAdapter
import com.mobile.ta.databinding.FragmentCourseTabBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.student.ui.course.CourseTabFragment.CourseTabType.ONGOING_TAB
import com.mobile.ta.utils.wrapper.status.StatusType
import com.mobile.ta.student.viewmodel.course.chapter.content.CourseTabViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CourseTabFragment :
    BaseFragment<FragmentCourseTabBinding>(FragmentCourseTabBinding::inflate) {
    enum class CourseTabType {
        ONGOING_TAB, FINISHED_TAB
    }

    private val viewModel: CourseTabViewModel by viewModels()
    private val userCourseAdapter by lazy {
        StudentProgressAdapter(::navigateToCourseInformation)
    }

    override fun runOnCreateView() {
        super.runOnCreateView()
        setupRecyclerView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val type =
            if ((arguments?.getInt(EXTRA_POSITION)
                    ?: 0) == 0
            ) ONGOING_TAB else CourseTabType.FINISHED_TAB
        viewModel.getUserCourse(type)
    }

    private fun setupRecyclerView() {
        val itemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
            setDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.drawable_space_item_decoration,
                    null
                )!!
            )
        }
        binding.apply {
            val layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            courseTabRecyclerView.addItemDecoration(itemDecoration)
            courseTabRecyclerView.adapter = userCourseAdapter
            courseTabRecyclerView.layoutManager = layoutManager
            viewModel.studentProgress.observe(viewLifecycleOwner, {
                if (it.status == StatusType.SUCCESS) {
                    courseTabNoData.isVisible = it.data?.count() ?: 0 == 0
                    courseTabRecyclerView.isVisible = it.data?.count() ?: 0 != 0
                    userCourseAdapter.submitList(it.data)
                }
                courseTabProgressBarContainer.isVisible = false
            })
        }
    }

    private fun navigateToCourseInformation(courseId: String) {
        findNavController().navigate(
            MyCourseFragmentDirections.actionMyCourseFragmentToCourseInformationFragment(
                courseId
            )
        )
    }
}