package com.mobile.ta.ui.course

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.UserCourseDiffCallback
import com.mobile.ta.adapter.user.course.UserCourseAdapter
import com.mobile.ta.databinding.FragmentCourseTabBinding
import com.mobile.ta.viewmodel.course.chapter.content.CourseTabViewModel

class CourseTabFragment : Fragment() {
    private lateinit var binding: FragmentCourseTabBinding
    private val viewModel: CourseTabViewModel by viewModels()
    private val fragmentType by lazy {
        if (this.arguments?.get("position") == ONGOING_TAB) ONGOING_TAB else FINISHED_TAB
    }
    private lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCourseTabBinding.inflate(inflater, container, false)
        mContext = requireContext()
        setupRecyclerView()
        return binding.root
    }

    private fun setupRecyclerView() {
        val adapter = UserCourseAdapter(
            UserCourseDiffCallback()
        )

        val itemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
            setDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.drawable_space_item_decoration,
                    null
                )!!
            )
        }
        val layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        binding.apply {
            courseTabRecyclerView.addItemDecoration(itemDecoration)
            courseTabRecyclerView.adapter = adapter
            courseTabRecyclerView.layoutManager = layoutManager
            when (fragmentType) {
                ONGOING_TAB -> {
                    viewModel.userOngoingCourse.observe(viewLifecycleOwner, {
                        courseTabNoData.visibility =
                            if (it.count() == 0) View.VISIBLE else View.GONE
                        adapter.submitList(it)
                    })
                }
                FINISHED_TAB -> {
                    viewModel.userFinishedCourse.observe(viewLifecycleOwner, {
                        courseTabNoData.visibility =
                            if (it.count() == 0) View.VISIBLE else View.GONE
                        adapter.submitList(it)
                    })
                }
            }
        }
    }

    companion object {
        const val ONGOING_TAB = 0
        const val FINISHED_TAB = 1
    }
}