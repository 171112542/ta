package com.mobile.ta.adapter.user.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.adapter.diff.ProfileFeedbackDiffCallback
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.VhProfileFeedbackBinding
import com.mobile.ta.model.user.feedback.Feedback
import com.mobile.ta.utils.toDateString

class ProfileFeedbackAdapter(
    diffCallback: ProfileFeedbackDiffCallback
) : ListAdapter<Feedback, ProfileFeedbackAdapter.ProfileFeedbackViewHolder>(diffCallback) {

    class ProfileFeedbackViewHolder(private val binding: VhProfileFeedbackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(feedback: Feedback) {
            binding.apply {
                profileFeedbackId.text = feedback.id
                profileFeedbackDateSubmitted.text =
                    feedback.createdAt.toDate().toDateString(Constants.DD_MMMM_YYYY_HH_MM_SS)
                profileFeedbackType.text = feedback.feedbackType
                profileFeedbackDescription.text = feedback.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileFeedbackViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VhProfileFeedbackBinding.inflate(layoutInflater, parent, false)
        return ProfileFeedbackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileFeedbackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}