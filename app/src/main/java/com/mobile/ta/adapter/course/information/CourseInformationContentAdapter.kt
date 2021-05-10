package com.mobile.ta.adapter.course.information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseInfoChapterDiffCallback
import com.mobile.ta.databinding.LayoutCourseContentItemBinding
import com.mobile.ta.model.course.chapter.Chapter

class CourseInformationContentAdapter(
    private val onClickListener: (String) -> Unit
) : ListAdapter<Chapter, CourseInformationContentAdapter.CourseInfoChapterViewHolder>(
    CourseInfoChapterDiffCallback()
) {
    inner class CourseInfoChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCourseContentItemBinding.bind(itemView)
        val context: Context = itemView.context

        fun bind(data: Chapter) {
            with(binding) {
                textViewCourseContentTitle.text = data.title
//                textViewCourseContentDescription.text = data.content
                textViewCourseContentLessonNumber.text =
                    context.resources.getString(R.string.lesson_number, data.typeOrder)
                context.resources.getString(R.string.practice_number, data.typeOrder)

                root.setOnClickListener {
                    onClickListener.invoke(data.id as String)
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