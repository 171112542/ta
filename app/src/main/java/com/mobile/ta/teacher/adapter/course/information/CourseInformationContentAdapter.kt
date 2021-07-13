package com.mobile.ta.teacher.adapter.course.information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.databinding.LayoutCourseContentItemBinding
import com.mobile.ta.model.course.chapter.ChapterSummary
import com.mobile.ta.model.course.chapter.ChapterType
import com.mobile.ta.teacher.adapter.diff.ChapterSummaryDiffCallback
import kotlin.math.roundToInt

class CourseInformationContentAdapter(
    private val onClickListener: (String, ChapterType) -> Unit,
) : ListAdapter<ChapterSummary, CourseInformationContentAdapter.CourseInfoChapterViewHolder>(
    ChapterSummaryDiffCallback()
) {
    inner class CourseInfoChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = LayoutCourseContentItemBinding.bind(itemView)
        val context: Context = itemView.context

        fun bind(data: ChapterSummary) {
            with(binding) {
                cardViewCourseContentItem.layoutParams.height =
                    context.resources.getDimension(R.dimen.t_information_content_card_height)
                        .roundToInt()
                textViewCourseContentProgress.visibility = GONE
                textViewCourseContentTitle.text = data.title
                textViewCourseContentLessonNumber.text = when (data.type as ChapterType) {
                    ChapterType.CONTENT -> context.getString(R.string.lesson_label)
                    ChapterType.PRACTICE -> context.getString(R.string.practice_label)
                    ChapterType.QUIZ -> context.getString(R.string.quiz_label)
                }
                root.setOnClickListener {
                    onClickListener.invoke(data.id, data.type)
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