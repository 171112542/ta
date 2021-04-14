package com.mobile.ta.adapter.discussion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.LayoutDiscussionReplyBinding
import com.mobile.ta.model.discussion.DiscussionForumAnswer
import com.mobile.ta.utils.toDateString

class DiscussionAnswerAdapter(private val onMarkAsAnswerListener: ((String) -> Unit)? = null) :
    ListAdapter<DiscussionForumAnswer, DiscussionAnswerAdapter.DiscussionAnswerViewHolder>(
        diffCallback
    ) {

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<DiscussionForumAnswer>() {
            override fun areItemsTheSame(
                oldItem: DiscussionForumAnswer, newItem: DiscussionForumAnswer
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: DiscussionForumAnswer, newItem: DiscussionForumAnswer
            ) = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DiscussionAnswerViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_discussion_reply, parent, false)
    )

    override fun onBindViewHolder(holder: DiscussionAnswerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DiscussionAnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding by lazy {
            LayoutDiscussionReplyBinding.bind(itemView)
        }

        fun bind(data: DiscussionForumAnswer) {
            with(binding) {
                textViewDiscussionReply.text = data.answer
                textViewDiscussionQuestionCreatedTime.text =
                    data.createdAt?.toDateString(Constants.DD_MMMM_YYYY_HH_MM_SS).orEmpty()
                textViewDiscussionQuestionerName.text = data.userName

                buttonMarkAsAcceptedAnswer.apply {
                    visibility = onMarkAsAnswerListener?.let {
                        View.VISIBLE
                    } ?: run {
                        View.GONE
                    }
                    setOnClickListener {
                        onMarkAsAnswerListener?.invoke(data.id)
                    }
                }
            }
        }
    }
}