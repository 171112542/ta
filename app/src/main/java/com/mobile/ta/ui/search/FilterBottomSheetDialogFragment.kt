package com.mobile.ta.ui.search

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.R
import com.mobile.ta.adapter.TagAdapter
import com.mobile.ta.adapter.diff.StringDiffCallback
import com.mobile.ta.databinding.BsdFilterBinding
import com.mobile.ta.viewmodel.SearchViewModel
import com.mobile.ta.viewmodel.SearchViewModel.Companion.SortOption


class FilterBottomSheetDialogFragment(private val viewmodel: SearchViewModel) :
    BottomSheetDialogFragment(),
    View.OnClickListener {
    companion object {
        fun newInstance(viewmodel: SearchViewModel) = FilterBottomSheetDialogFragment(viewmodel)
        private const val TAG_SELECTION_DIALOG = "tag_selection_dialog"
    }

    private var _binding: BsdFilterBinding? = null
    private val binding get() = _binding as BsdFilterBinding

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupHeight(bottomSheetDialog)
        }
        return dialog
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bsd_filter_reset_to_default -> viewmodel.resetSelectedToFault()
            R.id.bsd_filter_apply -> {
                viewmodel.performFilter()
                this.dismiss()
            }
            R.id.bsd_filter_tag_edit -> showTagSelectionDialog()
        }
    }

    private fun setupHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        behavior.peekHeight = windowHeight * 80 / 100
        if (layoutParams != null) {
            layoutParams.height = windowHeight * 80 / 100
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun getWindowHeight(): Int = requireActivity().window.decorView.height

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
        val dialog = TagSelectionDialogFragment(viewmodel)
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