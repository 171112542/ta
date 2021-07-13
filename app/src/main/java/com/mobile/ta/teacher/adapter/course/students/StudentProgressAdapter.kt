package com.mobile.ta.teacher.adapter.course.students

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.databinding.TVhStudentProgressBinding
import com.mobile.ta.model.studentProgress.StudentProgress
import com.mobile.ta.teacher.adapter.diff.StudentProgressDiffCallback

class StudentProgressAdapter(
    diffCallback: StudentProgressDiffCallback
): ListAdapter<StudentProgress, StudentProgressAdapter.ViewHolder>(diffCallback) {
    inner class ViewHolder(
        private val binding: TVhStudentProgressBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(studentProgress: StudentProgress) {
            binding.let {
                it.tVhStudentProgressName.text = studentProgress.student?.name
                it.tVhStudentProgressEmail.text = studentProgress.student?.email
                it.tVhStudentProgressScore.text = studentProgress.averageScore.toString()
                it.tVhStudentProgressProgress.text =
                    it.root.context
                        .getString(
                            R.string.student_progress_text_template,
                            studentProgress.finishedChapterIds.size.toString(),
                            studentProgress.totalChapterCount.toString()
                        )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TVhStudentProgressBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}