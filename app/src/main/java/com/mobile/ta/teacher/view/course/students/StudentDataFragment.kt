package com.mobile.ta.teacher.view.course.students

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.ta.databinding.TFragStudentDataBinding
import com.mobile.ta.teacher.adapter.course.students.StudentProgressAdapter
import com.mobile.ta.teacher.viewmodel.course.studentData.StudentDataViewModel
import com.mobile.ta.ui.view.base.BaseFragment
import com.mobile.ta.utils.view.DividerItemDecorator
import com.mobile.ta.utils.view.RVSeparator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class StudentDataFragment
    : BaseFragment<TFragStudentDataBinding>(TFragStudentDataBinding::inflate) {
    @Inject
    lateinit var studentProgressAdapter: StudentProgressAdapter
    private val viewModel by viewModels<StudentDataViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchBar()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearchBar() {
        binding.tFragStudentDataSearchBar.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    val keyword = binding.tFragStudentDataSearchBar.text.toString()
                    viewModel.performSearch(keyword)
                    hideKeyboard()
                    return true
                }
                return false
            }
        })
        binding.tFragStudentDataSearchBar.setOnTouchListener(View.OnTouchListener { _, event ->
            val DRAWABLE_LEFT = 0
            if (event.action == MotionEvent.ACTION_UP) {
                val rightBoundOfDrawableLeft =
                    binding.tFragStudentDataSearchBar.compoundDrawables[DRAWABLE_LEFT].bounds.right
                val paddingDrawable = binding.tFragStudentDataSearchBar.compoundDrawablePadding
                if (event.x <= rightBoundOfDrawableLeft + paddingDrawable) {
                    val keyword = binding.tFragStudentDataSearchBar.text.toString()
                    viewModel.performSearch(keyword)
                    hideKeyboard()
                    return@OnTouchListener true
                }
            }
            false
        })
    }

    private fun setupRecyclerView() {
        binding.tFragStudentDataRv.adapter = studentProgressAdapter
        binding.tFragStudentDataRv.addItemDecoration(
            DividerItemDecorator(requireContext(), LinearLayoutManager.VERTICAL)
        )
        viewModel.studentProgressData.observe(viewLifecycleOwner, {
            binding.tFragStudentDataLoading.visibility = View.GONE
            binding.tFragStudentDataSearchBar.visibility = View.VISIBLE
            binding.tFragStudentDataTable.visibility = View.VISIBLE
        })
        viewModel.searchedStudentProgressData.observe(viewLifecycleOwner, {
            studentProgressAdapter.submitList(it)
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