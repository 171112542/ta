package com.mobile.ta.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.databinding.VhCourseOverviewBinding
import com.mobile.ta.model.course.Course
import com.mobile.ta.ui.home.HomeFragment
import com.mobile.ta.ui.home.HomeFragmentDirections
import com.mobile.ta.ui.search.SearchFragment
import com.mobile.ta.ui.search.SearchFragmentDirections
import com.mobile.ta.utils.ImageUtil

class CourseAdapter(
    diffCallback: CourseOverviewDiffCallback
) : ListAdapter<Course, CourseAdapter.ViewHolder>(diffCallback) {
    private lateinit var parentFragment: Fragment

    inner class ViewHolder(private val binding: VhCourseOverviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            val context = binding.root.context
            binding.vhCoTitle.text = course.title
            binding.vhCoDesc.text = course.description
            binding.vhCoLevel.text = course.level.toString()
            binding.vhCoType.text = course.type.toString()
            ImageUtil.loadImage(context, course.imageUrl, binding.vhCoImage)
            binding.root.setOnClickListener {
                //TODO: Make adapter only update ViewModel regarding navigation signal and let the Fragment handle the navigation
                if (parentFragment is HomeFragment)
                    it.findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToCourseInformationFragment("")
                    )
                else if (parentFragment is SearchFragment)
                    it.findNavController().navigate(
                        SearchFragmentDirections.actionSearchFragmentToCourseInformationFragment("")
                    )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = VhCourseOverviewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    override fun submitList(list: List<Course>?) {
        super.submitList(list)
        notifyDataSetChanged()
    }

    fun setParentFragment(fragment: Fragment) {
        parentFragment = fragment
    }
}