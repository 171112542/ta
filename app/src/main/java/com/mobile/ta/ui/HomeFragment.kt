package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mobile.ta.R
import com.mobile.ta.adapter.CourseOverviewAdapter
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.data.CourseOverviewData
import com.mobile.ta.databinding.FragHomeBinding

class HomeFragment :
    Fragment(),
    View.OnClickListener {
    private var _binding: FragHomeBinding? = null
    private val binding get() = _binding as FragHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragHomeBinding.inflate(inflater, container, false)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.frag_home_search_bar -> HomeFragmentDirections.actionHomeFragmentToSearchFragment()
        }
    }

    private fun setupRecyclerView() {
        val diffCallback = CourseOverviewDiffCallback()
        val adapter = CourseOverviewAdapter(diffCallback)
        adapter.submitList(CourseOverviewData.data.toMutableList())
        binding.fragHomeRv.adapter = adapter
    }
}