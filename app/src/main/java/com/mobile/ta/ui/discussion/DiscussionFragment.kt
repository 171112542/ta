package com.mobile.ta.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.databinding.FragmentDiscussionBinding

class DiscussionFragment : Fragment() {

    companion object {
        private const val REPLY_DISCUSSION_TAG = "REPLY DISCUSSION"
    }

    private lateinit var binding: FragmentDiscussionBinding

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
//                adapter = discussionForumAdapter
            }
        }

        return binding.root
    }

    private fun hideResult() {
        with(binding) {
            layoutDiscussionQuestion.root.visibility = View.GONE
            recyclerViewDiscussionAnswer.visibility = View.GONE
            progressBarDiscussionLoad.visibility = View.VISIBLE
        }
    }

    private fun openReplyBottomSheet() {
//        ReplyDiscussionBottomSheetDialogFragment.newInstance()
//            .show(parentFragmentManager, REPLY_DISCUSSION_TAG)
    }

    private fun showResult() {
        with(binding) {
            layoutDiscussionQuestion.root.visibility = View.VISIBLE
            recyclerViewDiscussionAnswer.visibility = View.VISIBLE
            progressBarDiscussionLoad.visibility = View.GONE
        }
    }
}