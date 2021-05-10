package com.mobile.ta.ui.user.feedback

import android.view.View
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.mobile.ta.R
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentFeedbackBinding
import com.mobile.ta.ui.base.BaseFragment
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.utils.text
import com.mobile.ta.viewmodel.user.feedback.FeedbackViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedbackFragment : BaseFragment<FragmentFeedbackBinding>(FragmentFeedbackBinding::inflate),
    View.OnClickListener {

    private val viewModel: FeedbackViewModel by viewModels()

    override fun runOnCreateView() {
        binding.apply {
            buttonSubmitFeedback.setOnClickListener(this@FeedbackFragment)
            editTextFeedback.doOnTextChanged { _, _, _, _ ->
                buttonSubmitFeedback.isEnabled =
                    editTextFeedback.notBlankValidate(Constants.FEEDBACK)
            }
        }
        setupObserver()
    }

    override fun onStart() {
        super.onStart()
        setupAutoComplete()
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                buttonSubmitFeedback -> viewModel.addFeedback(
                    binding.autoCompleteFeedbackCategory.text(),
                    editTextFeedback.text()
                )
            }
        }
    }

    private fun onFeedbackSent(isSent: Boolean) {
        if (isSent) {
            showToastWithCloseAction(R.string.feedback_sent_message)
            findNavController().navigateUp()
        } else {
            showToast(R.string.feedback_not_sent_message)
        }
    }

    private fun setupObserver() {
        viewModel.fetchUserData()
        viewModel.isFeedbackSent.observe(viewLifecycleOwner, { isSent ->
            onFeedbackSent(isSent)
        })
    }

    private fun setupAutoComplete() {
        val categories = resources.getStringArray(R.array.feedback_category)
        val adapter = ArrayAdapter(mContext, R.layout.layout_simple_list_item, categories)
        binding.autoCompleteFeedbackCategory.setAdapter(adapter)
        adapter.notifyDataSetChanged()
    }
}