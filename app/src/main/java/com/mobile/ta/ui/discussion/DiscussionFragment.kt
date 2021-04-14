package com.mobile.ta.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
import com.mobile.ta.adapter.discussion.DiscussionAnswerAdapter
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentDiscussionBinding
import com.mobile.ta.utils.toDateString
import com.mobile.ta.viewmodel.discussion.DiscussionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Date

@AndroidEntryPoint
class DiscussionFragment : Fragment(), View.OnClickListener {

    companion object {
        private const val REPLY_DISCUSSION_TAG = "REPLY DISCUSSION"
    }

    private var _binding: FragmentDiscussionBinding? = null
    private val binding get() = _binding!!

    private val args: DiscussionFragmentArgs by navArgs()

    private val discussionAnswerAdapter by lazy { DiscussionAnswerAdapter() }

    private val viewModel by viewModels<DiscussionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscussionBinding.inflate(inflater, container, false)
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
                buttonReplyDiscussion -> openReplyBottomSheet()
            }
        }
    }

    private fun setupObserver() {
        viewModel.setDiscussionId(args.id)
        viewModel.fetchDiscussion()
        viewModel.discussionForumQuestion.observe(viewLifecycleOwner, {
            it.data?.let { discussionForum ->
                binding.layoutDiscussionQuestion.root.visibility = View.VISIBLE
                setupQuestionData(
                    discussionForum.name,
                    discussionForum.userName,
                    discussionForum.createdAt,
                    discussionForum.question
                )
            }
        })
        viewModel.discussionAnswers.observe(viewLifecycleOwner, {
            it.data?.let { data ->
                if (data.isEmpty()) {
                    hideResult(false)
                } else {
                    showResult()
                    discussionAnswerAdapter.submitList(data)
                    binding.layoutDiscussionQuestion.textViewReplyCount.text = data.size.toString()
                }
            }
        })
        viewModel.isAnswerAdded.observe(viewLifecycleOwner, {
            if (it) {
                showSuccessAddReplyToast()
            }
            viewModel.setIsAnswerAdded()
        })
    }

    private fun setupViews() {
        with(binding) {
            buttonReplyDiscussion.setOnClickListener(this@DiscussionFragment)
            with(recyclerViewDiscussionAnswer) {
                layoutManager = LinearLayoutManager(context)
                adapter = discussionAnswerAdapter
                setHasFixedSize(false)
            }
        }
    }

    private fun hideResult(isLoading: Boolean) {
        with(binding) {
            recyclerViewDiscussionAnswer.visibility = View.GONE
            progressBarDiscussionLoad.visibility = if (isLoading) {
                View.VISIBLE
            } else {
                View.GONE
            }
            textViewDiscussionRepliesEmpty.visibility = if (isLoading) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }
    }

    private fun openReplyBottomSheet() {
        ReplyDiscussionBottomSheetDialogFragment.newInstance(viewModel::createNewDiscussionAnswer)
            .show(parentFragmentManager, REPLY_DISCUSSION_TAG)
    }

    private fun setupQuestionData(
        title: String, userName: String, createdAt: Date?, question: String
    ) {
        with(binding.layoutDiscussionQuestion) {
            textViewDiscussionTitle.text = title
            textViewDiscussionQuestionerName.text = userName
            textViewDiscussionQuestionCreatedTime.text =
                createdAt?.toDateString(Constants.DD_MMMM_YYYY_HH_MM_SS).orEmpty()
            textViewDiscussionQuestion.text = question
            textViewReplyCount.text = getString(R.string.reply_count_state)
        }
    }

    private fun showResult() {
        with(binding) {
            recyclerViewDiscussionAnswer.visibility = View.VISIBLE
            progressBarDiscussionLoad.visibility = View.GONE
        }
    }

    private fun showSuccessAddReplyToast() {
        Snackbar.make(
            binding.root,
            getString(R.string.success_add_reply_message),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}