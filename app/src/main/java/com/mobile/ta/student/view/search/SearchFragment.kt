package com.mobile.ta.student.view.search

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.R
import com.mobile.ta.databinding.FragSearchBinding
import com.mobile.ta.student.adapter.course.CourseAdapter
import com.mobile.ta.student.adapter.course.CourseVHListener
import com.mobile.ta.student.adapter.diff.CourseDiffCallback
import com.mobile.ta.student.viewmodel.search.SearchViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.isNull
import com.mobile.ta.utils.view.RVSeparator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchFragment :
    BaseFragment<FragSearchBinding>(FragSearchBinding::inflate),
    View.OnClickListener,
    CourseVHListener {
    companion object {
        private const val FILTER_BSD_FRAGMENT = "filter_bsd_fragment"
    }
    private val viewmodel by viewModels<SearchViewModel>()

    override fun runOnCreateView() {
        binding.fragSearchedFilter.setOnClickListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchBar()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.frag_searched_filter -> showFilterDialog()
        }
    }

    override fun onClickListener(courseId: String) {
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToCourseInformationFragment(
                courseId = courseId
            )
        )
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
        val diffCallback = CourseDiffCallback()
        val adapter = CourseAdapter(diffCallback, this)
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
        fun setOnlyThisAsVisible(visibleView: View) {
            binding.fragSearchLoading.visibility = View.GONE
            binding.fragFilteredNotFoundGroup.visibility = View.GONE
            binding.fragSearchNotFoundGroup.visibility = View.GONE
            binding.fragSearchFoundGroup.visibility = View.GONE
            binding.fragSearchNoSearchGroup.visibility = View.GONE
            visibleView.visibility = View.VISIBLE
        }

        viewmodel.isSearching.observe(viewLifecycleOwner, {
            if (it.isNull()) return@observe
            if (it) {
                setOnlyThisAsVisible(binding.fragSearchLoading)
            }
            else {
                setOnlyThisAsVisible(binding.fragSearchedGroup)
                binding.fragSearchedLabel.text =
                    getString(R.string.searched_desc_text, viewmodel.keyword)
            }
        })

        viewmodel.isFiltered.observe(viewLifecycleOwner, {
            binding.fragFilteredGroup.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewmodel.filteredSearchResult.observe(viewLifecycleOwner, {
            if (viewmodel.isSearching.value.isNull()) return@observe
            if (it.isEmpty() && viewmodel.isFiltered.value == false) {
                setOnlyThisAsVisible(binding.fragSearchNotFoundGroup)
            } else if (it.isEmpty() && viewmodel.isFiltered.value == true) {
                setOnlyThisAsVisible(binding.fragFilteredNotFoundGroup)
            } else {
                setOnlyThisAsVisible(binding.fragSearchFoundGroup)
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