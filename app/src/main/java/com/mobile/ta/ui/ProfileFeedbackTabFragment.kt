package com.mobile.ta.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.R
import com.mobile.ta.adapter.ProfileFeedbackAdapter
import com.mobile.ta.adapter.diff.ProfileFeedbackDiffCallback
import com.mobile.ta.databinding.FragmentProfileFeedbackTabBinding
import com.mobile.ta.viewmodel.profile.ProfileFeedbackTabViewModel

class ProfileFeedbackTabFragment : Fragment() {
    private lateinit var binding: FragmentProfileFeedbackTabBinding
    private val viewModel: ProfileFeedbackTabViewModel by viewModels()
    lateinit var mContext: Context
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileFeedbackTabBinding.inflate(inflater, container, false)
        mContext = requireContext()
        binding.apply {
            val diffCallback = ProfileFeedbackDiffCallback()
            val adapter = ProfileFeedbackAdapter(diffCallback)
            val layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
            profileTabRecyclerView.adapter = adapter
            profileTabRecyclerView.layoutManager = layoutManager
            val itemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
                setDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.drawable_space_item_decoration,
                        null
                    )!!
                )
            }
            profileTabRecyclerView.addItemDecoration(itemDecoration)
            viewModel.feedbacks.observe(viewLifecycleOwner, Observer {
                adapter.submitList(it)
            })
        }
        return binding.root
    }
}