package com.mobile.ta.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.adapter.CourseOverviewAdapter
import com.mobile.ta.adapter.diff.CourseOverviewDiffCallback
import com.mobile.ta.data.CourseOverviewData
import com.mobile.ta.databinding.FragSearchBinding


class SearchFragment : Fragment() {
    private var _binding: FragSearchBinding? = null
    private val binding get() = _binding as FragSearchBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?)
    : View {
        _binding = FragSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSearchBar()
        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchBar() {
        binding.fragSearchSearchBar.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val keyword = binding.fragSearchSearchBar.text.toString()
                    performSearch(keyword)
                    hideKeyboard()
                    return true
                }
                return false
            }
        })
        binding.fragSearchSearchBar.setOnTouchListener(OnTouchListener { _, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                val rightBoundOfDrawableLeft = binding.fragSearchSearchBar.compoundDrawables[DRAWABLE_LEFT].bounds.right
                val paddingDrawable = binding.fragSearchSearchBar.compoundDrawablePadding
                if (event.x <= rightBoundOfDrawableLeft + paddingDrawable) {
                    findNavController().navigateUp()
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
            addItemDecoration(RVSeparator.getSpaceSeparator(
                    context,
                    LinearLayoutManager.VERTICAL,
                    resources
            ))
        }
        adapter.submitList(CourseOverviewData.data.toMutableList())
    }

    private fun performSearch(keyword: String) {
        if (keyword == "nothing") {
            binding.fragSearchNoSearchGroup.visibility = View.GONE
            binding.fragSearchNoSearchResultGroup.visibility = View.VISIBLE
            binding.fragSearchSearchResultGroup.visibility = View.GONE
        }
        else {
            binding.fragSearchNoSearchGroup.visibility = View.GONE
            binding.fragSearchNoSearchResultGroup.visibility = View.GONE
            binding.fragSearchSearchResultGroup.visibility = View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager: InputMethodManager = requireActivity()
                .getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                (requireActivity().currentFocus as View).windowToken, 0)
    }
}