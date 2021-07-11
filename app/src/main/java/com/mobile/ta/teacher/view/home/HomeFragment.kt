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
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.teacher.viewmodel.home.HomeViewModel
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.view.RVSeparator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class HomeFragment :
    BaseFragment<FragHomeBinding>(FragHomeBinding::inflate),
    View.OnClickListener,
    CourseVHListener {
    private val viewmodel by viewModels<HomeViewModel>()

    override fun runOnCreateView() {
        binding.fragHomeSearchBar.setOnClickListener(this)
        binding.fragHomeNotificationContainer.setOnClickListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fragHomeSearchBar -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
            binding.fragHomeNotificationContainer -> findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToNotificationFragment()
            )
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
        with(binding.fragHomeRv) {
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
                binding.fragHomeLoading.visibility = View.GONE
                binding.fragHomeRv.visibility = View.VISIBLE
                adapter.submitList(it)
            }
        })
    }
}