package com.mobile.ta.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.MainActivity
import com.mobile.ta.adapter.discussion.DiscussionForumAdapter
import com.mobile.ta.databinding.FragmentDiscussionForumBinding
import com.mobile.ta.viewmodel.discussion.DiscussionForumViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscussionForumFragment : Fragment(), View.OnClickListener {

    companion object {
        private const val CREATE_NEW_DISCUSSION_TAG = "CREATE NEW DISCUSSION"
    }

    private var _binding: FragmentDiscussionForumBinding? = null
    private val binding get() = _binding!!

    private val discussionForumAdapter by lazy {
        DiscussionForumAdapter(this::goToDiscussionDetail)
    }
    private val viewModel: DiscussionForumViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscussionForumBinding.inflate(inflater, container, false)
        with(binding) {
            buttonAddDiscussion.setOnClickListener(this@DiscussionForumFragment)
            with(recyclerViewDiscussions) {
                layoutManager = LinearLayoutManager(context)
                adapter = discussionForumAdapter
                setHasFixedSize(true)
            }
        }
        showLoading()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (activity as MainActivity).showToolbar()
        viewModel.fetchDiscussionForums()
        viewModel.discussionForums.observe(viewLifecycleOwner, {
            discussionForumAdapter.submitList(it)
            showResult()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                buttonAddDiscussion -> openCreateDiscussionBottomSheet()
            }
        }
    }

    private fun goToDiscussionDetail(discussionForumId: String) {
        findNavController().navigate(
            DiscussionForumFragmentDirections.actionDiscussionForumFragmentToDiscussionFragment(
                discussionForumId
            )
        )
    }

    private fun showLoading() {
        with(binding) {
            recyclerViewDiscussions.visibility = View.GONE
            progressBarDiscussionForumLoad.visibility = View.VISIBLE
        }
    }

    private fun openCreateDiscussionBottomSheet() {
        CreateDiscussionForumBottomSheetDialogFragment.newInstance(viewModel::createNewDiscussion)
            .show(parentFragmentManager, CREATE_NEW_DISCUSSION_TAG)
    }

    private fun showResult() {
        with(binding) {
            recyclerViewDiscussions.visibility = View.VISIBLE
            progressBarDiscussionForumLoad.visibility = View.GONE
        }
    }
}