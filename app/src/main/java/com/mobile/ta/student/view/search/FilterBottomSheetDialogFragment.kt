package com.mobile.ta.student.view.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.R
import com.mobile.ta.databinding.BsdFilterBinding
import com.mobile.ta.student.adapter.course.information.TagAdapter
import com.mobile.ta.student.adapter.diff.StringDiffCallback
import com.mobile.ta.student.viewmodel.search.SearchViewModel
import com.mobile.ta.student.viewmodel.search.SearchViewModel.Companion.SortOption
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class FilterBottomSheetDialogFragment :
    BottomSheetDialogFragment(),
    View.OnClickListener {
    companion object {
        fun newInstance() = FilterBottomSheetDialogFragment()
        private const val TAG_SELECTION_DIALOG = "tag_selection_dialog"
    }

    private var _binding: BsdFilterBinding? = null
    private val binding get() = _binding as BsdFilterBinding
    private val viewmodel by viewModels<SearchViewModel>(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BsdFilterBinding.inflate(inflater, container, false)
        binding.bsdFilterApply.setOnClickListener(this)
        binding.bsdFilterTagEdit.setOnClickListener(this)
        binding.bsdFilterResetToDefault.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTagRecyclerView()
        setupRadioSortOption()
        observeViewModel()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bsd_filter_reset_to_default -> {
                viewmodel.resetFilter()
                viewmodel.performFilter()
                this.dismiss()
            }
            R.id.bsd_filter_apply -> {
                viewmodel.performFilter()
                this.dismiss()
            }
            R.id.bsd_filter_tag_edit -> showTagSelectionDialog()
        }
    }

    private fun setupTagRecyclerView() {
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            flexWrap = FlexWrap.WRAP
        }
        val spaceSeparator = DividerItemDecoration(context, LinearLayoutManager.VERTICAL).apply {
            val drawable = ResourcesCompat.getDrawable(
                resources,
                R.drawable.rv_space_separator, null
            ) ?: return@apply
            setDrawable(drawable)
        }
        val tagAdapter = TagAdapter(StringDiffCallback())
        with(binding.bsdFilterTagRv) {
            layoutManager = flexboxLayoutManager
            this.adapter = tagAdapter
            addItemDecoration(spaceSeparator)
        }
    }

    private fun setupRadioSortOption() {
        binding.bsdFilterSortGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.bsd_filter_sort_ascending -> viewmodel.saveSelectedSortOption(SortOption.A_Z)
                R.id.bsd_filter_sort_descending -> viewmodel.saveSelectedSortOption(SortOption.Z_A)
            }
        }
    }

    private fun showTagSelectionDialog() {
        val dialog = TagSelectionDialogFragment()
        dialog.show(parentFragmentManager, TAG_SELECTION_DIALOG)
    }

    private fun observeViewModel() {
        viewmodel.selectedTags.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.bsdFilterTagRv.visibility = View.GONE
                binding.bsdFilterNoTagSelected.visibility = View.VISIBLE
            } else {
                binding.bsdFilterTagRv.visibility = View.VISIBLE
                binding.bsdFilterNoTagSelected.visibility = View.GONE
            }

            if (binding.bsdFilterTagRv.adapter is TagAdapter) {
                (binding.bsdFilterTagRv.adapter as TagAdapter).submitList(it)
            }
        }
        viewmodel.selectedSortOption.observe(viewLifecycleOwner) {
            if (it == SortOption.A_Z)
                binding.bsdFilterSortAscending.isChecked = true
            else
                binding.bsdFilterSortDescending.isChecked = true
        }
    }
}