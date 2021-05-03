package com.mobile.ta.ui.course.chapter.discussion

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

class CreateDiscussionForumBottomSheetDialogFragment : BottomSheetDialogFragment(),
    View.OnClickListener {

    companion object {
        fun newInstance(onSubmitListener: (String, String) -> Unit) =
            CreateDiscussionForumBottomSheetDialogFragment().apply {
                this.onSubmitListener = onSubmitListener
            }
    }

    private var _binding: FragmentCreateDiscussionForumBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var onSubmitListener: (String, String) -> Unit

    private var discussionTitle: String? = null
    private var discussionQuestion: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentCreateDiscussionForumBottomSheetBinding.inflate(inflater, container, false)
        with(binding) {
            buttonSubmitDiscussionForum.setOnClickListener(this@CreateDiscussionForumBottomSheetDialogFragment)
            editTextDiscussionTitle.doOnTextChanged { text, _, _, _ ->
                discussionTitle = text.toString()
                if (editTextDiscussionTitle.notBlankValidate(Constants.TITLE)) {
                    validate()
                }
            }
            editTextDiscussionQuestion.doOnTextChanged { text, _, _, _ ->
                discussionQuestion = text.toString()
                if (editTextDiscussionQuestion.notBlankValidate(Constants.QUESTION)) {
                    validate()
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                buttonSubmitDiscussionForum -> submitDiscussion()
            }
        }
    }

    private fun submitDiscussion() {
        onSubmitListener.invoke(discussionTitle.orEmpty(), discussionQuestion.orEmpty())
        dismiss()
    }

    private fun validate() {
        binding.buttonSubmitDiscussionForum.isEnabled = discussionTitle.isNotNullOrBlank()
            && discussionQuestion.isNotNullOrBlank()
    }
}