package com.mobile.ta.student.view.course.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.databinding.FragmentInputEnrollmentKeyBottomSheetBinding

class InputEnrollmentKeyBottomSheetDialogFragment : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(onSubmitListener: (String) -> Unit) =
            InputEnrollmentKeyBottomSheetDialogFragment().apply {
                this.onSubmitListener = onSubmitListener
            }
    }

    private lateinit var binding: FragmentInputEnrollmentKeyBottomSheetBinding
    private lateinit var onSubmitListener: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputEnrollmentKeyBottomSheetBinding.inflate(inflater, container, false)
        with(binding) {
            buttonCloseDialog.setOnClickListener {
                dismiss()
            }
            buttonSubmitEnrollmentKey.setOnClickListener {
                onSubmitListener.invoke(editTextInputEnrollment.text.toString())
                dismiss()
            }
            editTextInputEnrollment.doOnTextChanged { _, _, _, _ ->
                buttonSubmitEnrollmentKey.isEnabled = editTextInputEnrollment.notBlankValidate(
                    Constants.ENROLLMENT_KEY
                )
            }
        }
        return binding.root
    }
}