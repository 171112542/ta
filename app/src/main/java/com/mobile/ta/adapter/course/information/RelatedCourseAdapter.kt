package com.mobile.ta.adapter.course.information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseInfoPrerequisitesCourseDiffCallback
import com.mobile.ta.databinding.LayoutCourseItemBinding
import com.mobile.ta.model.course.information.RelatedCourse
import com.mobile.ta.utils.getOrDefaultInt
import com.mobile.ta.utils.view.ImageUtil

class RelatedCourseAdapter(
    private val onClickListener: (String) -> Unit
) : ListAdapter<RelatedCourse, RelatedCourseAdapter.PrerequisitesCourseViewHolder>(
    CourseInfoPrerequisitesCourseDiffCallback()
) {
    inner class PrerequisitesCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCourseItemBinding.bind(itemView)
        val context: Context = itemView.context

        fun bind(data: RelatedCourse) {
            binding.apply {
                textViewCourseItemTitle.text = data.title
                textViewCourseItemEnrolledStudents.text =
                    data.totalStudentEnrolled.getOrDefaultInt().toString()

                data.imageUrl?.let { ImageUtil.loadImage(context, it, imageViewCourseItem) }

                root.setOnClickListener {
                    onClickListener.invoke(data.id as String)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PrerequisitesCourseViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_course_item, parent, false)
        )

    override fun onBindViewHolder(holder: PrerequisitesCourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}