package com.mobile.ta.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.adapter.diff.ProfileFeedbackDiffCallback
import com.mobile.ta.data.FeedbackData.labelFeedbackDateFormat
import com.mobile.ta.databinding.VhProfileFeedbackBinding
import com.mobile.ta.model.Feedback

class ProfileFeedbackAdapter(
    diffCallback: ProfileFeedbackDiffCallback
) : ListAdapter<Feedback, ProfileFeedbackAdapter.ViewHolder>(diffCallback) {
    class ViewHolder(private val binding: VhProfileFeedbackBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(feedback: Feedback) {
            binding.apply {
                profileFeedbackId.text = feedback.id
                profileFeedbackDateSubmitted.text =
                    labelFeedbackDateFormat.format(feedback.createdAt)
                profileFeedbackType.text = feedback.feedbackType
                profileFeedbackDescription.text = feedback.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VhProfileFeedbackBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}