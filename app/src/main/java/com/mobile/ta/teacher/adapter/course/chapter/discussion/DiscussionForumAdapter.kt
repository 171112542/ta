package com.mobile.ta.teacher.adapter.course.chapter.discussion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.LayoutDiscussionForumItemBinding
import com.mobile.ta.model.course.chapter.discussion.DiscussionForum
import com.mobile.ta.utils.toDateString

class DiscussionForumAdapter(private val onClickListener: (String) -> Unit) :
    ListAdapter<DiscussionForum, DiscussionForumAdapter.DiscussionForumViewHolder>(diffCallback) {

    companion object {
        const val STATE_BLUE = 0
        const val STATE_GREEN = 1
        const val STATE_ORANGE = 2
        const val STATE_PINK = 3

        private val diffCallback = object : DiffUtil.ItemCallback<DiscussionForum>() {
            override fun areItemsTheSame(oldItem: DiscussionForum, newItem: DiscussionForum) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: DiscussionForum, newItem: DiscussionForum) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = DiscussionForumViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_discussion_forum_item, parent, false),
        viewType
    )

    override fun onBindViewHolder(holder: DiscussionForumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int = position % 4

    inner class DiscussionForumViewHolder(itemView: View, private val viewType: Int) :
        RecyclerView.ViewHolder(itemView) {

        private val binding by lazy {
            LayoutDiscussionForumItemBinding.bind(itemView)
        }

        fun bind(data: DiscussionForum) {
            with(binding) {
                textViewDiscussionTitle.text = data.name
                textViewDiscussionQuestion.text = data.question
                textViewDiscussionCreatedTime.text =
                    data.createdAt?.toDateString(Constants.DD_MMMM_YYYY_HH_MM_SS).orEmpty()

                cardViewDiscussionItem.setOnClickListener {
                    onClickListener.invoke(data.id)
                }

                courseCardViewRounded.background = getDrawable(viewType)
            }
        }

        private fun getDrawable(type: Int) = ContextCompat.getDrawable(
            itemView.context, when (type) {
                STATE_BLUE -> R.drawable.drawable_half_rounded_blue
                STATE_GREEN -> R.drawable.drawable_half_rounded_green
                STATE_ORANGE -> R.drawable.drawable_half_rounded_orange
                else -> R.drawable.drawable_half_rounded_pink
            }
        )
    }
}