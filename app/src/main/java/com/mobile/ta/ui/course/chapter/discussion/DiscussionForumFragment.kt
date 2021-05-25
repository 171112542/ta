package com.mobile.ta.ui.course.chapter.discussion

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
import com.mobile.ta.adapter.course.chapter.discussion.DiscussionForumAdapter
import com.mobile.ta.databinding.FragmentDiscussionForumBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.viewmodel.course.chapter.discussion.DiscussionForumViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscussionForumFragment :
    BaseFragment<FragmentDiscussionForumBinding>(FragmentDiscussionForumBinding::inflate),
    View.OnClickListener {

    companion object {
        private const val CREATE_NEW_DISCUSSION_TAG = "CREATE NEW DISCUSSION"
    }

    private val args: DiscussionForumFragmentArgs by navArgs()

    private val discussionForumAdapter by lazy {
        DiscussionForumAdapter(this::goToDiscussionDetail)
    }
    private val viewModel by viewModels<DiscussionForumViewModel>()

    override fun runOnCreateView() {
        binding.apply {
            buttonAddDiscussion.setOnClickListener(this@DiscussionForumFragment)
            with(recyclerViewDiscussions) {
                layoutManager = LinearLayoutManager(context)
                adapter = discussionForumAdapter
                setHasFixedSize(true)
            }
        }
        setupObserver()
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                buttonAddDiscussion -> openCreateDiscussionBottomSheet()
            }
        }
    }

    private fun setupObserver() {
        viewModel.setCourseAndChapterId(args.courseId, args.chapterId)
        fetchDiscussionForums()
        viewModel.discussionForums.observe(viewLifecycleOwner, {
            it?.let { data ->
                discussionForumAdapter.submitList(data)
                scrollToTop()
                showLoadingState(false)
            }
        })
        viewModel.isForumAdded.observe(viewLifecycleOwner, {
            it?.let { isForumAdded ->
                if (isForumAdded) {
                    showSuccessAddForumToast()
                    fetchDiscussionForums()
                }
                viewModel.setIsForumAdded(false)
            }
        })
    }

    private fun fetchDiscussionForums() {
        showLoadingState(true)
        viewModel.fetchDiscussionForums()
    }

    private fun goToDiscussionDetail(discussionForumId: String) {
        val courseAndChapterId = viewModel.getCourseAndChapterId()
        findNavController().navigate(
            DiscussionForumFragmentDirections.actionDiscussionForumFragmentToDiscussionFragment(
                courseAndChapterId.first, courseAndChapterId.second, discussionForumId
            )
        )
    }

    private fun openCreateDiscussionBottomSheet() {
        CreateDiscussionForumBottomSheetDialogFragment.newInstance(viewModel::createNewDiscussion)
            .show(parentFragmentManager, CREATE_NEW_DISCUSSION_TAG)
    }

    private fun scrollToTop() {
        binding.recyclerViewDiscussions.scrollToPosition(0)
    }

    private fun showLoadingState(isLoading: Boolean) {
        with(binding) {
            recyclerViewDiscussions.visibility = if (isLoading) View.GONE else View.VISIBLE
            progressBarDiscussionForumLoad.visibility = if (isLoading) View.VISIBLE else View.GONE
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