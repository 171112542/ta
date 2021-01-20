package com.mobile.ta.ui.courseInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobile.ta.config.Constants
import com.mobile.ta.databinding.FragmentInputEnrollmentKeyBottomSheetBinding
import com.mobile.ta.utils.notBlankValidate
import com.mobile.ta.utils.text

class InputEnrollmentKeyBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(onSubmitListener: (String) -> Unit) = InputEnrollmentKeyBottomSheetDialogFragment().apply {
            this.onSubmitListener = onSubmitListener
        }
    }

    private lateinit var binding: FragmentInputEnrollmentKeyBottomSheetBinding
    private lateinit var onSubmitListener: (String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentInputEnrollmentKeyBottomSheetBinding.inflate(inflater, container, false)
        with(binding) {
            buttonCloseDialog.setOnClickListener {
                dismiss()
            }
            buttonSubmitEnrollmentKey.setOnClickListener {
                onSubmitListener.invoke(editTextInputEnrollment.text())
                dismiss()
            }
            editTextInputEnrollment.doOnTextChanged { _, _, _, _ ->
                buttonSubmitEnrollmentKey.isEnabled = editTextInputEnrollment.notBlankValidate(
                    Constants.ENROLLMENT_KEY)
            }
        }
        return binding.root
    }
}