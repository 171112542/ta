package com.mobile.ta.teacher.view.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.teacher.adapter.course.CourseAdapter
import com.mobile.ta.teacher.adapter.course.CourseVHListener
import com.mobile.ta.teacher.adapter.diff.CourseDiffCallback
import com.mobile.ta.databinding.FragHomeBinding
import com.mobile.ta.databinding.TFragHomeBinding
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.teacher.viewmodel.home.HomeViewModel
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.view.RVSeparator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HomeFragment :
    BaseFragment<TFragHomeBinding>(TFragHomeBinding::inflate),
    View.OnClickListener,
    CourseVHListener {
    private val viewmodel by viewModels<HomeViewModel>()

    override fun runOnCreateView() {
        binding.tFragHomeSearchBar.setOnClickListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onClick(v: View) {
        when (v) {
            binding.tFragHomeSearchBar -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
        }
    }

    override fun onClickListener(courseId: String) {
        findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToCourseInformationFragment(
                courseId = courseId
            )
        )
    }

    private fun setupRecyclerView() {
        val diffCallback = CourseDiffCallback()
        val adapter = CourseAdapter(diffCallback, this)
        with(binding.tFragHomeRv) {
            this.adapter = adapter
            addItemDecoration(
                RVSeparator.getSpaceSeparator(
                    context,
                    LinearLayoutManager.VERTICAL,
                    resources
                )
            )
        }
        viewmodel.courseOverviews.observe(viewLifecycleOwner, {
            if (it.isNotNull()) {
                binding.tFragHomeLoading.visibility = View.GONE
                if (it.size == 0) {
                    binding.tFragHomeEmptyCourseText.visibility = View.VISIBLE
                } else {
                    binding.tFragHomeRv.visibility = View.VISIBLE
                }
                adapter.submitList(it)
            }
        })
    }
}