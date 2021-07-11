package com.mobile.ta.teacher.adapter.course.information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.teacher.adapter.diff.ChapterSummaryDiffCallback
import com.mobile.ta.databinding.LayoutCourseContentItemBinding
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType

class CourseInformationContentAdapter(
    private val onClickListener: (String, ChapterType, Int) -> Unit,
    private val changeProgress: (String, TextView) -> Unit
) : ListAdapter<ChapterSummary, CourseInformationContentAdapter.CourseInfoChapterViewHolder>(
    ChapterSummaryDiffCallback()
) {
    inner class CourseInfoChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCourseContentItemBinding.bind(itemView)
        val context: Context = itemView.context

        fun bind(position: Int, data: ChapterSummary) {
            with(binding) {
                textViewCourseContentTitle.text = data.title
                changeProgress(data.id, textViewCourseContentProgress)
                textViewCourseContentLessonNumber.text = when (data.type as ChapterType) {
                    ChapterType.CONTENT -> context.getString(R.string.lesson_label)
                    ChapterType.PRACTICE -> context.getString(R.string.practice_label)
                    ChapterType.QUIZ -> context.getString(R.string.quiz_label)
                }
                root.setOnClickListener {
                    onClickListener.invoke(data.id, data.type, position)
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
        holder.bind(position, getItem(position))
    }
}