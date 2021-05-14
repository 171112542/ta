package com.mobile.ta.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.adapter.course.CourseAdapter
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.databinding.FragHomeBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.RVSeparator
import com.mobile.ta.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    BaseFragment<FragHomeBinding>(FragHomeBinding::inflate),
    View.OnClickListener {
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

    private fun setupRecyclerView() {
        val diffCallback = CourseOverviewDiffCallback()
        val adapter = CourseAdapter(diffCallback)
        adapter.setParentFragment(this)
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
            adapter.submitList(it)
        })
    }
}