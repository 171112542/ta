package com.mobile.ta.adapter.user.course

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.adapter.diff.UserCourseDiffCallback
import com.mobile.ta.databinding.VhCourseCardBinding
import com.mobile.ta.model.user.course.UserCourse
import com.mobile.ta.utils.getMaximum
import com.mobile.ta.utils.view.ImageUtil


class UserCourseAdapter(
    private val onClickListener: (String) -> Unit,
    private val changeProgressListener: (String, ProgressBar) -> Unit
) : ListAdapter<UserCourse, UserCourseAdapter.ViewHolder>(UserCourseDiffCallback()) {
    class ViewHolder private constructor(
        private val binding: VhCourseCardBinding,
        private val onClickListener: (String) -> Unit,
        private val changeProgressListener: (String, ProgressBar) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: UserCourse
        ) {
            binding.apply {
                courseContainer.setOnClickListener {
                    onClickListener.invoke(item.id)
                }
                courseCardTitle.text = item.title
                courseCardDescription.text =
                    item.description.getMaximum(64)
                item.imageUrl?.let {
                    ImageUtil.loadImage(courseCardImage.context, it, courseCardImage)
                }
                changeProgressListener.invoke(item.id, courseCardProgress)
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                onClickListener: (String) -> Unit,
                changeProgressListener: (String, ProgressBar) -> Unit
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = VhCourseCardBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, onClickListener, changeProgressListener)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent, onClickListener, changeProgressListener
        )
    }
}