package com.mobile.ta.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.adapter.discussion.DiscussionAnswerAdapter
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentDiscussionBinding
import com.mobile.ta.utils.toDateString
import com.mobile.ta.viewmodel.discussion.DiscussionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class DiscussionFragment : Fragment() {

    companion object {
        private const val REPLY_DISCUSSION_TAG = "REPLY DISCUSSION"
    }

    private lateinit var binding: FragmentDiscussionBinding

    private val args: DiscussionFragmentArgs by navArgs()
    private val discussionAnswerAdapter by lazy { DiscussionAnswerAdapter() }
    private val viewModel: DiscussionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiscussionBinding.inflate(inflater, container, false)
        with(binding) {
            buttonReplyDiscussion.setOnClickListener {
                openReplyBottomSheet()
            }
            with(recyclerViewDiscussionAnswer) {
                layoutManager = LinearLayoutManager(context)
                adapter = discussionAnswerAdapter
            }
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.fetchDiscussion(args.id)
        viewModel.discussionForumQuestion.observe(viewLifecycleOwner, {
            it?.let { discussionForum ->
                binding.layoutDiscussionQuestion.root.visibility = View.VISIBLE
                setupQuestionData(
                    discussionForum.name,
                    discussionForum.userName,
                    discussionForum.createdAt,
                    discussionForum.question,
                    discussionForum.answer.size
                )
            }
        })
        viewModel.discussionAnswers.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                hideResult(false)
            } else {
                discussionAnswerAdapter.submitList(it)
                showResult()
                binding.layoutDiscussionQuestion.textViewReplyCount.text = it.size.toString()
            }
        })
    }

    private fun hideResult(isLoading: Boolean) {
        with(binding) {
            recyclerViewDiscussionAnswer.visibility = View.GONE
            progressBarDiscussionLoad.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun openReplyBottomSheet() {
        ReplyDiscussionBottomSheetDialogFragment.newInstance(viewModel::createNewDiscussionAnswer)
            .show(parentFragmentManager, REPLY_DISCUSSION_TAG)
    }

    private fun setupQuestionData(
        title: String, userName: String, createdAt: Date, question: String, replyCount: Int
    ) {
        with(binding.layoutDiscussionQuestion) {
            textViewDiscussionTitle.text = title
            textViewDiscussionQuestionerName.text = userName
            textViewDiscussionQuestionCreatedTime.text =
                createdAt.toDateString(Constants.DD_MMMM_YYYY_HH_MM_SS)
            textViewDiscussionQuestion.text = question
            textViewReplyCount.text = replyCount.toString()
        }
    }

    private fun showResult() {
        with(binding) {
            recyclerViewDiscussionAnswer.visibility = View.VISIBLE
            progressBarDiscussionLoad.visibility = View.GONE
        }
    }
}