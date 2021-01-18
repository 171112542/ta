package com.mobile.ta.ui.search

import android.app.Dialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
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


class FilterBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance() = FilterBottomSheetDialogFragment()
    }

    private var _binding: BsdFilterBinding? = null
    private val binding get() = _binding as BsdFilterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BsdFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTagRecyclerView()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupHeight(bottomSheetDialog)
        }
        return dialog
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

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        return requireActivity().window.decorView.height
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
        tagAdapter.submitList(arrayListOf("Test 1", "Test 2"))
    }
}