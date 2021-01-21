package com.mobile.ta.adapter.courseInfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseInfoChapterDiffCallback
import com.mobile.ta.databinding.LayoutCourseContentItemBinding
import com.mobile.ta.model.courseInfo.Chapter

class CourseInfoChapterAdapter(
    diffCallback: CourseInfoChapterDiffCallback,
    private val onClickListener: (String) -> Unit
) : ListAdapter<Chapter, CourseInfoChapterAdapter.CourseInfoChapterViewHolder>(
    diffCallback
) {

    inner class CourseInfoChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = LayoutCourseContentItemBinding.bind(itemView)
        val context: Context = itemView.context

        fun bind(data: Chapter) {
            with(binding) {
                textViewCourseContentTitle.text = data.name
                textViewCourseContentDescription.text = data.content
                textViewCourseContentLessonNumber.text =
                    context.resources.getString(R.string.lesson_number, data.order)
                textViewCourseContentProgress.visibility = View.GONE

                root.setOnClickListener {
                    onClickListener.invoke(data.id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CourseInfoChapterViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_course_content_item, parent, false)
        )

    override fun onBindViewHolder(holder: CourseInfoChapterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}