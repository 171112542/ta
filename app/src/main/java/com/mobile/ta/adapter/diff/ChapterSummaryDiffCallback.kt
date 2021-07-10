package com.mobile.ta.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.mobile.ta.model.course.chapter.Chapter
import com.mobile.ta.model.course.chapter.ChapterSummary

class ChapterSummaryDiffCallback : DiffUtil.ItemCallback<ChapterSummary>() {

    override fun areItemsTheSame(oldItem: ChapterSummary, newItem: ChapterSummary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChapterSummary, newItem: ChapterSummary): Boolean {
        return oldItem == newItem
    }
}