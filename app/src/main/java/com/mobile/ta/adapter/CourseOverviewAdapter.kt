package com.mobile.ta.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobile.ta.R
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.databinding.VhCourseOverviewBinding
import com.mobile.ta.model.CourseOverview
import com.mobile.ta.model.LevelTag
import com.mobile.ta.model.TypeTag
import com.mobile.ta.ui.HomeFragment
import com.mobile.ta.ui.HomeFragmentDirections
import com.mobile.ta.ui.SearchFragmentDirections
import com.mobile.ta.ui.search.SearchFragment

class CourseOverviewAdapter(
    diffCallback: CourseOverviewDiffCallback
) : ListAdapter<CourseOverview, CourseOverviewAdapter.ViewHolder>(diffCallback) {
    private lateinit var parentFragment: Fragment
    inner class ViewHolder(private val binding: VhCourseOverviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(courseOverview: CourseOverview) {
            val context = binding.root.context
            binding.vhCoTitle.text = courseOverview.title
            binding.vhCoDesc.text = courseOverview.description
            binding.vhCoLevel.text = when (courseOverview.level) {
                LevelTag.JUNIOR_ONE -> context.getString(R.string.junior_one_text)
                LevelTag.JUNIOR_TWO -> context.getString(R.string.junior_two_text)
                LevelTag.JUNIOR_THREE -> context.getString(R.string.junior_three_text)
                LevelTag.SENIOR_ONE -> context.getString(R.string.senior_one_text)
                LevelTag.SENIOR_TWO -> context.getString(R.string.senior_two_text)
                LevelTag.SENIOR_THREE -> context.getString(R.string.senior_three_text)
            }
            binding.vhCoImage.setImageResource(courseOverview.imageUrl)
            binding.vhCoType.text = when (courseOverview.type) {
                TypeTag.BIOLOGY -> context.getString(R.string.biology_text)
                TypeTag.MATH -> context.getString(R.string.math_text)
                TypeTag.PHYSICS -> context.getString(R.string.physics_text)
                TypeTag.CHEMISTRY -> context.getString(R.string.chemistry_text)
            }
            binding.root.setOnClickListener {
                if (parentFragment is HomeFragment)
                    it.findNavController().navigate(
                        HomeFragmentDirections.actionHomeFragmentToCourseInformationFragment(""))
                else if (parentFragment is SearchFragment)
                    it.findNavController().navigate(
                        SearchFragmentDirections.actionSearchFragmentToCourseInformationFragment(""))
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

    fun setParentFragment(fragment: Fragment) {
        parentFragment = fragment
    }
}