package com.mobile.ta.adapter.courseInfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseInfoPrerequisitesCourseDiffCallback
import com.mobile.ta.databinding.LayoutCourseItemBinding
import com.mobile.ta.model.courseInfo.PrerequisiteCourse
import com.mobile.ta.utils.ImageUtil

class CourseInfoPrerequisitesCourseAdapter(
    diffCallback: CourseInfoPrerequisitesCourseDiffCallback,
    private val onClickListener: (String) -> Unit
) : ListAdapter<PrerequisiteCourse, CourseInfoPrerequisitesCourseAdapter.PrerequisitesCourseViewHolder>(
    diffCallback
) {

    inner class PrerequisitesCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = LayoutCourseItemBinding.bind(itemView)
        val context: Context = itemView.context

        fun bind(data: PrerequisiteCourse) {
            with(binding) {
                textViewCourseItemTitle.text = data.name
                textViewCourseItemEnrolledStudents.visibility = View.GONE

                ImageUtil.loadImage(context, data.photo, imageViewCourseItem)

                root.setOnClickListener {
                    onClickListener.invoke(data.id)
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