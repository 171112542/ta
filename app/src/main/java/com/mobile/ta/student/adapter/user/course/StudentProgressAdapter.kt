package com.mobile.ta.student.adapter.user.course

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.student.adapter.diff.StudentProgressDiffCallback
import com.mobile.ta.databinding.VhCourseCardBinding
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.utils.getMaximum
import com.mobile.ta.utils.view.ImageUtil


class StudentProgressAdapter(
    private val onClickListener: (String) -> Unit,
) : ListAdapter<StudentProgress, StudentProgressAdapter.ViewHolder>(StudentProgressDiffCallback()) {
    class ViewHolder private constructor(
        private val binding: VhCourseCardBinding,
        private val onClickListener: (String) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: StudentProgress
        ) {
            binding.apply {
                courseContainer.setOnClickListener {
                    onClickListener.invoke(item.course?.id as String)
                }
                courseCardTitle.text = item.course?.title
                courseCardDescription.text =
                    item.course?.description?.getMaximum(64)
                item.course?.imageUrl?.let {
                    ImageUtil.loadImage(courseCardImage.context, it, courseCardImage)
                }
                val progress = item.finishedChapterIds.size * 100 / item.totalChapterCount
                ObjectAnimator.ofInt(courseCardProgress, "progress", 0, progress).apply {
                    duration = 500
                    start()
                }
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onClickListener: (String) -> Unit,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VhCourseCardBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, onClickListener)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onClickListener)
    }
}