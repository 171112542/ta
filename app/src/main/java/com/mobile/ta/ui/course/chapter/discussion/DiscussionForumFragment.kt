package com.mobile.ta.ui.course.chapter.discussion

import android.util.Log
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
import com.mobile.ta.viewmodel.discussion.DiscussionForumViewModel
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
        showLoading()
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
        viewModel.fetchDiscussionForums()
        viewModel.discussionForums.observe(viewLifecycleOwner, {
            it?.data?.let { data ->
                discussionForumAdapter.submitList(data)
                showResult()
            }
            Log.d("DISCUSSION FORUMS: ", it?.data.toString())
        })
        viewModel.isForumAdded.observe(viewLifecycleOwner, {
            it?.let { isForumAdded ->
                if (isForumAdded) {
                    showSuccessAddForumToast()
                    viewModel.fetchDiscussionForums()
                }
                viewModel.setIsForumAdded()
            }
            Log.d("IS FORUM ADDED: ", it.toString())
        })
    }

    private fun goToDiscussionDetail(discussionForumId: String) {
        val courseAndChapterId = viewModel.getCourseAndChapterId()
        findNavController().navigate(
            DiscussionForumFragmentDirections.actionDiscussionForumFragmentToDiscussionFragment(
                courseAndChapterId.first, courseAndChapterId.second, discussionForumId
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