package com.mobile.ta.adapter.course.information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseInfoChapterDiffCallback
import com.mobile.ta.databinding.LayoutCourseContentItemBinding
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterType

class CourseInformationContentAdapter(
    private val onClickListener: (String, ChapterType, Int) -> Unit,
    private val changeProgress: (String, TextView) -> Unit
) : ListAdapter<Chapter, CourseInformationContentAdapter.CourseInfoChapterViewHolder>(
    CourseInfoChapterDiffCallback()
) {
    inner class CourseInfoChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCourseContentItemBinding.bind(itemView)
        val context: Context = itemView.context

        fun bind(data: Chapter) {
            with(binding) {
                textViewCourseContentTitle.text = data.title
                changeProgress(data.id, textViewCourseContentProgress)
                val typeOrder = data.typeOrder + 1
                textViewCourseContentLessonNumber.text = when (data.type) {
                    ChapterType.CONTENT -> String.format(context.getString(R.string.lesson_number), typeOrder)
                    ChapterType.PRACTICE -> String.format(context.getString(R.string.practice_number), typeOrder)
                    ChapterType.QUIZ -> String.format(context.getString(R.string.quiz_number), typeOrder)
                }
                root.setOnClickListener {
                    onClickListener.invoke(data.id, data.type, data.order)
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