package com.mobile.ta.teacher.view.course.chapter.discussion

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.databinding.TFragmentDiscussionForumBinding
import com.mobile.ta.teacher.adapter.course.chapter.discussion.DiscussionForumAdapter
import com.mobile.ta.teacher.viewmodel.course.chapter.discussion.DiscussionForumViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DiscussionForumFragment :
    BaseFragment<TFragmentDiscussionForumBinding>(TFragmentDiscussionForumBinding::inflate) {

    private val args: DiscussionForumFragmentArgs by navArgs()

    private val discussionForumAdapter by lazy {
        DiscussionForumAdapter(this::goToDiscussionDetail)
    }
    private val viewModel by viewModels<DiscussionForumViewModel>()

    override fun runOnCreateView() {
        binding.apply {
            with(recyclerViewDiscussions) {
                layoutManager = LinearLayoutManager(context)
                adapter = discussionForumAdapter
                setHasFixedSize(true)
            }
        }
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.setCourseAndChapterId(args.courseId, args.chapterId)
        fetchDiscussionForums()
        viewModel.discussionForums.observe(viewLifecycleOwner, {
            it?.let { data ->
                discussionForumAdapter.submitList(data)
                scrollToTop()
                showLoadingState(false)
                showEmptyState(data.isEmpty())
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

    private fun scrollToTop() {
        binding.recyclerViewDiscussions.scrollToPosition(0)
    }

    private fun showLoadingState(isLoading: Boolean) {
        with(binding) {
            recyclerViewDiscussions.visibility = if (isLoading) View.GONE else View.VISIBLE
            progressBarDiscussionForumLoad.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        with(binding) {
            recyclerViewDiscussions.visibility = if (isEmpty) View.GONE else View.VISIBLE
            groupDiscussionEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }
}