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
import com.mobile.ta.R
import com.mobile.ta.adapter.course.CourseAdapter
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.databinding.FragSearchBinding
import com.mobile.ta.ui.RVSeparator
import com.mobile.ta.viewmodel.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchFragment :
    Fragment(),
    View.OnClickListener {
    companion object {
        private const val FILTER_BSD_FRAGMENT = "filter_bsd_fragment"
    }

    private var _binding: FragSearchBinding? = null
    private val binding get() = _binding as FragSearchBinding
    private val viewmodel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )
        : View {
        _binding = FragSearchBinding.inflate(inflater, container, false)
        binding.fragSearchedFilter.setOnClickListener(this)
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
            R.id.frag_searched_filter -> showFilterDialog()
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
                val rightBoundOfDrawableLeft =
                    binding.fragSearchSearchBar.compoundDrawables[DRAWABLE_LEFT].bounds.right
                val drawableRightWidth =
                    binding.fragSearchSearchBar.compoundDrawables[DRAWABLE_RIGHT].bounds.width()
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
        val adapter = CourseAdapter(diffCallback)
        adapter.setParentFragment(this)
        with(binding.fragSearchFoundResultRv) {
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
        dialog.show(childFragmentManager, FILTER_BSD_FRAGMENT)
    }

    private fun observeViewModel() {
        viewmodel.hasSearched.observe(viewLifecycleOwner, {
            binding.fragSearchNoSearchGroup.visibility = if (it) View.GONE else View.VISIBLE
            binding.fragSearchedGroup.visibility = if (it) View.VISIBLE else View.GONE
            binding.fragSearchedLabel.text =
                getString(R.string.searched_desc_text, viewmodel.keyword)
        })

        viewmodel.hasFiltered.observe(viewLifecycleOwner, {
            binding.fragFilteredGroup.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewmodel.filteredSearchResult.observe(viewLifecycleOwner, {
            if (viewmodel.hasSearched.value == false) return@observe
            if (it.isEmpty() && viewmodel.hasFiltered.value == false) {
                binding.fragFilteredNotFoundGroup.visibility = View.GONE
                binding.fragSearchNotFoundGroup.visibility = View.VISIBLE
                binding.fragSearchFoundGroup.visibility = View.GONE
            } else if (it.isEmpty() && viewmodel.hasFiltered.value == true) {
                binding.fragFilteredNotFoundGroup.visibility = View.VISIBLE
                binding.fragSearchNotFoundGroup.visibility = View.GONE
                binding.fragSearchFoundGroup.visibility = View.GONE
            } else {
                binding.fragFilteredNotFoundGroup.visibility = View.GONE
                binding.fragSearchNotFoundGroup.visibility = View.GONE
                binding.fragSearchFoundGroup.visibility = View.VISIBLE
                if (binding.fragSearchFoundResultRv.adapter is CourseAdapter) {
                    (binding.fragSearchFoundResultRv.adapter as CourseAdapter)
                        .submitList(it)
                }
            }
        })
    }

    private fun hideKeyboard() {
        val inputMethodManager: InputMethodManager = requireActivity()
            .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken, 0
        )
    }
}