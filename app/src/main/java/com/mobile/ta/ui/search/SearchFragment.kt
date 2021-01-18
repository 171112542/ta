package com.mobile.ta.ui.search

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mobile.ta.R
import com.mobile.ta.adapter.CourseOverviewAdapter
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.data.CourseOverviewData
import com.mobile.ta.databinding.FragSearchBinding
import com.mobile.ta.ui.RVSeparator
import com.mobile.ta.viewmodel.SearchViewModel


class SearchFragment :
    Fragment(),
    View.OnClickListener {
    companion object {
        private const val FILTER_TAG = "FILTER_TAG"
    }

    private var _binding: FragSearchBinding? = null
    private val binding get() = _binding as FragSearchBinding
    private val viewmodel by viewModels<SearchViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?)
    : View {
        _binding = FragSearchBinding.inflate(inflater, container, false)
        binding.fragSearchSearchResultFilter.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchBar()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frag_search_search_result_filter -> showFilterDialog()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchBar() {
        binding.fragSearchSearchBar.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val keyword = binding.fragSearchSearchBar.text.toString()
                    viewmodel.performSearch(keyword)
                    hideKeyboard()
                    return true
                }
                return false
            }
        })
        binding.fragSearchSearchBar.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_RIGHT = 2
            if (event.action == MotionEvent.ACTION_UP) {
                val rightBoundOfDrawableLeft = binding.fragSearchSearchBar.compoundDrawables[DRAWABLE_LEFT].bounds.right
                val drawableRightWidth = binding.fragSearchSearchBar.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
                val leftBoundOfSearchBar = binding.fragSearchSearchBar.left
                val searchBarWidth = binding.fragSearchSearchBar.width
                val paddingDrawable = binding.fragSearchSearchBar.compoundDrawablePadding
                if (event.x <= rightBoundOfDrawableLeft + paddingDrawable) {
                    findNavController().navigateUp()
                    return@OnTouchListener true
                } else if (event.x >= searchBarWidth - leftBoundOfSearchBar - drawableRightWidth - paddingDrawable) {
                    val keyword = binding.fragSearchSearchBar.text.toString()
                    viewmodel.performSearch(keyword)
                    hideKeyboard()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun setupRecyclerView() {
        val diffCallback = CourseOverviewDiffCallback()
        val adapter = CourseOverviewAdapter(diffCallback)
        with(binding.fragSearchSearchResultRv) {
            this.adapter = adapter
            addItemDecoration(
                RVSeparator.getSpaceSeparator(
                    context,
                    LinearLayoutManager.VERTICAL,
                    resources
                )
            )
        }
    }

    private fun showFilterDialog() {
        val dialog = FilterBottomSheetDialogFragment.newInstance()
        dialog.show(parentFragmentManager, FILTER_TAG)
    }

    private fun observeViewModel() {
        viewmodel.hasSearched.observe(viewLifecycleOwner, {
            binding.fragSearchNoSearchGroup.visibility = if (it) View.GONE else View.VISIBLE
        })

        viewmodel.searchResult.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.fragSearchNoSearchResultGroup.visibility = View.VISIBLE
                binding.fragSearchSearchResultGroup.visibility = View.GONE
            } else {
                binding.fragSearchNoSearchResultGroup.visibility = View.GONE
                binding.fragSearchSearchResultGroup.visibility = View.VISIBLE
                if (binding.fragSearchSearchResultRv.adapter is CourseOverviewAdapter) {
                    (binding.fragSearchSearchResultRv.adapter as CourseOverviewAdapter)
                        .submitList(it)
                }
            }
        })
    }

    private fun hideKeyboard() {
        val inputMethodManager: InputMethodManager = requireActivity()
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken, 0)
    }
}