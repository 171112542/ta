package com.mobile.ta.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.MainActivity
import com.mobile.ta.R
import com.mobile.ta.adapter.CourseOverviewAdapter
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.databinding.FragHomeBinding
import com.mobile.ta.viewmodel.HomeViewModel

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).hideToolbar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.frag_home_search_bar -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
        }
    }

    private fun setupRecyclerView() {
        val diffCallback = CourseOverviewDiffCallback()
        val adapter = CourseOverviewAdapter(diffCallback)
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
            adapter.submitList(it.toMutableList())
        })
    }
}