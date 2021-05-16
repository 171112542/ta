package com.mobile.ta.adapter.course

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.adapter.diff.CourseDiffCallback
import com.mobile.ta.databinding.VhCourseOverviewBinding
import com.mobile.ta.model.course.Course
import com.mobile.ta.utils.ImageUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi

interface CourseVHListener {
    fun onClickListener(courseId: String)
}

@ExperimentalCoroutinesApi
class CourseAdapter(
    diffCallback: CourseDiffCallback,
    val listener: CourseVHListener
) : ListAdapter<Course, CourseAdapter.ViewHolder>(diffCallback) {
    inner class ViewHolder(private val binding: VhCourseOverviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            val context = binding.root.context
            binding.vhCoTitle.text = course.title
            binding.vhCoDesc.text = course.description
            binding.vhCoLevel.text = course.level.toString()
            binding.vhCoType.text = course.type.toString()
            course.imageUrl.let { ImageUtil.loadImage(context, it, binding.vhCoImage) }
            binding.root.setOnClickListener {
                listener.onClickListener(course.id)
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

    override fun submitList(list: List<Course>?) {
        super.submitList(list)
        notifyDataSetChanged()
    }
}