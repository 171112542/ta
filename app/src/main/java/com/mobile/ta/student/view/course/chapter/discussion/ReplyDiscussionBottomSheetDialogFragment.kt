package com.mobile.ta.student.view.course.chapter.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.databinding.FragmentReplyDiscussionBottomSheetBinding

class ReplyDiscussionBottomSheetDialogFragment : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {
        fun newInstance(onSubmitListener: (String) -> Unit) =
            ReplyDiscussionBottomSheetDialogFragment().apply {
                this.onSubmitListener = onSubmitListener
            }
    }

    private var _binding: FragmentReplyDiscussionBottomSheetBinding? = null
    private val binding get() = _binding!!

    private lateinit var onSubmitListener: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentReplyDiscussionBottomSheetBinding.inflate(inflater, container, false)
        with(binding) {
            buttonSubmitDiscussionForum.setOnClickListener(this@ReplyDiscussionBottomSheetDialogFragment)
            editTextDiscussionAnswer.doOnTextChanged { _, _, _, _ ->
                buttonSubmitDiscussionForum.isEnabled =
                    editTextDiscussionAnswer.notBlankValidate(Constants.REPLY)
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        binding.apply {
            when (view) {
                buttonSubmitDiscussionForum -> submitReply()
            }
        }
    }

    private fun submitReply() {
        onSubmitListener.invoke(binding.editTextDiscussionAnswer.text())
        dismiss()
    }
}