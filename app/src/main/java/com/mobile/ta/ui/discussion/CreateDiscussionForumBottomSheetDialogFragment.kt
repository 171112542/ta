package com.mobile.ta.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentCreateDiscussionForumBottomSheetBinding
import com.mobile.ta.utils.isNotNullOrBlank
import com.mobile.ta.utils.notBlankValidate

class CreateDiscussionForumBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(onSubmitListener: (String, String) -> Unit) =
            CreateDiscussionForumBottomSheetDialogFragment().apply {
                this.onSubmitListener = onSubmitListener
            }
    }

    private lateinit var binding: FragmentCreateDiscussionForumBottomSheetBinding
    private lateinit var onSubmitListener: (String, String) -> Unit

    private var discussionTitle: String? = null
    private var discussionQuestion: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentCreateDiscussionForumBottomSheetBinding.inflate(inflater, container, false)
        with(binding) {
            buttonSubmitDiscussionForum.setOnClickListener {
                onSubmitListener.invoke(discussionTitle.orEmpty(), discussionQuestion.orEmpty())
                dismiss()
            }
            editTextDiscussionTitle.doOnTextChanged { text, _, _, _ ->
                discussionTitle = text.toString()
                if (editTextDiscussionTitle.notBlankValidate(Constants.TITLE).not()) {
                    validate()
                }
            }
            editTextDiscussionQuestion.doOnTextChanged { text, _, _, _ ->
                discussionQuestion = text.toString()
                if (editTextDiscussionQuestion.notBlankValidate(Constants.QUESTION).not()) {
                    validate()
                }
            }
        }
        return binding.root
    }

    private fun validate() {
        binding.buttonSubmitDiscussionForum.isEnabled = discussionTitle.isNotNullOrBlank()
                && discussionQuestion.isNotNullOrBlank()
    }
}