package com.mobile.ta.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.databinding.VhCourseOverviewBinding
import com.mobile.ta.model.CourseOverview

class CourseOverviewAdapter(
    diffCallback: CourseOverviewDiffCallback
) : ListAdapter<CourseOverview, CourseOverviewAdapter.ViewHolder>(diffCallback) {
    class ViewHolder(private val binding: VhCourseOverviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(courseOverview: CourseOverview) {
            binding.vhCoTitle.text = courseOverview.title
            binding.vhCoDesc.text = courseOverview.description
            binding.vhCoLevel.text = courseOverview.level
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