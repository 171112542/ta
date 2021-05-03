package com.mobile.ta.adapter.user.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.UserCourseDiffCallback
import com.mobile.ta.databinding.VhCourseCardBinding
import com.mobile.ta.model.user.course.UserCourse


class UserCourseAdapter(
    diffCallback: UserCourseDiffCallback
) : ListAdapter<UserCourse, UserCourseAdapter.ViewHolder>(diffCallback) {
    class ViewHolder private constructor(private val binding: VhCourseCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: UserCourse
        ) {
            binding.apply {
                courseContainer.setOnClickListener {
                    it.findNavController()
                        .navigate(R.id.action_myCourseFragment_to_courseInformationFragment)
                }
                courseCardTitle.text = item.title
                courseCardDescription.text = item.description
                courseCardProgress.progress = item.progress
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VhCourseCardBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }
}