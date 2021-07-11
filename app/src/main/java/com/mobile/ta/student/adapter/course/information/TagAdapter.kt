package com.mobile.ta.student.adapter.course.information

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.student.adapter.diff.StringDiffCallback
import com.mobile.ta.databinding.VhTagBinding

class TagAdapter(
    diffCallback: StringDiffCallback
) : ListAdapter<String, TagAdapter.ViewHolder>(diffCallback) {
    class ViewHolder(private val binding: VhTagBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: String) {
            binding.vhTagTitle.text = tag
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VhTagBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }
}