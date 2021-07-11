package com.mobile.ta.student.view.course.chapter.discussion

import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
import com.mobile.ta.databinding.FragmentDiscussionBinding
import com.mobile.ta.student.adapter.course.chapter.discussion.DiscussionAnswerAdapter
import com.mobile.ta.student.viewmodel.course.chapter.discussion.DiscussionViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.isNotNull
import com.mobile.ta.utils.toDateString
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DiscussionFragment :
    BaseFragment<FragmentDiscussionBinding>(FragmentDiscussionBinding::inflate),
    View.OnClickListener {

    companion object {
        private const val REPLY_DISCUSSION_TAG = "REPLY DISCUSSION"
    }

    private val args: DiscussionFragmentArgs by navArgs()

    private val discussionAnswerAdapter by lazy {
        DiscussionAnswerAdapter(viewModel::markAsAcceptedAnswer)
    }

    private val viewModel by viewModels<DiscussionViewModel>()

    override fun runOnCreateView() {
        binding.apply {
            buttonReplyDiscussion.setOnClickListener(this@DiscussionFragment)
            with(recyclerViewDiscussionAnswer) {
                layoutManager = LinearLayoutManager(context)
                adapter = discussionAnswerAdapter
                setHasFixedSize(false)
            }
        }
        showLoadingState(true)
        setupObserver()
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                buttonReplyDiscussion -> openReplyBottomSheet()
            }
        }
    }

    private fun setupObserver() {
        viewModel.setDiscussionData(args.courseId, args.chapterId, args.id)
        viewModel.fetchDiscussion()
        viewModel.discussionForumQuestion.observe(viewLifecycleOwner, {
            it?.let { discussionForum ->
                binding.layoutDiscussionQuestion.root.visibility = View.VISIBLE
                setupQuestionData(
                    discussionForum.name,
                    discussionForum.userName,
                    discussionForum.createdAt,
                    discussionForum.question,
                    discussionForum.userImage
                )
                discussionAnswerAdapter.setHasAcceptedAnswer(discussionForum.acceptedAnswerId.isNotNull())
            }
        })
        viewModel.discussionAnswers.observe(viewLifecycleOwner, {
            it?.let { data ->
                showEmptyState(data.isEmpty())
                showLoadingState(false)
                discussionAnswerAdapter.submitList(data)
                binding.layoutDiscussionQuestion.textViewReplyCount.text = data.size.toString()
            }
        })
        viewModel.isAnswerAdded.observe(viewLifecycleOwner, {
            it?.let { added ->
                if (added) {
                    showSuccessAddReplyToast()
                }
                viewModel.setIsAnswerAdded(false)
            }
        })
        viewModel.user.observe(viewLifecycleOwner, {
            if (it.isNotNull()) {
                discussionAnswerAdapter.setIsCurrentUser(viewModel.isCurrentUser())
            }
        })
    }

    private fun openReplyBottomSheet() {
        ReplyDiscussionBottomSheetDialogFragment.newInstance(viewModel::createNewDiscussionAnswer)
            .show(parentFragmentManager, REPLY_DISCUSSION_TAG)
    }

    private fun setupQuestionData(
        title: String, userName: String, createdAt: Date?, question: String, image: String
    ) {
        with(binding.layoutDiscussionQuestion) {
            textViewDiscussionTitle.text = title
            textViewDiscussionQuestionerName.text = userName
            textViewDiscussionQuestionCreatedTime.text =
                createdAt?.toDateString(Constants.DD_MMMM_YYYY_HH_MM_SS).orEmpty()
            textViewDiscussionQuestion.text = question
            textViewReplyCount.text = getString(R.string.reply_count_state)

            ImageUtil.loadImageWithPlaceholder(
                mContext,
                image,
                imageViewDiscussionQuestioner,
                R.drawable.ic_person
            )
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        with(binding) {
            recyclerViewDiscussionAnswer.visibility = if (isEmpty) View.GONE else View.VISIBLE
            textViewDiscussionRepliesEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun showLoadingState(isLoading: Boolean) {
        binding.progressBarDiscussionLoad.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSuccessAddReplyToast() {
        Snackbar.make(
            binding.root,
            getString(R.string.success_add_reply_message),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}