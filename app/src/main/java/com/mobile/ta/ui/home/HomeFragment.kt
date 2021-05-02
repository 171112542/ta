package com.mobile.ta.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.adapter.CourseAdapter
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.databinding.FragHomeBinding
import com.mobile.ta.model.status.StatusType
import com.mobile.ta.ui.RVSeparator
import com.mobile.ta.viewmodel.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment :
    Fragment(),
    View.OnClickListener {
    private var _binding: FragHomeBinding? = null
    private val binding get() = _binding as FragHomeBinding
    private val viewmodel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragHomeBinding.inflate(inflater, container, false)
        binding.fragHomeSearchBar.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View) {
        when (v) {
            binding.fragHomeSearchBar -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
            binding.fragHomeNotificationContainer -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToNotificationFragment())
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