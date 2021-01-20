package com.mobile.ta.ui.feedback

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentFeedbackBinding
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.utils.text

class FeedbackFragment : Fragment() {

    private lateinit var binding: FragmentFeedbackBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        with(binding) {
            buttonSubmitFeedback.setOnClickListener {
                submitFeedback(editTextFeedback.text())
            }
            editTextFeedback.doOnTextChanged { _, _, _, _ ->
                buttonSubmitFeedback.isEnabled =
                    editTextFeedback.notBlankValidate(Constants.FEEDBACK)
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setupAutoComplete()
    }

    private fun setupAutoComplete() {
        val categories = resources.getStringArray(R.array.feedback_category)
        context?.let { context ->
            val adapter = ArrayAdapter(context, R.layout.layout_simple_list_item, categories)
            binding.autoCompleteFeedbackCategory.setAdapter(adapter)
            adapter.notifyDataSetChanged()
        }
    }

    private fun submitFeedback(feedback: String) {
        Snackbar.make(binding.root, "Feedback sent!", Snackbar.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }
}