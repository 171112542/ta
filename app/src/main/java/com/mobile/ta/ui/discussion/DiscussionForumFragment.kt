package com.mobile.ta.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
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
    private val viewModel by viewModels<DiscussionForumViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscussionForumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        setupViews()
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

    private fun setupObserver() {
        viewModel.fetchDiscussionForums()
        viewModel.discussionForums.observe(viewLifecycleOwner, {
            it.data?.let { data ->
                discussionForumAdapter.submitList(data)
                showResult()
            }
        })
        viewModel.isForumAdded.observe(viewLifecycleOwner, {
            if (it) {
                showSuccessAddForumToast()
                viewModel.fetchDiscussionForums()
            }
            viewModel.setIsForumAdded()
        })
    }

    private fun setupViews() {
        with(binding) {
            buttonAddDiscussion.setOnClickListener(this@DiscussionForumFragment)
            with(recyclerViewDiscussions) {
                layoutManager = LinearLayoutManager(context)
                adapter = discussionForumAdapter
                setHasFixedSize(true)
            }
        }
        showLoading()
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

    private fun showSuccessAddForumToast() {
        Snackbar.make(
            binding.root,
            getString(R.string.success_add_forum_message),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}