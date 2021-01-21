package com.mobile.ta.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentReplyDiscussionBottomSheetBinding
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.utils.text

class ReplyDiscussionBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(onSubmitListener: (String) -> Unit) =
            ReplyDiscussionBottomSheetDialogFragment().apply {
                this.onSubmitListener = onSubmitListener
            }
    }

    private lateinit var binding: FragmentReplyDiscussionBottomSheetBinding
    private lateinit var onSubmitListener: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentReplyDiscussionBottomSheetBinding.inflate(inflater, container, false)
        with(binding) {
            buttonSubmitDiscussionForum.setOnClickListener {
                onSubmitListener.invoke(editTextDiscussionAnswer.text())
                dismiss()
            }
            editTextDiscussionAnswer.doOnTextChanged { _, _, _, _ ->
                buttonSubmitDiscussionForum.isEnabled =
                    editTextDiscussionAnswer.notBlankValidate(Constants.REPLY)
            }
        }
        return binding.root
    }
}