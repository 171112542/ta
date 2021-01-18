package com.mobile.ta.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.databinding.VhCourseOverviewBinding
import com.mobile.ta.model.CourseOverview
import com.mobile.ta.model.LevelTag

class CourseOverviewAdapter(
    diffCallback: CourseOverviewDiffCallback
) : ListAdapter<CourseOverview, CourseOverviewAdapter.ViewHolder>(diffCallback) {
    class ViewHolder(private val binding: VhCourseOverviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(courseOverview: CourseOverview) {
            binding.vhCoTitle.text = courseOverview.title
            binding.vhCoDesc.text = courseOverview.description
            binding.vhCoLevel.text = when (courseOverview.level) {
                LevelTag.JUNIOR_ONE -> "Junior - 1"
                LevelTag.JUNIOR_TWO -> "Junior - 2"
                LevelTag.JUNIOR_THREE -> "Junior - 3"
                LevelTag.SENIOR_ONE -> "Senior - 1"
                LevelTag.SENIOR_TWO -> "Senior - 2"
                LevelTag.SENIOR_THREE -> "Senior - 3"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VhCourseOverviewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}